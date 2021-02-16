package com.easternsauce.game.creature

import java.util
import java.util.List

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.attack.{Attack, BowAttack, SwordAttack, TridentAttack, UnarmedAttack}
import com.easternsauce.game.animation.Animation
import com.easternsauce.game.area.{Area, AreaGate}
import com.easternsauce.game.assets.{Assets, SpriteSheet}
import com.easternsauce.game.creature.player.PlayerCharacter
import com.easternsauce.game.creature.util.WalkDirection
import com.easternsauce.game.creature.util.WalkDirection.WalkDirection
import com.easternsauce.game.effect.Effect
import com.easternsauce.game.item.Item
import com.easternsauce.game.shapes.{CustomBatch, CustomRectangle, CustomVector2}
import com.easternsauce.game.spawn.Blockade
import com.easternsauce.game.utils.{IntPair, Timer}
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

abstract class Creature(val id: String) extends Ordered[Creature] {

  protected var healthRegen = 0.3f
  protected var staminaRegen = 10f

  protected var staminaOveruseTime = 1.3f


  protected var poisonTickTime = 1.5f

  protected var poisonTime = 20f
  protected var knockbackPower = 0f

  protected var healing = false

  protected var healingTickTime = 0.3f

  protected var healingTime = 8f
  protected var healingPower = 0f

  protected var knockback: Boolean = false

  protected var knockbackVector: CustomVector2 = _

  protected var knockbackSpeed: Float = 0f

  protected var scale: Float = 1f

  protected var knockbackable = true

  protected var dropTable: mutable.Map[String, Float] = mutable.Map()

  protected val onGettingHitSound: Sound = null

  protected var creatureType: String = "regularCreature"

  protected var effectMap: mutable.Map[String, Effect] = mutable.Map()

  protected var staminaDrain = 0.0f

  val rect: CustomRectangle = new CustomRectangle(0, 0, 64, 64)
  val hitboxBounds: CustomRectangle = new CustomRectangle(2, 2, 60, 60)

  val isPlayer = false
  val isMob = false

  var passedGateRecently = false

  var area: Area = _

  var attackVector: CustomVector2 = CustomVector2(0f, 0f)
  var facingVector: CustomVector2 = CustomVector2(0f, 0f)

  var maxHealthPoints = 100f
  var healthPoints: Float = maxHealthPoints

  var maxStaminaPoints = 100f
  var staminaPoints: Float = maxStaminaPoints

  var isAttacking = false

  var toBeRemoved = false

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

  var equipmentItems: mutable.Map[Int, Item] = mutable.Map()

  protected var movingDir: IntPair = IntPair(0, 0)
  protected var isMoving = false
  protected var wasMoving = false
  protected var totalDirections = 0
  protected var movementIncrement: Float = 0
  protected var runningStoppedTimer: Timer = Timer()

  var movementVector: CustomVector2 = CustomVector2(0f, 0f)

  val baseSpeed: Float = 400.0f

  protected val walkAnimationFrameDuration = 0.1f
  protected val walkAnimationTimer: Timer = Timer()
  protected var neutralPositionIndex: Int = _
  protected var isRunningAnimationActive = false

  protected var walkAnimation: mutable.Map[WalkDirection, Animation] = mutable.Map()

  var lastMovingDir: WalkDirection = WalkDirection.Down

  protected var bowAttack: BowAttack = _
  protected var unarmedAttack: UnarmedAttack = _
  protected var swordAttack: SwordAttack = _
  protected var tridentAttack: TridentAttack = _

  var abilityList: mutable.ListBuffer[Ability] = _
  var attackList: mutable.ListBuffer[Attack] = _

  var currentAttack: Attack = _

  protected var healthRegenTimer: Timer = Timer(true)
  protected var staminaRegenTimer: Timer = Timer(true)
  protected var poisonTickTimer: Timer = Timer()
  protected var staminaOveruseTimer: Timer = Timer()
  protected var healingTimer: Timer = Timer()
  protected var healingTickTimer: Timer = Timer()
  protected var knockbackTimer: Timer = Timer()

  def alive: Boolean = healthPoints > 0f


  def setFacingDirection(): Unit = {

  }

