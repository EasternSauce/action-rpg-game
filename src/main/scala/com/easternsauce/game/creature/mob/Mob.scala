package com.easternsauce.game.creature.mob

import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.WalkDirection.WalkDirection
import com.easternsauce.game.creature.util.{AttackType, Unarmed, WalkDirection}
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.util.ItemType
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.SimpleTimer
import system.GameSystem

abstract class Mob(override val id: String, val mobSpawnPoint: MobSpawnPoint) extends Creature(id) {
  protected var aggroedCreature: Option[Creature] = None
  protected var aggroDistance: Float = 800

  protected var destinationX = 0f
  protected var destinationY = 0f
  protected var hasDestination = false

  override val isMob = true

  protected var attackOrHoldTimer: SimpleTimer = SimpleTimer(true)
  protected var attackOrHoldTime = 0.5f
  protected var hold = false

  protected var circlingDirectionTimer: SimpleTimer = SimpleTimer(true)
  protected var circlingDirectionTime = 0.5f
  protected var circling = false
  protected var circlingDir = 0
  protected var findNewDestinationTimer: SimpleTimer = SimpleTimer(true)

  protected var actionTimer: SimpleTimer = SimpleTimer(true)

  protected var stayInPlace = false

  protected var currentDirection: WalkDirection = WalkDirection.Down


  protected var attackDistance: Float = null.asInstanceOf[Float]
  protected var walkUpDistance: Float = null.asInstanceOf[Float]

  val attackType: AttackType = Unarmed // TODO: val attackType: AttackType = currentAttack.getAttackType


  override def performActions(): Unit = {

    aggroedCreature = None
    var foundCreatureToAggro = false



    GameSystem.areaCreatures.filter(creature => !creature.isMob && !creature.isNPC).foreach(creature => { // TODO: exclude npc too

      if (!foundCreatureToAggro && isAlive && GameSystem.distance(body, creature.body) < aggroDistance) {
        aggroedCreature = Some(creature)
        foundCreatureToAggro = true

        onAggroed()
      }

    })

    aggroedCreature match {
      case Some(_) => performAggroedBehavior()
      case None => performIdleBehavior()
    }

  }

  override def onAggroed(): Unit = {

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
    if (distX - distY < 20f) if (creatureCenterX < gotoPosX) lastMovingDir = Right
    else lastMovingDir = Left
  }

  def performAggroedBehavior(): Unit = {
    if (attackOrHoldTimer.time > attackOrHoldTime) {
      hold = GameSystem.random.nextFloat() < 0.8f
      attackOrHoldTimer.restart()
    }

    if (circlingDirectionTimer.time > circlingDirectionTime) {
      circling = GameSystem.random.nextFloat() < 0.8f
      if (circling) if (GameSystem.random.nextFloat() < 0.5f) circlingDir = 0
      else circlingDir = 1
      circlingDirectionTimer.restart()
    }

    //val attackType = currentAttack.getAttackType

    val aggroed = aggroedCreature match {
      case Some(value) => value
      case None => throw new RuntimeException("aggroed creature is not set")
    }

    val aggroedCenterX = aggroed.posX
    val aggroedCenterY = aggroed.posY

    val creatureCenterX = posX
    val creatureCenterY = posY

    val dist = GameSystem.distance(this.body, aggroed.body)

    if (findNewDestinationTimer.time > 0.2f) {
      if (dist < attackType.holdDistance) {
        if (hold) {
          if (circling) {
            if (circlingDir == 0) {
              destinationX = aggroedCenterX
              destinationY = aggroedCenterY
              val destinationVector = new Vector2(destinationX - creatureCenterX, destinationY - creatureCenterY)
              val perpendicular = GameSystem.getVectorPerpendicular(destinationVector)
              destinationX = aggroedCenterX + perpendicular.x
              destinationY = aggroedCenterY + perpendicular.y
              hasDestination = true
            }
            else {
              destinationX = aggroedCenterX
              destinationY = aggroedCenterY
              val destinationVector = new Vector2(destinationX - creatureCenterX, destinationY - creatureCenterY)
              val perpendicular = GameSystem.getVectorPerpendicular(destinationVector)
              val negated = new Vector2(-perpendicular.x, -perpendicular.y)

              destinationX = creatureCenterX + negated.x
              destinationY = creatureCenterY + negated.y
              hasDestination = true
            }
          }
          else {
            hasDestination = false;
          }
        }
        else {
          destinationX = aggroedCenterX
          destinationY = aggroedCenterY
          hasDestination = true
        }
      }
      else if (dist < (if (walkUpDistance == null.asInstanceOf[Float]) attackType.walkUpDistance else walkUpDistance)) {
        destinationX = aggroedCenterX
        destinationY = aggroedCenterY
        hasDestination = true
      }
      else {
        hasDestination = false
      }

      findNewDestinationTimer.restart();
    }

    if (hasDestination) walkTowards(destinationX, destinationY)

    if (dist < (if (attackDistance == null.asInstanceOf[Float]) attackType.attackDistance else attackDistance)) {
      if (currentAttack.canPerform) currentAttack.perform()
    }

  }

  def performIdleBehavior(): Unit = {
    if (actionTimer.time > 0.5f) {
      lastMovingDir = WalkDirection.randomDir()
      stayInPlace = Math.abs(GameSystem.random.nextInt) % 10 < 8
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
    equipmentItems.put(0, new Item(weaponItemType, null))

  }

  override def setFacingDirection(): Unit = {
    if (aggroedCreature.nonEmpty) {
      val aggroed = aggroedCreature.get
      facingVector = new Vector2(aggroed.posX - posX, aggroed.posY - posY)
    }
  }

}
