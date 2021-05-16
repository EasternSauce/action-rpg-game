package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.components.Fist
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.creature.Creature
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable.ListBuffer

class FistSlamAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  protected var fists: ListBuffer[Fist] = ListBuffer()

  override protected var cooldownTime: Float = 6.5f
  override protected var activeTime: Float = 3.0f
  override protected var channelTime: Float = 0.35f

  override protected def onActiveStart(): Unit = {
    abilityCreature.takeStaminaDamage(25f)
  }

  override protected def onUpdateActive(): Unit = {
    for (fist <- fists) {
      if (!fist.started && activeTimer.time > fist.startTime) {
        fist.start()
      }
      fist.onUpdateActive()
    }
  }



  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Active) {
      for (fist <- fists) {
        fist.render(shapeDrawer, batch)
      }
    }
  }



  override def onChannellingStart(): Unit = {
    abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime)
    fists = new ListBuffer[Fist]
    for (i <- 0 until 20) {
      val range: Int = 250
      val aggroedCreature = abilityCreature.aggroedCreature.get
      fists += Fist(this, 0.1f * i, aggroedCreature.posX + GameSystem.random.between(-range, range),
        aggroedCreature.posY + GameSystem.random.between(-range, range), 20)
    }
  }

}

object FistSlamAbility {
  def apply(creature: Creature) = new FistSlamAbility(creature)
}