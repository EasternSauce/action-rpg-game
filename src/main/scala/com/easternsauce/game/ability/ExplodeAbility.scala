package com.easternsauce.game.ability

import com.easternsauce.game.animation.Animation
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature

class ExplodeAbility(override protected val abilityCreature: Creature) extends Ability(abilityCreature) {

  protected var explosionAnimation = new Animation(Assets.slashWindupSpriteSheet, 100, 64, 64)
  protected var explosionRange = .0

  override def init(): Unit = {

  }

  // TODO
}
