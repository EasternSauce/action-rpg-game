package com.easternsauce.game.creature

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.physics.box2d._
import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.attack._
import com.easternsauce.game.ability.attack.util.Attack
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.area.Area
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up, WalkDirection}
import com.easternsauce.game.creature.util._
import com.easternsauce.game.effect.Effect
import com.easternsauce.game.item.Item
import com.easternsauce.game.utils.{EsIntPair, EsTimer}
import com.easternsauce.game.wrappers.{EsAnimation, EsSpriteSheet}
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

abstract class Creature protected (val id: String) extends Ordered[Creature] {

  val scale: Float = 1f
  val spriteWidth: Float = 64
  val spriteHeight: Float = 64
  val hitboxBounds: Rectangle = new Rectangle(2, 2, 60, 60)
  val isPlayer = false
  val isMob = false
  val isNPC = false
  val baseSpeed: Float = 12f
  val dirMap = Map(Left -> 1, Right -> 2, Up -> 3, Down -> 0)
  val mass: Float = 200f
  protected val onGettingHitSound: Sound = null
  protected val walkAnimationFrameDuration = 0.1f
  protected val walkAnimationTimer: EsTimer = EsTimer()
  var passedGateRecently = false
  var area: Area = _
  var attackVector: Vector2 = new Vector2(0f, 0f)
  var facingVector: Vector2 = new Vector2(0f, 0f)
  var maxHealthPoints = 100f
  var healthPoints: Float = maxHealthPoints
  val maxStaminaPoints = 100f
  var staminaPoints: Float = maxStaminaPoints
  var isAttacking = false
  var pendingArea: Area = _
  var pendingX = 0f
  var pendingY = 0f
  var unarmedDamage = 15f
  var startingPosX: Float = 0f
  var startingPosY: Float = 0f
  var staminaOveruse = false
  var name: String = _
  var isBoss: Boolean = false
  var sprinting = false
  val equipmentItems: mutable.Map[Int, Item] = mutable.Map()
  val movementVector: Vector2 = new Vector2(0f, 0f)
  var lastMovingDir: WalkDirection = WalkDirection.Down
  var abilityList: mutable.ListBuffer[Ability] = _
  var attackList: mutable.ListBuffer[Attack] = _
  var currentMaxVelocity: Float = _
  var body: Body = _
  var fixture: Fixture = _
  var toSetBodyNonInteractive: Boolean = false
  var aggroedCreature: Option[Creature] = None
  protected val healthRegen = 0.3f
  protected val staminaRegen = 3f
  protected val staminaOveruseTime = 1.3f
  protected val poisonTickTime = 1.5f
  protected val poisonTime = 20f
  protected val knockbackPower = 0f
  protected var healing = false
  protected val healingTickTime = 0.3f
  protected val healingTime = 8f
  protected var healingPower = 0f
  protected var knockbackVector: Vector2 = _
  protected var knockbackSpeed: Float = 0f
  protected var knockbackable = true
  protected val dropTable: mutable.Map[String, Float] = mutable.Map()
  protected var creatureType: String = _
  protected val effectMap: mutable.Map[String, Effect] = mutable.Map()
  protected var staminaDrain = 0.0f
  protected val movingDir: EsIntPair = EsIntPair(0, 0)
  protected var isMoving = false
  protected var wasMoving = false
  protected var totalDirections = 0
  protected var movementIncrement: Float = 0
  protected val runningStoppedTimer: EsTimer = EsTimer()
  protected var neutralPositionIndex: Int = _
  protected var isRunningAnimationActive = false
  protected val walkAnimation: mutable.Map[WalkDirection, EsAnimation] =
    mutable.Map()
  protected var bowAttack: BowAttack = _
  protected var unarmedAttack: UnarmedAttack = _
  protected var swordAttack: SwordAttack = _
  protected var tridentAttack: TridentAttack = _
  protected val healthRegenTimer: EsTimer = EsTimer(true)
  protected val staminaRegenTimer: EsTimer = EsTimer(true)
  protected val poisonTickTimer: EsTimer = EsTimer()
  protected val staminaOveruseTimer: EsTimer = EsTimer()
  protected val healingTimer: EsTimer = EsTimer()
  protected val healingTickTimer: EsTimer = EsTimer()
  protected var knocbackable = true

  def atFullLife: Boolean = healthPoints >= maxHealthPoints

  def weaponDamage: Float = {
    if (equipmentItems.contains(0)) equipmentItems(0).damage else unarmedDamage
  }

  def currentAttackType: AttackType = {
    if (equipmentItems.contains(0)) {
      equipmentItems(0).itemType.attackType
    } else {
      Unarmed
    }
  }

