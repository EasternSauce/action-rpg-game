package com.easternsauce.game.ability.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.Ability
import com.easternsauce.game.creature.Creature
import space.earlygrey.shapedrawer.ShapeDrawer

abstract class AbilityComponent(val mainAbility: Ability) {

  def onUpdateActive(): Unit = {

  }

  def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {

  }

  def onCollideWithCreature(creature: Creature): Unit = {

  }
}
