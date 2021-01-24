package com.easternsauce.game.ability.attack

import com.easternsauce.game.animation.Animation
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.{AttackType, Sword}
import com.easternsauce.game.shapes.{Polygon, Rectangle}

class SwordAttack(override protected val abilityCreature: Creature) extends MeleeAttack(abilityCreature) {
    override val attackType: AttackType = Sword

  override def init(): Unit = {

    var weaponSpeed = 1.0f
    // TODO
//    if (this.abilityCreature.getEquipmentItems.get(0) != null) weaponSpeed = this.abilityCreature.getEquipmentItems.get(0).getItemType.getWeaponSpeed

    val baseChannelTime = 0.3f
    val baseActiveTime = 0.3f
    val numOfChannelFrames = 6
    val numOfFrames = 6
    val channelFrameDuration = baseChannelTime / numOfChannelFrames
    val frameDuration = baseActiveTime / numOfFrames

    channelTime = baseChannelTime * 1f / weaponSpeed

    activeTime = baseActiveTime * 1f / weaponSpeed

    cooldownTime = 0.8f

    val spriteWidth = 40 // TODO
    val spriteHeight = 40

    windupAnimation = new Animation(Assets.slashWindup, channelFrameDuration, spriteWidth, spriteHeight)
    attackAnimation = new Animation(Assets.slash, frameDuration, spriteWidth, spriteHeight)

    meleeAttackRect = new Rectangle(-999, -999, 1, 1)

    meleeAttackHitbox = new Polygon(meleeAttackRect)

    width = 40f
    height = 40f
    scale = 1f
    attackRange = 30f
    knockbackPower = 0.35f

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