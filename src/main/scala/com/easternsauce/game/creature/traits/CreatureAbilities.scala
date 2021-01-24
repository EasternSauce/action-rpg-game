package com.easternsauce.game.creature.traits

import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.attack._

import scala.collection.mutable

trait CreatureAbilities {
  protected var bowAttack: BowAttack = _
  protected var unarmedAttack: UnarmedAttack = _
  protected var swordAttack: SwordAttack = _
  protected var tridentAttack: TridentAttack = _

  var abilityList: mutable.ListBuffer[Ability] = _
  var attackList: mutable.ListBuffer[Attack] = _

  var currentAttack: Attack = _

}
