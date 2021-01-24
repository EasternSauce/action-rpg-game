package com.easternsauce.game.ability.attack

import com.easternsauce.game.creature.Creature

class UnarmedAttack(override protected val abilityCreature: Creature) extends MeleeAttack(abilityCreature) {
  override def init(): Unit = {

  }
}


object UnarmedAttack {
  def apply(creature: Creature): UnarmedAttack = {
    val attack = new UnarmedAttack(creature)
    attack.init()
    attack
  }
}