  def update(): Unit = {
    if (alive) {
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


    if (GameSystem.cameraFocussedCreature.nonEmpty
      && this == GameSystem.cameraFocussedCreature.get) {
      GameSystem.adjustCamera(rect)
    }

    if (staminaDrain >= 0.3f) {
      takeStaminaDamage(8f)

      staminaDrain = 0.0f
    }
  }

  def render(batch: CustomBatch): Unit = {
    drawRunningAnimation(batch)

    abilityList.foreach(ability => ability.renderSprites(batch))
    currentAttack.renderSprites(batch)
  }

  def renderHealthBar(batch: CustomBatch): Unit = {
    val healthBarHeight = 5
    val healthBarWidth = 50
    val currentHealthBarWidth = healthBarWidth * healthPoints/maxHealthPoints
    batch.drawRect(new CustomRectangle(rect.x + (rect.width/2 - healthBarWidth/2), rect.y + rect.width + 10, healthBarWidth, healthBarHeight), Color.ORANGE)
    batch.drawRect(new CustomRectangle(rect.x + (rect.width/2 - healthBarWidth/2), rect.y + rect.width + 10, currentHealthBarWidth, healthBarHeight), Color.RED)

  }

  def performActions(): Unit


  def takeDamage(damage: Float, immunityFrames: Boolean, knockbackPower: Float, sourceX: Float, sourceY: Float): Unit = {
    if (alive) {
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

      if (knockbackable && !knockback && knockbackPower > 0f) {
        this.knockbackPower = knockbackPower
        knockbackVector = CustomVector2(rect.getX - sourceX, rect.getY - sourceY).normal
        knockback = true
        knockbackTimer.resetStart()

      }

      onGettingHitSound.play(0.1f)
    }
  }

  def renderAbilities(batch: SpriteBatch): Unit = {
    for (ability <- abilityList) {
      ability.renderSprites(batch)
    }
    currentAttack.renderSprites(batch)
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

    currentAttack = swordAttack
  }

  def onInit(): Unit = {
    defineEffects()

    defineStandardAbilities()

    defineCustomAbilities()

    updateAttackType()
  }

  protected def defineCustomAbilities(): Unit = {
  }

  protected def defineEffects(): Unit = {
    effectMap.put("immune", new Effect(this))
    effectMap.put("immobilized", new Effect(this))
    effectMap.put("staminaRegenStopped", new Effect(this))
    effectMap.put("poisoned", new Effect(this))

  }

  def updateAttackType(): Unit = {

  }

  def onPassedGate(gatesList: ListBuffer[AreaGate]): Unit = {
    var leftGate = true
    for (areaGate <- gatesList) {
      if (leftGate) {
        if (areaGate.areaFrom == area) if (rect.intersects(areaGate.fromRect)) {
          leftGate = false
        }
        if (areaGate.areaTo == area) if (rect.intersects(areaGate.toRect)) {
          leftGate = false
        }
      }
    }

    passedGateRecently = !leftGate
  }

  def regenerate(): Unit = {
    if (healthRegenTimer.time > 0.5f) {
      heal(healthRegen)
      healthRegenTimer.resetStart()
    }

    if (!isEffectActive("staminaRegenStopped") && !sprinting) if (staminaRegenTimer.time > 0.25f && !abilityActive && !staminaOveruse) {
      if (staminaPoints < maxStaminaPoints) {
        val afterRegen = staminaPoints + staminaRegen
        staminaPoints = Math.min(afterRegen, maxStaminaPoints)
      }
      staminaRegenTimer.resetStart()
    }

    if (staminaOveruse) if (staminaOveruseTimer.time > staminaOveruseTime) staminaOveruse = false

    if (getEffect("poisoned").isActive) if (poisonTickTimer.time > poisonTickTime) {
      takeDamage(15f, immunityFrames = false, 0, 0, 0)
      poisonTickTimer.resetStart()
    }

    if (healing) {
      if (healingTickTimer.time > healingTickTime) {
        heal(healingPower)
        healingTickTimer.resetStart()
      }
      if (healingTimer.time > healingTime || healthPoints >= maxHealthPoints) healing = false
    }
  }

  def abilityActive: Boolean = {
    // TODO
    false
  }

  def heal(healValue: Float): Unit = {
    if (healthPoints < maxHealthPoints) {
      val afterHeal = healthPoints + healValue
      healthPoints = Math.min(afterHeal, maxHealthPoints)

    }
  }

  def becomePoisoned(): Unit = {
    // TODO
  }

  def totalArmor: Float = {
    // TODO
    0f
  }

  def onDeath(): Unit = {
    isRunningAnimationActive = false
  }

  def kill: Unit = {
    // TODO
  }

  def moveToArea(area: Area, posX: Float, posY: Float): Unit = {
    // TODO

  }

  def takeStaminaDamage(staminaDamage: Float): Unit = {
    if (staminaPoints - staminaDamage > 0) staminaPoints -= staminaDamage
    else {
      staminaPoints = 0f
      staminaOveruse = true
      staminaOveruseTimer.resetStart()
    }
  }

  def useItem(item: Item): Unit = {
    // TODO
  }

  private def startHealing(healingPower: Float): Unit = {
    // TODO
  }

  def reset(): Unit = {
    healthPoints = maxHealthPoints
    rect.x = startingPosX
    rect.y = startingPosY
  }

  def onAttack(): Unit = {
    // TODO
  }

  def isNoAbilityActive: Boolean = {
    for (ability <- abilityList) {
      if (ability.active) return false
    }
    true
  }

  def onAggroed(): Unit = {

  }

  def getEffect(effectName: String): Effect = {
    effectMap.get(effectName) match {
      case Some(effect) => effect
      case _ => throw new RuntimeException("tried to access non-existing effect: " + effectName)
    }
  }

  def isEffectActive(effectName: String): Boolean = {
    effectMap.get(effectName) match {
      case Some(effect) => effect.isActive
      case _ => throw new RuntimeException("tried to access non-existing effect: " + effectName)
    }
  }

  def isImmune: Boolean = isEffectActive("immune")

  def compare(other: Creature): Int = {
    if (healthPoints <= 0.0f) {
      return 1
    }
    if (other.healthPoints <= 0.0f) {
      return -1
    }
    if (rect.y == other.rect.y) {
      return 0
    }
    if (rect.getY - other.rect.getY > 0.0f) 1 else -1

  }

  def hitbox: CustomRectangle = new CustomRectangle(rect.x + hitboxBounds.x, rect.y + hitboxBounds.y,
    hitboxBounds.width, hitboxBounds.height)

  // TODO: check blockade list for collisions
  def isCollidingX(tiledMap: TiledMap, blockadeList: ListBuffer[Blockade], newPosX: Float, newPosY: Float): Boolean = {
    val layer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]

    var collided = false

    for {x <- Seq.range(0, layer.getWidth)
         y <- Seq.range(0, layer.getHeight)} {
      if (!collided) {
        val cell: TiledMapTileLayer.Cell = layer.getCell(x, y)

        val traversable: Boolean = cell.getTile.getProperties.get("traversable").asInstanceOf[Boolean]

        if (!traversable) {
          collided = {
            val rect1 = new CustomRectangle(x * GameSystem.TiledMapCellSize, y * GameSystem.TiledMapCellSize,
              GameSystem.TiledMapCellSize, GameSystem.TiledMapCellSize)
            val rect2 = new CustomRectangle(newPosX + hitboxBounds.x, hitbox.y,
              hitbox.width, hitbox.height)

            rect1.overlaps(rect2)
          }
        }

      }

    }

    collided
  }

