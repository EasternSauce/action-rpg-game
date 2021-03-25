package com.easternsauce.game.ability.attack

import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.{CustomPolygon, CustomRectangle}
import com.easternsauce.game.wrappers.EsAnimation

class TridentAttack(override val abilityCreature: Creature) extends MeleeAttack(abilityCreature) {
  override def init(): Unit = {
    var weaponSpeed = 1.0f

    //TODO
    //if (this.abilityCreature.equipmentItems(0) != null) weaponSpeed = this.abilityCreature.equipmentItems(0).itemType.weaponSpeed


    val baseChannelTime = 0.6f
    val baseActiveTime = 0.275f
    val numOfChannelFrames = 7
    val numOfFrames = 11
    val channelFrameDuration = baseChannelTime / numOfChannelFrames
    val frameDuration = baseActiveTime / numOfFrames

    channelTime = baseChannelTime / weaponSpeed
    activeTime = baseActiveTime / weaponSpeed

    cooldownTime = 0.7f

    val spriteWidth = 64
    val spriteHeight = 32

    windupAnimation = new EsAnimation(Assets.tridentThrustWindupSpriteSheet, 0, channelFrameDuration)
    attackAnimation = new EsAnimation(Assets.tridentThrustSpriteSheet, 0, frameDuration)

    meleeAttackRect = new CustomRectangle(-999, -999, 1, 1)

    width = 64f
    height = 32f
    scale = 2f
    attackRange = 30f
    knockbackPower = 30f

    aimed = false
  }
}


object TridentAttack {
  def apply(creature: Creature): TridentAttack = {
    val attack = new TridentAttack(creature)
    attack.init()
    attack
  }
}