package com.easternsauce.game.creature

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.attack.{Attack, BowAttack, SwordAttack, TridentAttack, UnarmedAttack}
import com.easternsauce.game.animation.Animation
import com.easternsauce.game.area.{Area, AreaGate}
import com.easternsauce.game.assets.{Assets, SpriteSheet}
import com.easternsauce.game.creature.util.WalkDirection
import com.easternsauce.game.creature.util.WalkDirection.WalkDirection
import com.easternsauce.game.effect.Effect
import com.easternsauce.game.item.Item
import com.easternsauce.game.shapes.Rectangle
import com.easternsauce.game.utils.{IntPair, Timer}
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

abstract class Creature(val id: String) extends Ordered[Creature] {

  protected var healthRegen = 0.3f
  protected var staminaRegen = 10f

  protected var staminaOveruseTime = 1300


  protected var poisonTickTime = 1500

  protected var poisonTime = 20000
  protected var knockbackPower = 0f

  protected var healing = false

  protected var healingTickTime = 300

  protected var healingTime = 8000
  protected var healingPower = 0f

  protected var knockback: Boolean = false

  protected var knockbackVector: Vector2 = _

  protected var knockbackSpeed: Float = 0f

  protected var scale: Float = 1f

  protected var knocbackable = false

  protected var dropTable: mutable.Map[String, Float] = mutable.Map()

  protected val onGettingHitSound: Sound = null

  protected var baseSpeed = 0f

  protected var creatureType: String = "regularCreature"

  protected var effectMap: mutable.Map[String, Effect] = mutable.Map()

  val rect: Rectangle = new Rectangle(0, 0, 64, 64)
  val hitboxBounds: Rectangle = new Rectangle(2, 2, 60, 60)

  val isPlayer = false
  val isMob = false

  var passedGateRecently = false

  var area: Area = _

  var attackVector: Vector2 = new Vector2(0f, 0f)
  var facingVector: Vector2 = new Vector2(0f, 0f)

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
  protected var movementVector: Vector2 = new Vector2(0f, 0f)
  protected var runningStoppedTimer: Timer = Timer()

  val speed: Float = 400.0f

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

      performActions()

      controlMovement()
      processMovement()

      setFacingDirection()
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
  }

  def render(batch: SpriteBatch): Unit = {
    drawRunningAnimation(batch)

    abilityList.foreach(ability => ability.renderSprites(batch))
    currentAttack.renderSprites(batch)
  }

  def performActions(): Unit


  def takeDamage(damage: Float, immunityFrames: Boolean, knockbackPower: Float, x: Float, y: Float): Unit = {
    healthPoints -= damage
    // TODO

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
    // TODO
  }

  def abilityActive: Boolean = {
    // TODO
    false
  }

  def heal(healValue: Float): Unit = {
    // TODO
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
    // TODO

  }

  def useItem(item: Item): Unit = {
    // TODO
  }

  private def startHealing(healingPower: Float): Unit = {
    // TODO
  }

  def reset(): Unit = {
    // TODO
  }

  def onAttack(): Unit = {
    // TODO
  }

  def isNoAbilityActive: Boolean = {
    // TODO
    false
  }

  def onAggroed(): Unit = {
    // TODO
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

  def hitbox: Rectangle = new Rectangle(rect.x + hitboxBounds.x, rect.y + hitboxBounds.y,
    hitboxBounds.width, hitboxBounds.height)

  def isCollidingX(tiledMap: TiledMap, newPosX: Float, newPosY: Float): Boolean = {
    val layer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]

    var collided = false

    for {x <- Seq.range(0, layer.getWidth)
         y <- Seq.range(0, layer.getHeight)} {
      if (!collided) {
        val cell: TiledMapTileLayer.Cell = layer.getCell(x, y)

        val traversable: Boolean = cell.getTile.getProperties.get("traversable").asInstanceOf[Boolean]

        if (!traversable) {
          collided = {
            val rect1 = new Rectangle(x * GameSystem.TiledMapCellSize, y * GameSystem.TiledMapCellSize,
              GameSystem.TiledMapCellSize, GameSystem.TiledMapCellSize)
            val rect2 = new Rectangle(newPosX + hitboxBounds.x, hitbox.y,
              hitbox.width, hitbox.height)

            rect1.overlaps(rect2)
          }
        }

      }

    }

    collided
  }

  def isCollidingY(tiledMap: TiledMap, newPosX: Float, newPosY: Float): Boolean = {
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
            val rect1 = new Rectangle(x * GameSystem.TiledMapCellSize, y * GameSystem.TiledMapCellSize,
              GameSystem.TiledMapCellSize, GameSystem.TiledMapCellSize)
            val rect2 = new Rectangle(hitbox.x, newPosY + hitboxBounds.y,
              hitbox.width, hitbox.height)

            rect1.overlaps(rect2)
          }
        }

      }
    }

    collided
  }

  def move(dir: WalkDirection): Unit = {
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

    movingDir.x = 0
    movingDir.y = 0

    var adjustedSpeed = this.speed

    if (isAttacking) adjustedSpeed = adjustedSpeed / 3
    else if (sprinting && staminaPoints > 0) adjustedSpeed = adjustedSpeed * 2

    movementIncrement = adjustedSpeed * Gdx.graphics.getDeltaTime

  }

  def processMovement(): Unit = {

    if (totalDirections > 1) {
      movementIncrement = movementIncrement / Math.sqrt(2).floatValue
    }
    val newPosX: Float = rect.getX + movementIncrement * movingDir.x
    val newPosY: Float = rect.getY + movementIncrement * movingDir.y

    if (!isCollidingX(Assets.grassyMap, newPosX, newPosY)
      && newPosX + hitboxBounds.x >= 0 && newPosX <
      GameSystem.getTiledMapRealWidth(Assets.grassyMap) - (hitboxBounds.x + hitboxBounds.width)) {
      move(movementIncrement * movingDir.x, 0)
      movementVector.x = movementIncrement * movingDir.x
    }
    else movementVector.x = 0

    if (!isCollidingY(Assets.grassyMap, newPosX, newPosY)
      && newPosY + hitboxBounds.y >= 0 && newPosY <
      GameSystem.getTiledMapRealHeight(Assets.grassyMap) - (hitboxBounds.y + hitboxBounds.height)) {
      move(0, movementIncrement * movingDir.y)
      movementVector.y = movementIncrement * movingDir.y
    }
    else movementVector.y = 0

    if (isMoving && !wasMoving) {
      if (!isRunningAnimationActive) {

        isRunningAnimationActive = true
        walkAnimationTimer.resetStart()
      }
    }

    if (!isMoving && wasMoving) {
      runningStoppedTimer.resetStart()
    }

    if (!isMoving && isRunningAnimationActive && runningStoppedTimer.time > 0.25f) {
      isRunningAnimationActive = false
      runningStoppedTimer.stop()
      walkAnimationTimer.stop()
    }

    wasMoving = isMoving
  }

  private def move(dx: Float, dy: Float): Unit = {
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
