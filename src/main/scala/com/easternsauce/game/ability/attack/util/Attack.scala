package com.easternsauce.game.ability.attack.util

import com.easternsauce.game.ability.Ability
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.wrappers.EsAnimation

abstract class Attack protected (override val abilityCreature: Creature) extends Ability(abilityCreature) {

  protected var abilityAnimation: EsAnimation
  protected var abilityWindupAnimation: EsAnimation

  isAttack = true

  override def onStop() {
    super.onStop()

    abilityCreature.isAttacking = false
  }
}
