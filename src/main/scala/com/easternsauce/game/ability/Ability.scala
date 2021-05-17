package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.ability.util.AbilityState.{AbilityState, Inactive}
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.utils.EsTimer
import space.earlygrey.shapedrawer.ShapeDrawer

abstract class Ability(val abilityCreature: Creature) {

  protected val isStoppable: Boolean = true
  var state: AbilityState = Inactive
  var onCooldown = false
  var onPerformAction: () => Unit = () => {}
  var onChannelAction: () => Unit = () => {}
  protected var activeTimer: EsTimer = EsTimer()
  protected var channelTimer: EsTimer = EsTimer()
  protected var cooldownTime: Float
  protected var activeTime: Float
  protected var channelTime: Float
  protected var isAttack = false

  def update(): Unit = {

    if ((state == AbilityState.Channeling) && channelTimer.time > channelTime) {
      state = AbilityState.Active
      onActiveStart()
      onPerformAction()
      activeTimer.restart()
      onCooldown = true
    }
    if ((state == AbilityState.Active) && activeTimer.time > activeTime) {
      onStop()

      state = AbilityState.Inactive
    }

    if (state == AbilityState.Channeling || state == AbilityState.Active) {
      updateHitbox()
    }

    if (state == AbilityState.Channeling) onUpdateChanneling()
    else if (state == AbilityState.Active) onUpdateActive()

    if ((state == AbilityState.Inactive) && onCooldown)
      if (activeTimer.time > cooldownTime) onCooldown = false
  }

  def updateHitbox(): Unit = {}

  protected def onActiveStart(): Unit = {}

  protected def onUpdateActive(): Unit = {}

  protected def onUpdateChanneling(): Unit = {}

  def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {}

  def forceStop(): Unit = {
    if (isStoppable && state != AbilityState.Inactive) {
      onStop()

      state = AbilityState.Inactive
    }
  }

  protected def onStop(): Unit = {}

  def isOnCooldown: Boolean = onCooldown

  def perform(): Unit = {
    channelTimer.restart()
    state = AbilityState.Channeling
    onChannellingStart()
    onChannelAction()

    if (isAttack) { // + 0.01 to ensure regen doesnt start if we hold attack button
      abilityCreature
        .getEffect("staminaRegenStopped")
        .applyEffect(channelTime + cooldownTime + 0.01f)
    } else abilityCreature.getEffect("staminaRegenStopped").applyEffect(1f)
  }

  def onChannellingStart(): Unit = {}

  def performMovement(): Unit = {}

  def performOnUpdateStart(): Unit = {}

  def canPerform: Boolean = {
    abilityCreature.staminaPoints > 0 && (state == AbilityState.Inactive) && !onCooldown
  }

  def active: Boolean = {
    state == AbilityState.Active
  }

  def onCollideWithCreature(creature: Creature): Unit = {}

}
