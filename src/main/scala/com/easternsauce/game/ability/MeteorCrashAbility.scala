package com.easternsauce.game.ability

import com.easternsauce.game.creature.Creature

class MeteorCrashAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {
  override def init(): Unit = {

  }
}


object MeteorCrashAbility {
  def apply(creature: Creature): MeteorCrashAbility = {
    val ability = new MeteorCrashAbility(creature)
    ability.init()
    ability
  }

}