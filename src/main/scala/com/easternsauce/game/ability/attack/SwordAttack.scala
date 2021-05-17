package com.easternsauce.game.ability.attack

import com.easternsauce.game.ability.attack.util.MeleeAttack
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.wrappers.EsAnimation

class SwordAttack private (override val abilityCreature: Creature)
    extends MeleeAttack(abilityCreature) {

  private val baseChannelTime = 0.3f

  var weaponSpeed = 1.0f
  if (abilityCreature.equipmentItems.contains(0)) {
    weaponSpeed = abilityCreature.equipmentItems(0).itemType.weaponSpeed
  }
  private val baseActiveTime = 0.3f
  private val numOfChannelFrames = 6
  private val numOfFrames = 6
  private val channelFrameDuration: Float = baseChannelTime / numOfChannelFrames
  private val frameDuration: Float = baseActiveTime / numOfFrames
  override var scale: Float = 2f
  override var attackRange: Float = 30f
  override protected var cooldownTime: Float = 0.8f
  override protected var channelTime: Float = baseChannelTime * 1f / weaponSpeed
  override protected var activeTime: Float = baseActiveTime * 1f / weaponSpeed
  override protected var abilityAnimation: EsAnimation =
    new EsAnimation(Assets.slashSpriteSheet, frameDuration)
  override protected var abilityWindupAnimation: EsAnimation =
    new EsAnimation(Assets.slashWindupSpriteSheet, channelFrameDuration)
  override protected var width: Float = 40f
  override protected var height: Float = 40f
  override protected var knockbackPower: Float = 1f
  override protected var aimed: Boolean = false

}

object SwordAttack {
  def apply(creature: Creature) = new SwordAttack(creature)
}
