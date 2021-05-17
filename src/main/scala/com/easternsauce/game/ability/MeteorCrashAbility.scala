package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.ability.components.Meteor
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.creature.Creature
import space.earlygrey.shapedrawer.ShapeDrawer

import scala.collection.mutable.ListBuffer

class MeteorCrashAbility private (override val abilityCreature: Creature) extends Ability(abilityCreature) {
  override protected var channelTime: Float = 1.25f
  override protected var activeTime: Float = 2f
  override protected var cooldownTime: Float = 6.5f
  protected var meteors: ListBuffer[Meteor] = ListBuffer()

  override def onChannellingStart(): Unit = {
    abilityCreature
      .getEffect("immobilized")
      .applyEffect(channelTime + activeTime)
  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Active) {
      for (meteor <- meteors) {
        meteor.render(shapeDrawer, batch)
      }
    }
  }

  override protected def onActiveStart(): Unit = {
    abilityCreature.takeStaminaDamage(25f)
    meteors = ListBuffer[Meteor]()
    val facingVector: Vector2 = abilityCreature.facingVector.nor()
    for (i <- 0 until 10) {
      meteors += Meteor(
        this,
        0.1f * i,
        abilityCreature.posX + (100 * (i + 1)) * facingVector.x,
        abilityCreature.posY + (100 * (i + 1)) * facingVector.y,
        50 + 3 * i * i,
        2.5f
      )
    }
    for (i <- 0 until 10) {
      val vector: Vector2 = facingVector.cpy()
      vector.setAngleDeg(vector.angleDeg() + 50)
      meteors += Meteor(
        this,
        0.1f * i,
        abilityCreature.posX + (100 * (i + 1)) * vector.x,
        abilityCreature.posY + (100 * (i + 1)) * vector.y,
        50 + 3 * i * i,
        2.5f
      )
    }
    for (i <- 0 until 10) {
      val vector: Vector2 = facingVector.cpy()
      vector.setAngleDeg(vector.angleDeg() - 50)
      meteors += Meteor(
        this,
        0.1f * i,
        abilityCreature.posX + (100 * (i + 1)) * vector.x,
        abilityCreature.posY + (100 * (i + 1)) * vector.y,
        50 + 3 * i * i,
        2.5f
      )
    }
  }

  override protected def onUpdateActive(): Unit = {
    for (meteor <- meteors) {
      if (!meteor.started && activeTimer.time > meteor.startTime) {
        meteor.start()
      }

      meteor.onUpdateActive()
    }
  }
}

object MeteorCrashAbility {
  def apply(creature: Creature) = new MeteorCrashAbility(creature)
}
