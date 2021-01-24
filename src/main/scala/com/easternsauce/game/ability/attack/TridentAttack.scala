package com.easternsauce.game.ability.attack

import com.easternsauce.game.creature.Creature

class TridentAttack(override protected val abilityCreature: Creature) extends MeleeAttack(abilityCreature) {
  override def init(): Unit = {

  }
}


object TridentAttack {
  def apply(creature: Creature): TridentAttack = {
    val attack = new TridentAttack(creature)
    attack.init()
    attack
  }
}