package com.easternsauce.game.ability

import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.creature.Creature
import system.GameSystem

class DashAbility private (override val abilityCreature: Creature, val dashDistance: Float)
    extends Ability(abilityCreature) {
  protected var dashSpeed: Float = 30.0f
  override protected var cooldownTime: Float = 1.0f
  override protected var channelTime: Float = 0f
  override protected var activeTime: Float =
    dashDistance / (dashSpeed * GameSystem.PixelsPerMeter)
  protected var dashAcceleration: Float = abilityCreature.mass * 20f
  protected var dashVector: Vector2 = new Vector2(0f, 0f)

  override def performMovement(): Unit = {
    if (state == AbilityState.Active) {

      val dashMaxVelocity = dashSpeed

      if (dashVector.x < 0f) {
        if (abilityCreature.body.getLinearVelocity.x >= -dashMaxVelocity) {
          abilityCreature.body.applyLinearImpulse(
            new Vector2(dashAcceleration * dashVector.x, 0),
            abilityCreature.body.getWorldCenter,
            true
          )
        }
      } else if (dashVector.x > 0f) {
        if (abilityCreature.body.getLinearVelocity.x <= dashMaxVelocity) {
          abilityCreature.body.applyLinearImpulse(
            new Vector2(dashAcceleration * dashVector.x, 0),
            abilityCreature.body.getWorldCenter,
            true
          )
        }
      }
      if (dashVector.y < 0f) {
        if (abilityCreature.body.getLinearVelocity.y >= -dashMaxVelocity) {
          abilityCreature.body.applyLinearImpulse(
            new Vector2(0, dashAcceleration * dashVector.y),
            abilityCreature.body.getWorldCenter,
            true
          )
        }
      } else if (dashVector.y > 0f) {
        if (abilityCreature.body.getLinearVelocity.y <= dashMaxVelocity) {
          abilityCreature.body.applyLinearImpulse(
            new Vector2(0, dashAcceleration * dashVector.y),
            abilityCreature.body.getWorldCenter,
            true
          )
        }
      }

    }
  }

  override def performOnUpdateStart(): Unit = {}

  def setDashVector(dashVector: Vector2): Unit = {
    this.dashVector = dashVector
  }

  override protected def onActiveStart(): Unit = {
    abilityCreature
      .getEffect("immobilized")
      .applyEffect(channelTime + activeTime)
    abilityCreature.takeStaminaDamage(15f)
  }
}

object DashAbility {
  def apply(abilityCreature: Creature, dashDistance: Float = 140f): DashAbility = {
    new DashAbility(abilityCreature, dashDistance)
  }
}
