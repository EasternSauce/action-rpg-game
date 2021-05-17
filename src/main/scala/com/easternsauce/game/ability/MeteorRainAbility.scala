package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.components.Meteor
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.creature.Creature
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable.ListBuffer

class MeteorRainAbility private (override val abilityCreature: Creature) extends Ability(abilityCreature) {
  override protected var channelTime: Float = 0.3f
  override protected var activeTime: Float = 13f
  override protected var cooldownTime = 35f
  protected var explosionRange: Float = 300f
  protected var meteors: ListBuffer[Meteor] = _

  override def onChannellingStart(): Unit = {
    abilityCreature
      .getEffect("immobilized")
      .applyEffect(channelTime + activeTime)
    meteors = ListBuffer[Meteor]()
    for (i <- 0 until 60) {
      val range = 1100
      val meteor = Meteor(
        this,
        0.15f * i,
        abilityCreature.posX + GameSystem.random.between(-range, range),
        abilityCreature.posY + GameSystem.random.between(-range, range),
        explosionRange,
        1.5f
      )

      meteors += meteor
    }
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

object MeteorRainAbility {
  def apply(creature: Creature) = new MeteorRainAbility(creature)
}