  // TODO: check blockade list for collisions
  def isCollidingY(tiledMap: TiledMap, blockadeList: ListBuffer[Blockade], newPosX: Float, newPosY: Float): Boolean = {
    val layer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]

    var collided = false

    // TODO: check collisions for nearby cells only
    for {x <- Seq.range(0, layer.getWidth)
         y <- Seq.range(0, layer.getHeight)} {
      if (!collided) {
        val cell: TiledMapTileLayer.Cell = layer.getCell(x, y)

        val traversable: Boolean = cell.getTile.getProperties.get("traversable").asInstanceOf[Boolean]

        if (!traversable) {
          collided = {
            val rect1 = new CustomRectangle(x * GameSystem.TiledMapCellSize, y * GameSystem.TiledMapCellSize,
              GameSystem.TiledMapCellSize, GameSystem.TiledMapCellSize)
            val rect2 = new CustomRectangle(hitbox.x, newPosY + hitboxBounds.y,
              hitbox.width, hitbox.height)

            rect1.overlaps(rect2)
          }
        }

      }
    }

    collided
  }

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

  def onUpdateStart(): Unit = {
    isMoving = false

    totalDirections = 0

    knockbackSpeed = knockbackPower * Gdx.graphics.getDeltaTime

    movingDir.x = 0
    movingDir.y = 0

    var adjustedSpeed = this.baseSpeed

    if (isAttacking) adjustedSpeed = adjustedSpeed / 3
    else if (sprinting && staminaPoints > 0) {
      adjustedSpeed = adjustedSpeed * 1.65f
      staminaDrain += Gdx.graphics.getDeltaTime
    }

    movementIncrement = adjustedSpeed * Gdx.graphics.getDeltaTime

  }

  def processMovement(): Unit = {

    assert(GameSystem.currentArea.nonEmpty)

    val tiledMap = GameSystem.currentArea.get.tiledMap
    val blockadeList = GameSystem.currentArea.get.blockadeList



    if (!isEffectActive("immobilized") && !knockback) {

      if (totalDirections > 1) movementIncrement = movementIncrement / Math.sqrt(2).toFloat
      val newPosX = rect.getX + movementIncrement * movingDir.x
      val newPosY = rect.getY + movementIncrement * movingDir.y


      if (isMovementAllowedXAxis(newPosX, newPosY, tiledMap, blockadeList)) {
        move(movementIncrement * movingDir.x, 0)
        movementVector.x = movementIncrement * movingDir.x
      }
      else movementVector.x = 0

      if (isMovementAllowedYAxis(newPosX, newPosY, tiledMap, blockadeList)) {
        move(0, movementIncrement * movingDir.y)
        movementVector.y = movementIncrement * movingDir.y
      }
      else movementVector.y = 0

      if (isMoving && !wasMoving) if (!isRunningAnimationActive) {

        isRunningAnimationActive = true
        walkAnimationTimer.resetStart()
      }

      if (!isMoving && wasMoving) runningStoppedTimer.resetStart()

      if (!isMoving && isRunningAnimationActive && runningStoppedTimer.time > 0.25f) {
        isRunningAnimationActive = false
        runningStoppedTimer.stop()
        walkAnimationTimer.stop()
      }

      wasMoving = isMoving
    }

    if (knockback) {
      val tiledMap = GameSystem.currentArea.get.tiledMap

      val newPosX: Float = rect.getX + knockbackSpeed * knockbackVector.x
      val newPosY: Float = rect.getY + knockbackSpeed * knockbackVector.y
      val blockadeList: ListBuffer[Blockade] = GameSystem.currentArea.get.blockadeList
      if (isMovementAllowedXAxis(newPosX, newPosY, tiledMap, blockadeList)) move(knockbackSpeed * knockbackVector.x, 0)
      if (isMovementAllowedYAxis(newPosX, newPosY, tiledMap, blockadeList)) move(0, knockbackSpeed * knockbackVector.y)
      if (knockbackTimer.time > 0.15f) knockback = false
    }

    for (ability <- abilityList) {
      ability.performMovement()
    }

    currentAttack.performMovement()
  }

  def isMovementAllowedXAxis(newPosX: Float, newPosY: Float, tiledMap: TiledMap, blockadeList: ListBuffer[Blockade]): Boolean = {
    !isCollidingX(tiledMap, blockadeList, newPosX, newPosY) && newPosX + hitboxBounds.x >= 0 && newPosX <
      GameSystem.getTiledMapRealWidth(tiledMap) - (hitboxBounds.x + hitboxBounds.width)
  }

  def isMovementAllowedYAxis(newPosX: Float, newPosY: Float, tiledMap: TiledMap, blockadeList: ListBuffer[Blockade]): Boolean = {
    !isCollidingY(tiledMap, blockadeList, newPosX, newPosY) && newPosY + hitboxBounds.y >= 0 && newPosY <
      GameSystem.getTiledMapRealHeight(tiledMap) - (hitboxBounds.y + hitboxBounds.height)
  }

  def move(dx: Float, dy: Float): Unit = {
    rect.x = rect.x + dx
    rect.y = rect.y + dy

  }

  def controlMovement(): Unit = {

  }

  def loadSprites(spriteSheet: SpriteSheet, directionalMapping: Map[WalkDirection, Int], neutralPositionIndex: Int): Unit = {

    this.neutralPositionIndex = neutralPositionIndex

    WalkDirection.values.foreach(dir => {
      walkAnimation(dir) = new Animation(spriteSheet, walkAnimationFrameDuration, rect.width, rect.height,
        GameSystem.textureRegionPrefix + directionalMapping(dir))
    })

  }

  def drawRunningAnimation(batch: SpriteBatch): Unit = {
    if (isRunningAnimationActive) {
      val currentFrame: Sprite = new Sprite(walkAnimation(lastMovingDir).currentFrame())

      currentFrame.setPosition(rect.x, rect.y)

      currentFrame.draw(batch)
    }
    else {
      val currentFrame: Sprite = new Sprite(walkAnimation(lastMovingDir).getFrameByIndex(neutralPositionIndex))

      if (!alive) {
        currentFrame.rotate90(true)
      }

      currentFrame.setPosition(rect.x, rect.y)

      currentFrame.draw(batch)
    }
  }
}
