package com.easternsauce.game.ability

import java.util
import java.util.Collection

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.attack.UnarmedAttack
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.animation.Animation
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.mob.Mob
import system.GameSystem

class ExplodeAbility(override protected val abilityCreature: Creature) extends Ability(abilityCreature) {

  protected var explosionAnimation = new Animation(Assets.explosionSpriteSheet, 0.05f, 64, 64)
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
        if (GameSystem.distance(abilityCreature.rect, creature.rect) < explosionRange && activeTimer.time < 0.1f) {
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

  override def render(spriteBatch: SpriteBatch): Unit = {
    if (state == AbilityState.Active) {
      val spriteWidth = 64
      val scale = explosionRange * 2 / spriteWidth
      val image = explosionAnimation.currentFrame()
      image.setCenterX(abilityCreature.rect.center.x)
      image.setCenterY(abilityCreature.rect.center.y)
      image.setScale(scale)
      image.draw(spriteBatch)
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