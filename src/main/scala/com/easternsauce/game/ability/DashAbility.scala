package com.easternsauce.game.ability

import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.creature.Creature
import system.GameSystem

class DashAbility(override val abilityCreature: Creature, val dashDistance: Float) extends Ability(abilityCreature) {
  protected var dashAcceleration: Float = 0.0f
  protected var dashVector: Vector2 = new Vector2(0f, 0f)
  protected var dashSpeed: Float = 0.0f

  override def init(): Unit = {
    cooldownTime = 1.0f
    channelTime = 0
    dashAcceleration = abilityCreature.mass * 20f

    dashSpeed = 40.0f

    activeTime = dashDistance / (dashSpeed * GameSystem.PixelsPerMeter)
  }


  override def performMovement(): Unit = {
    if (state == AbilityState.Active) {

      val dashMaxVelocity = dashSpeed

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

  def setDashVector(dashVector: Vector2): Unit = {
    this.dashVector = dashVector
  }
}

object DashAbility {
  def apply(abilityCreature: Creature, dashDistance: Float = 160f): DashAbility = {
    val ability = new DashAbility(abilityCreature, dashDistance)
    ability.init()
    ability
  }
}