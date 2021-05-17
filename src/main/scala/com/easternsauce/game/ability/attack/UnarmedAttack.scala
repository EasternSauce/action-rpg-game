package com.easternsauce.game.ability.attack

import com.easternsauce.game.ability.attack.util.MeleeAttack
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.wrappers.EsAnimation

class UnarmedAttack private (override val abilityCreature: Creature)
    extends MeleeAttack(abilityCreature) {

  val weaponSpeed = 1.0f

  val baseChannelTime = 0.3f
  val baseActiveTime = 0.3f
  val numOfChannelFrames = 6
  val numOfFrames = 6
  val channelFrameDuration: Float = baseChannelTime / numOfChannelFrames
  val frameDuration: Float = baseActiveTime / numOfFrames
  override var scale: Float = 1f
  override var attackRange: Float = 30f
  override protected var cooldownTime = 0.8f
  override protected var activeTime: Float = baseActiveTime * 1f / weaponSpeed
  override protected var channelTime: Float = baseChannelTime * 1f / weaponSpeed
  override protected var width: Float = 40f
  override protected var height: Float = 40f
  override protected var abilityAnimation: EsAnimation =
    new EsAnimation(Assets.slashSpriteSheet, frameDuration)
  override protected var abilityWindupAnimation: EsAnimation =
    new EsAnimation(Assets.slashWindupSpriteSheet, channelFrameDuration)
  override protected var knockbackPower: Float = 25f
  override protected var aimed: Boolean = false
}

object UnarmedAttack {
  def apply(creature: Creature) = new UnarmedAttack(creature)
}
