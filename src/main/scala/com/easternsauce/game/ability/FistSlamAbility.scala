package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.components.Fist
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable.ListBuffer

class FistSlamAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  protected var fists: ListBuffer[Fist] = ListBuffer()


  override def init(): Unit = {
    cooldownTime = 6.5f
    activeTime = 4.0f
    channelTime = 0.35f
  }

  override protected def onActiveStart(): Unit = {
    abilityCreature.takeStaminaDamage(25f)
  }

  override protected def onUpdateActive(): Unit = {
    for (fist <- fists) {
      if (!fist.started && activeTimer.time > fist.startTime) {
        fist.start()
      }
      if (fist.started) {
        if (fist.state == AbilityState.Channeling) {
          if (fist.channelTimer.time > fist.channelTime) {
            fist.state = AbilityState.Active
            Assets.glassBreakSound.play(0.1f)
            fist.abilityAnimation.restart()
            fist.activeTimer.restart()
            fist.initBody(fist.posX, fist.posY)
          }
        }
        if (fist.state == AbilityState.Active) {
          if (fist.activeTimer.time > fist.activeTime) {
            fist.state = AbilityState.Inactive
          }
          if (!fist.destroyed && fist.activeTimer.time >= 0.2f) {
            fist.body.getWorld.destroyBody(fist.body)
            fist.destroyed = true
          }
          if (fist.activeTimer.time > fist.activeTime) {
            // on active stop
            fist.state = AbilityState.Inactive
          }
        }
      }
    }
  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Active) {
      for (fist <- fists) { //                g.setColor(Color.green);
        //                g.drawRect(fist.getHitbox().getX() - camera.getPosX(), fist.getHitbox().getY() - camera.getPosY(), 40 * fist.getScale(), 40 * fist.getScale());
        if (fist.state == AbilityState.Channeling) {
          val image = fist.windupAnimation.currentFrame

          println("rendering at: " + fist.posX + " " + fist.posY)
          val shift = image.getRegionWidth * fist.scale / 2f
          println("shift = " + shift + " , asdasf = " + image.getRegionWidth)
          batch.draw(image, fist.posX - shift, fist.posY - shift, 0, 0,
            image.getRegionWidth, image.getRegionHeight, fist.scale, fist.scale, 0.0f)
        }
        if (fist.state == AbilityState.Active) {

          val image = fist.abilityAnimation.currentFrame

          val shift = image.getRegionWidth * fist.scale / 2f

          batch.draw(image, fist.posX - shift, fist.posY - shift, 0, 0,
            image.getRegionWidth, image.getRegionHeight, fist.scale, fist.scale, 0.0f)
        }
      }
    }
  }

  override def onChannellingStart(): Unit = {
    abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime)
    fists = new ListBuffer[Fist]
    for (i <- 0 until 10) {
      val range: Int = 270
      fists += new Fist(this, 0.2f * i, abilityCreature.posX + GameSystem.random.between(-range, range), abilityCreature.posY + GameSystem.random.between(-range, range), 20)
    }
  }

}


object FistSlamAbility {
  def apply(creature: Creature): FistSlamAbility = {
    val ability = new FistSlamAbility(creature)
    ability.init()
    ability
  }

}