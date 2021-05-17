package com.easternsauce.game.ability.attack

import com.easternsauce.game.ability.attack.util.MeleeAttack
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.wrappers.EsAnimation

class TridentAttack private (override val abilityCreature: Creature) extends MeleeAttack(abilityCreature) {

  private val baseChannelTime: Float = 0.6f

  private var weaponSpeed = 1.0f
  if (abilityCreature.equipmentItems.contains(0)) {
    weaponSpeed = abilityCreature.equipmentItems(0).itemType.weaponSpeed
  }
  private val baseActiveTime: Float = 0.275f
  private val numOfChannelFrames: Int = 7
  private val numOfFrames: Int = 11
  private val channelFrameDuration: Float = baseChannelTime / numOfChannelFrames
  private val frameDuration: Float = baseActiveTime / numOfFrames
  override var scale: Float = 2f
  override var attackRange: Float = 30f
  override protected var channelTime: Float = baseChannelTime / weaponSpeed
  override protected var activeTime: Float = baseActiveTime / weaponSpeed
  override protected var cooldownTime = 0.7f
  override protected var width: Float = 64f
  override protected var height: Float = 32f
  override protected var abilityAnimation: EsAnimation =
    new EsAnimation(Assets.tridentThrustSpriteSheet, frameDuration)
  override protected var abilityWindupAnimation: EsAnimation =
    new EsAnimation(Assets.tridentThrustWindupSpriteSheet, channelFrameDuration)
  override protected var knockbackPower: Float = 30f
  override protected var aimed: Boolean = false
}

object TridentAttack {
  def apply(creature: Creature) = new TridentAttack(creature)
}
