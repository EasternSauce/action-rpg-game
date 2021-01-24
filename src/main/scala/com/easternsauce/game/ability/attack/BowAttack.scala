package com.easternsauce.game.ability.attack

import com.easternsauce.game.creature.Creature

class BowAttack(override protected val abilityCreature: Creature) extends Attack(abilityCreature) {
  override def init(): Unit = {

  }
}

object BowAttack {
  def apply(creature: Creature): BowAttack = {
    val attack = new BowAttack(creature)
    attack.init()
    attack
  }
}