  def update(): Unit = {
    if (isAlive) {
      onUpdateStart()

      for (ability <- abilityList) {
        ability.performOnUpdateStart()
      }
      currentAttack.performOnUpdateStart()

      performActions()

      controlMovement()
      processMovement()

      setFacingDirection()

      regenerate()
    }

    for (effect <- effectMap.values) {
      effect.update()
    }

    for (ability <- abilityList) {
      ability.update()
    }

    currentAttack.update()

    if (staminaDrain >= 0.3f) {
      takeStaminaDamage(11f)

      staminaDrain = 0.0f
    }

    if (
      GameSystem.cameraFocussedCreature.nonEmpty
      && this == GameSystem.cameraFocussedCreature.get
    ) {
      GameSystem.adjustCamera(this)
    }

    if (toSetBodyNonInteractive) {
      fixture.setSensor(true)
      body.setType(BodyDef.BodyType.StaticBody)
      toSetBodyNonInteractive = false
    }
  }

  def setFacingDirection(): Unit = {}

  def regenerate(): Unit = {
    if (healthRegenTimer.time > 0.5f) {
      heal(healthRegen)
      healthRegenTimer.restart()
    }

    if (!isEffectActive("staminaRegenStopped") && !sprinting)
      if (staminaRegenTimer.time > 0.05f && !abilityActive && !staminaOveruse) {
        if (staminaPoints < maxStaminaPoints) {
          val afterRegen = staminaPoints + staminaRegen
          staminaPoints = Math.min(afterRegen, maxStaminaPoints)
        }
        staminaRegenTimer.restart()
      }

    if (staminaOveruse)
      if (staminaOveruseTimer.time > staminaOveruseTime) staminaOveruse = false

    if (getEffect("poisoned").isActive)
      if (poisonTickTimer.time > poisonTickTime) {
        takeDamage(15f, immunityFrames = false)
        poisonTickTimer.restart()
      }

    if (healing) {
      if (healingTickTimer.time > healingTickTime) {
        heal(healingPower)
        healingTickTimer.restart()
      }
      if (healingTimer.time > healingTime || healthPoints >= maxHealthPoints)
        healing = false
    }
  }

  def takeDamage(
    damage: Float,
    immunityFrames: Boolean,
    knockbackPower: Float = 0,
    sourceX: Float = 0,
    sourceY: Float = 0
  ): Unit = {
    if (isAlive) {
      val beforeHP = healthPoints

      val actualDamage = damage * 100f / (100f + totalArmor)

      if (healthPoints - actualDamage > 0) healthPoints -= actualDamage
      else healthPoints = 0f

      if (beforeHP != healthPoints && healthPoints == 0f) onDeath()

      if (immunityFrames) { // immunity frames on hit
        getEffect("immune").applyEffect(0.75f)
        // stagger on hit
        getEffect("immobilized").applyEffect(0.35f)
      }

      if (knocbackable) {
        knockbackVector = new Vector2(posX - sourceX, posY - sourceY).nor()

        body.applyLinearImpulse(
          new Vector2(knockbackVector.x * knockbackPower, knockbackVector.y * knockbackPower),
          body.getWorldCenter,
          true
        )
      }

      onGettingHitSound.play(0.1f)
    }
  }

  def totalArmor: Float = {
    var totalArmor = 0.0f
    for ((_, value) <- equipmentItems) {
      if (value != null && value.itemType.maxArmor != null.asInstanceOf[Float])
        totalArmor += value.itemType.maxArmor
    }
    totalArmor
  }

  def onDeath(): Unit = {
    isRunningAnimationActive = false

    for (ability <- abilityList) {
      ability.forceStop()
    }
    currentAttack.forceStop()
  }

  def abilityActive: Boolean = {
    var abilityActive = false

    for (ability <- abilityList) {
      if (!abilityActive && ability.active) {
        abilityActive = true

      }
    }

    if (currentAttack.active) return true

    abilityActive
  }

  def takeStaminaDamage(staminaDamage: Float): Unit = {
    if (staminaPoints - staminaDamage > 0) staminaPoints -= staminaDamage
    else {
      staminaPoints = 0f
      staminaOveruse = true
      staminaOveruseTimer.restart()
    }
  }

  def onUpdateStart(): Unit = {
    isMoving = false

    totalDirections = 0

    knockbackSpeed = knockbackPower * Gdx.graphics.getDeltaTime

    movingDir.x = 0
    movingDir.y = 0

    currentMaxVelocity = this.baseSpeed

    if (isAttacking) currentMaxVelocity = currentMaxVelocity / 2
    else if (sprinting && staminaPoints > 0) {
      currentMaxVelocity = currentMaxVelocity * 1.75f
      staminaDrain += Gdx.graphics.getDeltaTime
    }
  }

