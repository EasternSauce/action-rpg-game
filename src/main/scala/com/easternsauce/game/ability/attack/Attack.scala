package com.easternsauce.game.ability.attack

import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.ability_components.AbilityComponent
import com.easternsauce.game.creature.Creature

abstract class Attack(override val abilityCreature: Creature) extends Ability(abilityCreature) {

  isAttack = true

  var attackComponent: AbilityComponent = _

  override def onStop() {
    super.onStop()

    abilityCreature.isAttacking = false
  }
}
