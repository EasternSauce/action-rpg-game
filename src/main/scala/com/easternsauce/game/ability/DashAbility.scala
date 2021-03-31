package com.easternsauce.game.ability

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.CustomVector2
import com.easternsauce.game.spawn.Blockade

import scala.collection.mutable.ListBuffer

class DashAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  protected var dashAcceleration: Float = 0.0f
  protected var dashDistance: Float = 0.0f
  protected var dashVector: Vector2 = new Vector2(0f, 0f)
  protected var dashSpeed: Float = 0.0f

  override def init(): Unit = {
    cooldownTime = 1.0f
    channelTime = 0
    dashAcceleration = 3f
    dashSpeed = 1000.0f
    dashDistance = 200
    activeTime = dashDistance / dashSpeed
  }


  override def performMovement(): Unit = {
    if (state == AbilityState.Active) {

      val dashMaxVelocity = 50f

      if (dashVector.x < 0f) {
        if (abilityCreature.body.getLinearVelocity.x >= -dashMaxVelocity) {
          abilityCreature.body.applyLinearImpulse(new Vector2(dashAcceleration * dashVector.x, 0), abilityCreature.body.getWorldCenter, true)
        }
      }
      else if (dashVector.x > 0f) {
        if (abilityCreature.body.getLinearVelocity.x <= dashMaxVelocity) {
          abilityCreature.body.applyLinearImpulse(new Vector2(dashAcceleration * dashVector.x, 0), abilityCreature.body.getWorldCenter, true)
        }
      }
      if (dashVector.y < 0f) {
        if (abilityCreature.body.getLinearVelocity.y >= -dashMaxVelocity) {
          abilityCreature.body.applyLinearImpulse(new Vector2(0, dashAcceleration * dashVector.y), abilityCreature.body.getWorldCenter, true)
        }
      }
      else if (dashVector.y > 0f) {
        if (abilityCreature.body.getLinearVelocity.y <= dashMaxVelocity) {
          abilityCreature.body.applyLinearImpulse(new Vector2(0, dashAcceleration * dashVector.y), abilityCreature.body.getWorldCenter, true)
        }
      }

    }
  }

  override protected def onActiveStart(): Unit = {
    abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime)
    abilityCreature.takeStaminaDamage(15f)
  }

  override def performOnUpdateStart(): Unit = {

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