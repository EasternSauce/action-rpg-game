package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.mob.Mob
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class ExplodeAbility(override protected val abilityCreature: Creature) extends Ability(abilityCreature) {

  protected var explosionAnimation = new EsAnimation(Assets.explosionSpriteSheet, 0, 0.05f)
  protected var explosionRange: Float = _

  override def init(): Unit = {
    cooldownTime = 0.8f
    activeTime = 0.9f
    channelTime = 1.3f
    explosionRange = 150f
  }

  override protected def onActiveStart(): Unit = {
    explosionAnimation.restart()
    abilityCreature.takeStaminaDamage(25f)
    abilityCreature.takeDamage(700f, immunityFrames = false, 0, 0, 0)
    Assets.explosionSound.play(0.07f)
  }

  override protected def onUpdateActive(): Unit = {
    val creatures = abilityCreature.area.creatures
    for ((_, creature) <- creatures) {
      if (creature != this.abilityCreature) {
        if (GameSystem.distance(creature.body, abilityCreature.body) < explosionRange && activeTimer.time < 0.1f) {
          if (!(this.abilityCreature.isInstanceOf[Mob] && creature.isInstanceOf[Mob]) && creature.alive) { // mob can't hurt a mob?
            if (!creature.isImmune) creature.takeDamage(700f, immunityFrames = true, 0, 0, 0)
          }
        }
      }
    }
  }

  override def onChannellingStart(): Unit = {
    abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime)
  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Active) {
      val spriteWidth = 64
      val scale = explosionRange * 2 / spriteWidth
      val image = explosionAnimation.currentFrame

      batch.draw(image, abilityCreature.centerPosX - image.getRegionWidth / 2f, abilityCreature.centerPosY - image.getRegionHeight / 2f, 0,0,
        image.getRegionWidth, image.getRegionHeight, scale, scale, 0.0f)
    }
  }
}

object ExplodeAbility {
  def apply(creature: Creature): ExplodeAbility = {
    val ability = new ExplodeAbility(creature)
    ability.init()
    ability
  }
}