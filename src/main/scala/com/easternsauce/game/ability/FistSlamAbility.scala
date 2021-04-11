package com.easternsauce.game.ability

import com.easternsauce.game.creature.Creature

class FistSlamAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  override def init(): Unit = {

  }
}

object FistSlamAbility {
  def apply(creature: Creature): FistSlamAbility = {
    val ability = new FistSlamAbility(creature)
    ability.init()
    ability
  }

}