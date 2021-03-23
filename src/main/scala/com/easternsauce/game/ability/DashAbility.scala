package com.easternsauce.game.ability

import com.badlogic.gdx.Gdx
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.CustomVector2
import com.easternsauce.game.spawn.Blockade

import scala.collection.mutable.ListBuffer

class DashAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  protected var dashFrameSpeed: Float = 0.0f
  protected var dashDistance: Float = 0.0f
  protected var dashVector: CustomVector2 = CustomVector2(0f, 0f)
  protected var dashSpeed: Float = 0.0f

  override def init(): Unit = {
    cooldownTime = 1.0f
    channelTime = 0
    dashFrameSpeed = 0.0f
    dashSpeed = 1000.0f
    dashDistance = 200
    activeTime = dashDistance / dashSpeed
  }


  override def performMovement(): Unit = {
    if (state == AbilityState.Active) {
      //check collisions after dash
      val tiles = abilityCreature.area.tiledMap
      val newPosX: Float = abilityCreature.posX + dashFrameSpeed * dashVector.x
      val newPosY: Float = abilityCreature.posY + dashFrameSpeed * dashVector.y
      val blockadeList: ListBuffer[Blockade] = abilityCreature.area.blockadeList

      // TODO: box2d dash
//      if (abilityCreature.isMovementAllowedXAxis(newPosX, newPosY, tiles, blockadeList)) {
//        abilityCreature.move(dashFrameSpeed * dashVector.x, 0f)
//      }
//
//      if (abilityCreature.isMovementAllowedYAxis(newPosX, newPosY, tiles, blockadeList)) {
//        abilityCreature.move(0f, dashFrameSpeed * dashVector.y)
//      }
    }
  }

  override protected def onActiveStart(): Unit = {
    abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime)
    abilityCreature.takeStaminaDamage(15f)
  }

  override def performOnUpdateStart(): Unit = {
    dashFrameSpeed = dashSpeed * Gdx.graphics.getDeltaTime
  }

  def setDashVector(dashVector: CustomVector2): Unit = {
    this.dashVector = dashVector
  }
}

object DashAbility {
  def apply(abilityCreature: Creature): DashAbility = {
    val dashAbility = new DashAbility(abilityCreature)
    dashAbility.init()
    dashAbility
  }
}