package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.ability.components.Meteor
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable.ListBuffer

class MeteorRainAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  protected var explosionRange: Float = 0f

  protected var meteors: ListBuffer[Meteor] = _

  override def init(): Unit = {
    cooldownTime = 35f
    activeTime = 13f
    channelTime = 0.3f

    explosionRange = 300f
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

  override def onChannellingStart(): Unit = {
    abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime)
    meteors = ListBuffer[Meteor]()
    for (i <- 0 until 40) {
      val meteor = new Meteor(this, 0.3f * i, abilityCreature.posX + GameSystem.random.between(-700, 700), abilityCreature.posY + GameSystem.random.between(-700, 700), explosionRange, 1.5f)

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
}


object MeteorRainAbility {
  def apply(creature: Creature): MeteorRainAbility = {
    val ability = new MeteorRainAbility(creature)
    ability.init()
    ability
  }

}