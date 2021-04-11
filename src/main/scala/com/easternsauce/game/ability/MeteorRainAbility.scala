package com.easternsauce.game.ability

import com.easternsauce.game.creature.Creature

class MeteorRainAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  override def init(): Unit = {

  }
}


object MeteorRainAbility {
  def apply(creature: Creature): MeteorRainAbility = {
    val ability = new MeteorRainAbility(creature)
    ability.init()
    ability
  }

}