  def processMovement(): Unit = {

    assert(GameSystem.currentArea.nonEmpty)

    if (!isEffectActive("immobilized")) {

      if (totalDirections > 1)
        currentMaxVelocity = currentMaxVelocity / Math.sqrt(2).toFloat

      val impulseValue = 5 * mass

      if (movingDir.x == -1) {
        if (body.getLinearVelocity.x >= -currentMaxVelocity) {
          body.applyLinearImpulse(new Vector2(impulseValue * movingDir.x, 0), body.getWorldCenter, true)
        }
      } else if (movingDir.x == 1) {
        if (body.getLinearVelocity.x <= currentMaxVelocity) {
          body.applyLinearImpulse(new Vector2(impulseValue * movingDir.x, 0), body.getWorldCenter, true)
        }
      }

      movementVector.x = movingDir.x

      if (movingDir.y == -1) {
        if (body.getLinearVelocity.y >= -currentMaxVelocity) {
          body.applyLinearImpulse(new Vector2(0, impulseValue * movingDir.y), body.getWorldCenter, true)
        }
      } else if (movingDir.y == 1) {
        if (body.getLinearVelocity.y <= currentMaxVelocity) {
          body.applyLinearImpulse(new Vector2(0, impulseValue * movingDir.y), body.getWorldCenter, true)
        }
      }

      movementVector.y = movingDir.y

    }

    if (isMoving && !wasMoving) if (!isRunningAnimationActive) {

      isRunningAnimationActive = true
      walkAnimationTimer.restart()
    }

    if (!isMoving && wasMoving) runningStoppedTimer.restart()

    if (!isMoving && isRunningAnimationActive && runningStoppedTimer.time > 0.25f) {
      isRunningAnimationActive = false
      runningStoppedTimer.stop()
      walkAnimationTimer.stop()
    }

    wasMoving = isMoving

    for (ability <- abilityList) {
      ability.performMovement()
    }

    currentAttack.performMovement()
  }

  def controlMovement(): Unit = {}

  def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    drawRunningAnimation(batch)

