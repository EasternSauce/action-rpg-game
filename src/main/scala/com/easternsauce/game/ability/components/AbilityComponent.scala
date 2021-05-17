package com.easternsauce.game.ability.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.Body
import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.util.AbilityState.AbilityState
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.utils.EsTimer
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer

abstract class AbilityComponent protected (val mainAbility: Ability) {

  protected val activeTimer: EsTimer = EsTimer()
  protected val channelTimer: EsTimer = EsTimer()
  protected val activeTime: Float
  protected val channelTime: Float
  protected val abilityAnimation: EsAnimation
  protected val abilityWindupAnimation: EsAnimation

  var state: AbilityState
  var started: Boolean
  var body: Body
  var destroyed: Boolean

  def onUpdateActive(): Unit

  def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit

  def onCollideWithCreature(creature: Creature): Unit

}
