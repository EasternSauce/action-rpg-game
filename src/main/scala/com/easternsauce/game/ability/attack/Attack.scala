package com.easternsauce.game.ability.attack

import com.easternsauce.game.ability.Ability
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.{AttackType, Unarmed}

abstract class Attack(override protected val abilityCreature: Creature) extends Ability(abilityCreature) {

  isAttack = true


  override def onStop() {
    super.onStop()

    abilityCreature.isAttacking = false
  }
}
