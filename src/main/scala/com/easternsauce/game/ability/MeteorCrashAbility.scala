package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.ability.components.Meteor
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.creature.Creature
import space.earlygrey.shapedrawer.ShapeDrawer

import scala.collection.mutable.ListBuffer

class MeteorCrashAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  protected var meteors: ListBuffer[Meteor] = ListBuffer()

  override def init(): Unit = {
    cooldownTime = 6.5f
    activeTime = 2f
    channelTime = 1.25f
  }

  override protected def onActiveStart(): Unit = {
    abilityCreature.takeStaminaDamage(25f)
    val creaturePos = abilityCreature.body.getPosition
    meteors = ListBuffer[Meteor]()
    val facingVector: Vector2 = abilityCreature.facingVector.nor()
    for (i <- 0 until 5) {
      meteors += new Meteor(this, 0.1f * i, creaturePos.x + (100 * (i + 1)) * facingVector.x, creaturePos.y + (100 * (i + 1)) * facingVector.y, 50 + 3 * i * i, 2.5f)
    }
//    for (i <- 0 until 5) {
//      val vector: Vector2 = facingVector.cpy()
//      vector.setAngleDeg(vector.angleDeg() + 50)
//      meteors.add(new Meteor(100 * i, new Rectangle(rect.getCenterX + (100 * (i + 1)) * vector.x, rect.getCenterY + (100 * (i + 1)) * vector.y, 1, 1), 50 + 3 * i * i, 2.5f))
//    }
//    for (i <- 0 until 5) {
//      val vector: Vector2 = facingVector.copy
//      vector.setTheta(vector.getTheta - 50)
//      meteors.add(new Meteor(100 * i, new Rectangle(rect.getCenterX + (100 * (i + 1)) * vector.x, rect.getCenterY + (100 * (i + 1)) * vector.y, 1, 1), 50 + 3 * i * i, 2.5f))
//    }
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
  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Active) {
      for (meteor <- meteors) {
        meteor.render(shapeDrawer, batch)
      }
    }
  }
}


object MeteorCrashAbility {
  def apply(creature: Creature): MeteorCrashAbility = {
    val ability = new MeteorCrashAbility(creature)
    ability.init()
    ability
  }

}