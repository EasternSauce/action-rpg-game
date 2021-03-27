package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.ability.util.AbilityState.{AbilityState, Inactive}
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.{CustomPolygon, CustomRectangle}
import com.easternsauce.game.utils.SimpleTimer
import space.earlygrey.shapedrawer.ShapeDrawer

abstract class Ability(val abilityCreature: Creature) {

  protected var activeTimer: SimpleTimer = SimpleTimer()
  protected var channelTimer: SimpleTimer = SimpleTimer()

  protected var cooldownTime = 0f
  protected var activeTime = 0f
  protected var channelTime = 0f

  protected var meleeAttackRect: CustomRectangle = _

  protected var isAttack = false

  protected val isStoppable: Boolean = true


  var state: AbilityState = Inactive
  var onCooldown = false

  var onPerformAction: () => Unit = () => {}
  var onChannelAction: () => Unit = () => {}


  def init()

  def onUpdateHitbox(): Unit = {

  }

  def update(): Unit = {

    if ((state == AbilityState.Channeling) && channelTimer.time > channelTime) {
      state = AbilityState.Active
      onActiveStart()
      onPerformAction()
      activeTimer.restart()
      onCooldown = true
    }
    if ((state == AbilityState.Active) && activeTimer.time > activeTime) {
      state = AbilityState.Inactive
      onStop()
    }

    if (state == AbilityState.Channeling || state == AbilityState.Active) {
      onUpdateHitbox()
    }

    if (state == AbilityState.Channeling) onUpdateChanneling()
    else if (state == AbilityState.Active) onUpdateActive()



    if ((state == AbilityState.Inactive) && onCooldown) if (activeTimer.time > cooldownTime) onCooldown = false
  }

  protected def onActiveStart(): Unit = {
  }

  protected def onStop(): Unit = {
  }

  protected def onUpdateActive(): Unit = {
  }

  protected def onUpdateChanneling(): Unit = {
  }

  def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {

  }


  def stopAbility(): Unit = {
    if (isStoppable) {
      state = AbilityState.Inactive

    }
  }

  def isOnCooldown: Boolean = onCooldown

  def onChannellingStart(): Unit = {
  }

  def perform(): Unit = {
    channelTimer.restart()
    state = AbilityState.Channeling
    onChannellingStart()
    onChannelAction()

    if (isAttack) { // + 10 to ensure regen doesnt start if we hold attack button
      abilityCreature.getEffect("staminaRegenStopped").applyEffect(channelTime + cooldownTime + 0.001f)
    }
    else abilityCreature.getEffect("staminaRegenStopped").applyEffect(1f)
  }

  def performMovement(): Unit = {
  }

  def performOnUpdateStart(): Unit = {
  }

  def canPerform: Boolean = {
    abilityCreature.staminaPoints > 0 && (state == AbilityState.Inactive) && !onCooldown
  }

  def active: Boolean = {
    state == AbilityState.Active
  }

  def onCollideWithCreature(creature: Creature): Unit = {

  }

}

