package com.easternsauce.game.ability.attack

import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.CustomPolygon
import com.easternsauce.game.wrappers.EsAnimation

class SwordAttack(override val abilityCreature: Creature) extends MeleeAttack(abilityCreature) {

  override def init(): Unit = {

    var weaponSpeed = 1.0f

    if (abilityCreature.equipmentItems.contains(0)) {
      weaponSpeed = abilityCreature.equipmentItems(0).itemType.weaponSpeed
    }

    val baseChannelTime = 0.3f
    val baseActiveTime = 0.3f
    val numOfChannelFrames = 6
    val numOfFrames = 6
    val channelFrameDuration = baseChannelTime / numOfChannelFrames
    val frameDuration = baseActiveTime / numOfFrames

    channelTime = baseChannelTime * 1f / weaponSpeed

    activeTime = baseActiveTime * 1f / weaponSpeed

    cooldownTime = 0.8f

    windupAnimation = new EsAnimation(Assets.slashWindupSpriteSheet, 0, channelFrameDuration)
    
    activeAnimation = new EsAnimation(Assets.slashSpriteSheet,0, frameDuration)

    width = 40f
    height = 40f
    scale = 2f
    attackRange = 30f
    knockbackPower = 1f

    aimed = false
  }
}

object SwordAttack {
  def apply(creature: Creature): SwordAttack = {
    val attack = new SwordAttack(creature)
    attack.init()
    attack
  }
}
