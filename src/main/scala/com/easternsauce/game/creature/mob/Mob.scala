package com.easternsauce.game.creature.mob

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.WalkDirection.WalkDirection
import com.easternsauce.game.creature.util.{Unarmed, WalkDirection}
import com.easternsauce.game.shapes.CustomVector2
import com.easternsauce.game.utils.Timer
import org.lwjgl.util.vector.Vector2f
import system.GameSystem

abstract class Mob(id: String) extends Creature(id) {
  protected var aggroedCreature: Option[Creature] = None
  protected var aggroDistance: Float = 400

  protected var destinationX = 0f
  protected var destinationY = 0f
  protected var hasDestination = false

  override val isMob = true

  protected var attackOrHoldTimer: Timer = Timer(true)
  protected var attackOrHoldTime = 0.5f
  protected var hold = false

  protected var circlingDirectionTimer: Timer = Timer(true)
  protected var circlingDirectionTime = 0.5f
  protected var circling = false
  protected var circlingDir = 0
  protected var findNewDestinationTimer: Timer = Timer(true)

  protected var actionTimer: Timer = Timer(true)

  protected var stayInPlace = false

  protected var currentDirection: WalkDirection = WalkDirection.Down


  protected var attackDistance: Float = null.asInstanceOf[Float]
  protected var walkUpDistance: Float = null.asInstanceOf[Float]

  override def performActions(): Unit = {

    aggroedCreature = None
    var foundCreatureToAggro = false



    GameSystem.areaCreatures.filter(creature => !creature.isMob).foreach(creature => { // TODO: exclude npc too
      if (!foundCreatureToAggro && alive && GameSystem.distance(rect, creature.rect) < aggroDistance) {
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
    val creatureCenter = rect.center

    import com.easternsauce.game.creature.util.WalkDirection._

    if (creatureCenter.x < gotoPosX - 5f) move(Right)
    else if (creatureCenter.x > gotoPosX + 5f) move(Left)

    else if (creatureCenter.y > gotoPosY + 5f) move(Down)
    if (creatureCenter.y < gotoPosY - 5f) move(Up)


    val distX = Math.abs(creatureCenter.x - gotoPosX)
    val distY = Math.abs(creatureCenter.y - gotoPosY)
    if (distX - distY < 20f) if (creatureCenter.x < gotoPosX) lastMovingDir = Right
    else lastMovingDir = Left
  }

  def performAggroedBehavior(): Unit = {
    if (attackOrHoldTimer.time > attackOrHoldTime) {
      hold = GameSystem.random.nextFloat() < 0.8f
      attackOrHoldTimer.resetStart()
    }

    if (circlingDirectionTimer.time > circlingDirectionTime) {
      circling = GameSystem.random.nextFloat() < 0.8f
      if (circling) if (GameSystem.random.nextFloat() < 0.5f) circlingDir = 0
      else circlingDir = 1
      circlingDirectionTimer.resetStart()
    }

    //val attackType = currentAttack.getAttackType

    val aggroed = aggroedCreature match {
      case Some(value) => value
      case None => throw new RuntimeException("aggroed creature is not set")
    }

    val attackType = Unarmed // TODO: val attackType: AttackType = currentAttack.getAttackType

    val aggroedCenter = aggroed.rect.center
    val creatureCenter = rect.center

    val dist = GameSystem.distance(this.rect, aggroed.rect);

    if (findNewDestinationTimer.time > 0.2f) {
      if (dist < attackType.holdDistance) {
        if (hold) {
          if (circling) {
            if (circlingDir == 0) {
              destinationX = aggroedCenter.x
              destinationY = aggroedCenter.y
              val destinationVector = CustomVector2(destinationX - creatureCenter.x, destinationY - creatureCenter.y)
              val perpendicular = GameSystem.getVectorPerpendicular(destinationVector)
              destinationX = aggroedCenter.x + perpendicular.x
              destinationY = aggroedCenter.y + perpendicular.y
              hasDestination = true
            }
            else {
              destinationX = aggroedCenter.x
              destinationY = aggroedCenter.y
              val destinationVector = CustomVector2(destinationX - creatureCenter.x, destinationY - creatureCenter.y)
              val perpendicular = GameSystem.getVectorPerpendicular(destinationVector)
              val negated = CustomVector2(-perpendicular.x, -perpendicular.y)

              destinationX = creatureCenter.x + negated.x
              destinationY = creatureCenter.y + negated.y
              hasDestination = true
            }
          }
          else {
            hasDestination = false;
          }
        }
        else {
          destinationX = aggroedCenter.x
          destinationY = aggroedCenter.y
          hasDestination = true
        }
      }
      else if (dist < (if (walkUpDistance == null.asInstanceOf[Float]) attackType.walkUpDistance else walkUpDistance)) {
        destinationX = aggroedCenter.x
        destinationY = aggroedCenter.y
        hasDestination = true
      }
      else {
        hasDestination = false
      }

      findNewDestinationTimer.resetStart();
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
      actionTimer.resetStart()
    }

    if (!stayInPlace) {
      move(currentDirection)
    }

  }

  override def onDeath(): Unit = {
    super.onDeath()

    //gameSystem.getLootSystem.spawnLootPile(area, rect.getCenterX, rect.getCenterY, dropTable)

    for (ability <- abilityList) {
      ability.stopAbility()
    }

    currentAttack.stopAbility()
  }


  def grantWeapon(weaponName: String) {
    // TODO
  }

  override def setFacingDirection(): Unit = {
    if (aggroedCreature.nonEmpty) {
      val aggroed = aggroedCreature.get
      facingVector = CustomVector2(aggroed.rect.center.x - rect.center.x, -(aggroed.rect.center.y - rect.center.y))
    }
  }

}
