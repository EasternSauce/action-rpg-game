package com.easternsauce.game.creature.mob

import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.WalkDirection
import com.easternsauce.game.creature.util.WalkDirection.WalkDirection
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.util.ItemType
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.EsTimer
import system.GameSystem

abstract class Mob protected (override val id: String, val mobSpawnPoint: MobSpawnPoint) extends Creature(id) {
  override val isMob: Boolean = true
  protected var aggroDistance: Float = 600f
  protected var destinationX: Float = 0f
  protected var destinationY: Float = 0f
  protected var hasDestination: Boolean = false
  protected val attackOrHoldTimer: EsTimer = EsTimer(true)
  protected val attackOrHoldTime: Float = 0.5f
  protected var hold: Boolean = false

  protected val circlingDirectionTimer: EsTimer = EsTimer(true)
  protected val circlingDirectionTime: Float = 0.5f
  protected var circling: Boolean = false
  protected var circlingDir: Int = 0
  protected val findNewDestinationTimer: EsTimer = EsTimer(true)

  protected var actionTimer: EsTimer = EsTimer(true)

  protected var stayInPlace: Boolean = false

  protected var currentDirection: WalkDirection = WalkDirection.Down

  protected var attackDistance: Float = null.asInstanceOf[Float]
  protected var walkUpDistance: Float = null.asInstanceOf[Float]

  override def performActions(): Unit = {

    aggroedCreature = None
    var foundCreatureToAggro = false

    GameSystem.areaCreatures
      .filter(creature => !creature.isMob && !creature.isNPC)
      .foreach(creature => {
        if (
          !foundCreatureToAggro && isAlive && GameSystem
            .distance(body, creature.body) < aggroDistance
        ) {
          aggroedCreature = Some(creature)
          foundCreatureToAggro = true

          onAggroed()
        }

      })

    aggroedCreature match {
      case Some(_) => performAggroedBehavior()
      case None    => performIdleBehavior()
    }

  }

  override def onAggroed(): Unit = {}

  def performAggroedBehavior(): Unit = {
    if (attackOrHoldTimer.time > attackOrHoldTime) {
      hold = GameSystem.random.nextFloat() < 0.8f
      attackOrHoldTimer.restart()
    }

    if (circlingDirectionTimer.time > circlingDirectionTime) {
      circling = GameSystem.random.nextFloat() < 0.8f
      if (circling)
        if (GameSystem.random.nextFloat() < 0.5f) circlingDir = 0
        else circlingDir = 1
      circlingDirectionTimer.restart()
    }

    val aggroed = aggroedCreature match {
      case Some(value) => value
      case None        => throw new RuntimeException("aggroed creature is not set")
    }

    val aggroedCenterX = aggroed.posX
    val aggroedCenterY = aggroed.posY

    val creatureCenterX = posX
    val creatureCenterY = posY

    val dist = GameSystem.distance(this.body, aggroed.body)

    if (findNewDestinationTimer.time > 0.2f) {
      if (dist < currentAttackType.holdDistance) {
        if (hold) {
          if (circling) {
            if (circlingDir == 0) {
              destinationX = aggroedCenterX
              destinationY = aggroedCenterY
              val destinationVector = new Vector2(destinationX - creatureCenterX, destinationY - creatureCenterY)
              val perpendicular =
                GameSystem.getVectorPerpendicular(destinationVector)
              destinationX = aggroedCenterX + perpendicular.x
              destinationY = aggroedCenterY + perpendicular.y
              hasDestination = true
            } else {
              destinationX = aggroedCenterX
              destinationY = aggroedCenterY
              val destinationVector = new Vector2(destinationX - creatureCenterX, destinationY - creatureCenterY)
              val perpendicular =
                GameSystem.getVectorPerpendicular(destinationVector)
              val negated = new Vector2(-perpendicular.x, -perpendicular.y)

              destinationX = creatureCenterX + negated.x
              destinationY = creatureCenterY + negated.y
              hasDestination = true
            }
          } else {
            hasDestination = false
          }
        } else {
          destinationX = aggroedCenterX
          destinationY = aggroedCenterY
          hasDestination = true
        }
      } else if (
        dist < (if (walkUpDistance == null.asInstanceOf[Float])
                  currentAttackType.walkUpDistance
                else walkUpDistance)
      ) {
        destinationX = aggroedCenterX
        destinationY = aggroedCenterY
        hasDestination = true
      } else {
        hasDestination = false
      }

      findNewDestinationTimer.restart()
    }

    if (hasDestination) walkTowards(destinationX, destinationY)

    if (
      isNoAbilityActive && dist < (if (attackDistance == null.asInstanceOf[Float]) currentAttackType.attackDistance
                                   else attackDistance)
    ) {
      if (currentAttack.canPerform) currentAttack.perform()
    }

  }

  def walkTowards(gotoPosX: Float, gotoPosY: Float): Unit = {
    val creatureCenterX = posX
    val creatureCenterY = posY

    import com.easternsauce.game.creature.util.WalkDirection._

    if (creatureCenterX < gotoPosX - 5f) moveInDirection(Right)
    else if (creatureCenterX > gotoPosX + 5f) moveInDirection(Left)
    else if (creatureCenterY > gotoPosY + 5f) moveInDirection(Down)
    if (creatureCenterY < gotoPosY - 5f) moveInDirection(Up)

    val distX = Math.abs(creatureCenterX - gotoPosX)
    val distY = Math.abs(creatureCenterY - gotoPosY)
    if (distX - distY < 20f)
      if (creatureCenterX < gotoPosX) lastMovingDir = Right
      else lastMovingDir = Left
  }

  def performIdleBehavior(): Unit = {
    if (actionTimer.time > 0.5f) {
      stayInPlace = Math.abs(GameSystem.random.nextInt) % 10 < 9
      currentDirection = WalkDirection.randomDir()
      actionTimer.restart()
    }

    if (!stayInPlace) {
      moveInDirection(currentDirection)
    }

  }

  override def onDeath(): Unit = {
    isRunningAnimationActive = false

    GameSystem.lootSystem.spawnLootPile(area, posX, posY, dropTable)

    for (ability <- abilityList) {
      ability.forceStop()
    }

    currentAttack.forceStop()

    toSetBodyNonInteractive = true
  }

  def grantWeapon(weaponName: String) {
    val weaponItemType = ItemType.getItemType(weaponName)
    equipmentItems.put(0, Item(weaponItemType, null))

  }

  override def setFacingDirection(): Unit = {
    if (aggroedCreature.nonEmpty) {
      val aggroed = aggroedCreature.get
      facingVector = new Vector2(aggroed.posX - posX, aggroed.posY - posY).nor()
    }
  }

}
