package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.ability.util.AbilityState.{AbilityState, Inactive}
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.{Polygon, Rectangle}
import com.easternsauce.game.utils.Timer

abstract class Ability(protected val abilityCreature: Creature) {
  protected var onPerformAction: () => Unit = () => {}
  protected var onChannelAction: () => Unit = () => {}

  protected var activeTimer: Timer = Timer()
  protected var channelTimer: Timer = Timer()

  protected var cooldownTime = 0f
  protected var activeTime = 0f
  protected var channelTime = 0f

  protected var meleeAttackRect: Rectangle = _

  var meleeAttackHitbox: Polygon = _


  protected var isAttack = false


  var state: AbilityState = Inactive
  var onCooldown = false


  def init()

  def update(): Unit = {

    if ((state == AbilityState.Channeling) && channelTimer.time > channelTime) {
      state = AbilityState.Active
      onActiveStart()
      onPerformAction()
      activeTimer.resetStart()
      onCooldown = true
    }
    if ((state == AbilityState.Active) && activeTimer.time > activeTime) {
      state = AbilityState.Inactive
      onStop()
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

  def renderSprites(batch: SpriteBatch): Unit = {

  }

  def stopAbility(): Unit = {
    state = AbilityState.Inactive
  }

  def isOnCooldown: Boolean = onCooldown

  def onChannellingStart(): Unit = {
  }

  def perform(): Unit = {
    channelTimer.resetStart()
    state = AbilityState.Channeling
    onChannellingStart()
    onChannelAction()
    //if (isAttack) { // + 10 to ensure regen doesnt start if we hold attack button
//      abilityCreature.getEffect("staminaRegenStopped").applyEffect(channelTime + cooldownTime + 10)
   // }
//    else abilityCreature.getEffect("staminaRegenStopped").applyEffect(1000)
  }

  def performMovement(): Unit = {
  }

  def performOnUpdateStart(i: Int): Unit = {
  }

  def canPerform: Boolean = {
    abilityCreature.staminaPoints > 0 && (state == AbilityState.Inactive) && !onCooldown
  }

  def active: Boolean = {
    // TODO
    false
  }

}