    abilityList.foreach(ability => ability.render(shapeDrawer, batch))
    currentAttack.render(shapeDrawer, batch)
  }

  def drawRunningAnimation(batch: SpriteBatch): Unit = {
    val realWidth = spriteWidth * scale
    val realHeight = spriteHeight * scale

    val currentFrame =
      if (isRunningAnimationActive) walkAnimation(lastMovingDir).currentFrame
      else walkAnimation(lastMovingDir).getFrameByIndex(neutralPositionIndex)

    val rotation = if (isAlive) 0f else 90f

    if (isAlive && isImmune) {
      val alpha = effectMap("immune").getRemainingTime * 50f
      batch.setColor(1, 0.1f, 0.1f, 5f * Math.sin(alpha).toFloat + 0.5f)
    }

    batch.draw(
      currentFrame,
      posX - realWidth / 2,
      posY - realHeight / 2,
      realWidth / 2,
      realHeight / 2,
      realWidth,
      realHeight,
      1.0f,
      1.0f,
      rotation
    )

    batch.setColor(1, 1, 1, 1)
  }

  def isAlive: Boolean = healthPoints > 0f

  def renderHealthBar(shapeDrawer: ShapeDrawer): Unit = {
    val healthBarHeight = 5
    val healthBarWidth = 50
    val currentHealthBarWidth = healthBarWidth * healthPoints / maxHealthPoints
    val barPosX = posX - healthBarWidth / 2
    val barPosY = posY + spriteHeight / 2 + 10
    shapeDrawer.filledRectangle(new Rectangle(barPosX, barPosY, healthBarWidth, healthBarHeight), Color.ORANGE)
    shapeDrawer.filledRectangle(new Rectangle(barPosX, barPosY, currentHealthBarWidth, healthBarHeight), Color.RED)

  }

  def posX: Float = body.getPosition.x * GameSystem.PixelsPerMeter

  def posY: Float = body.getPosition.y * GameSystem.PixelsPerMeter

  def performActions(): Unit

  def renderAbilities(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    for (ability <- abilityList) {
      ability.render(shapeDrawer, batch)
    }
    currentAttack.render(shapeDrawer, batch)
  }

  def currentAttack: Ability = {
    if (equipmentItems.contains(0)) {
      equipmentItems(0).itemType.attackType match {
        case Sword   => swordAttack
        case Bow     => bowAttack
        case Trident => tridentAttack
        case _       => throw new RuntimeException("Unrecognized attack type")
      }
    } else {
      unarmedAttack
    }
  }

  def onInit(): Unit = {
    defineEffects()

    defineStandardAbilities()

    defineCustomAbilities()

  }

  def defineStandardAbilities(): Unit = {
    abilityList = ListBuffer()
    attackList = ListBuffer()

    bowAttack = BowAttack(this)
    unarmedAttack = UnarmedAttack(this)
    swordAttack = SwordAttack(this)
    tridentAttack = TridentAttack(this)

    attackList += bowAttack
    attackList += unarmedAttack
    attackList += swordAttack
    attackList += tridentAttack
  }

  protected def defineCustomAbilities(): Unit = {}

  protected def defineEffects(): Unit = {
    effectMap.put("immune", Effect())
    effectMap.put("immobilized", Effect())
    effectMap.put("staminaRegenStopped", Effect())
    effectMap.put("poisoned", Effect())
  }

  def becomePoisoned(): Unit = {
    poisonTickTimer.restart()
    getEffect("poisoned").applyEffect(poisonTime)
  }

  def getEffect(effectName: String): Effect = {
    effectMap.get(effectName) match {
      case Some(effect) => effect
      case _ =>
        throw new RuntimeException("tried to access non-existing effect: " + effectName)
    }
  }

  def kill(): Unit = {
    healthPoints = 0f
  }

  def moveToArea(area: Area, posX: Float, posY: Float): Unit = {
    pendingArea = area
    pendingX = posX
    pendingY = posY
  }

  def useItem(item: Item): Unit = {
    if (item.itemType.id.equals("healingPowder")) startHealing(10f)
  }

  private def startHealing(healingPower: Float): Unit = {
    healingTimer.restart()
    healingTickTimer.restart()
    healing = true
    this.healingPower = healingPower
  }

  def reset(): Unit = {
    healthPoints = maxHealthPoints

    setPos(startingPosX, startingPosY)
  }

  def setPos(x: Float, y: Float): Unit = {
    body.setTransform(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter, 0)
  }

  def onAttack(): Unit = {
    if (equipmentItems.contains(4) && equipmentItems(4) != null && equipmentItems(4).itemType.id == "thiefRing")
      heal(7f)
  }

  def heal(healValue: Float): Unit = {
    if (healthPoints < maxHealthPoints) {
      val afterHeal = healthPoints + healValue
      healthPoints = Math.min(afterHeal, maxHealthPoints)

    }
  }

  def isNoAbilityActive: Boolean = {
    for (ability <- abilityList) {
      if (ability.state != AbilityState.Inactive) return false
    }
    true
  }

  def onAggroed(): Unit = {}

  def isImmune: Boolean = isEffectActive("immune")

  def isEffectActive(effectName: String): Boolean = {
    effectMap.get(effectName) match {
      case Some(effect) => effect.isActive
      case _ =>
        throw new RuntimeException("tried to access non-existing effect: " + effectName)
    }
  }

  def compare(other: Creature): Int = {
    if (healthPoints <= 0.0f) {
      return 1
    }
    if (other.healthPoints <= 0.0f) {
      return -1
    }
    if (posX == other.posY) {
      return 0
    }
    if (posY - other.posY > 0.0f) 1 else -1

  }

  def hitbox: Rectangle =
    new Rectangle(posX + hitboxBounds.x, posY + hitboxBounds.y, hitboxBounds.width, hitboxBounds.height)

  def moveInDirection(dir: WalkDirection): Unit = {
    import com.easternsauce.game.creature.util.WalkDirection._
    dir match {
      case Left =>
        movingDir.x = -1
        isMoving = true
        totalDirections = totalDirections + 1
        lastMovingDir = Left
      case Right =>
        movingDir.x = 1
        isMoving = true
        totalDirections = totalDirections + 1
        lastMovingDir = Right
      case Up =>
        movingDir.y = 1
        isMoving = true
        totalDirections = totalDirections + 1
        lastMovingDir = Up
      case Down =>
        movingDir.y = -1
        isMoving = true
        totalDirections = totalDirections + 1
        lastMovingDir = Down
    }

  }

  def loadSprites(
    spriteSheet: EsSpriteSheet,
    directionalMapping: Map[WalkDirection, Int], // TODO: why is this unused :o and why does it work anyway
    neutralPositionIndex: Int
  ): Unit = {

    this.neutralPositionIndex = neutralPositionIndex

    WalkDirection.values.foreach(dir => {
      walkAnimation(dir) = EsAnimation(spriteSheet, walkAnimationFrameDuration, dirMap(dir))
    })
  }

  def initBody(x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position
      .set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)
    bodyDef.`type` = BodyDef.BodyType.DynamicBody
    body = area.world.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: CircleShape = new CircleShape()
    shape.setRadius(0.9f * spriteWidth * scale / 2 / GameSystem.PixelsPerMeter)
    fixtureDef.shape = shape

    fixture = body.createFixture(fixtureDef)
    val massData = new MassData()
    massData.mass = mass
    body.setMassData(massData)
    body.setLinearDamping(10f)
  }

}
