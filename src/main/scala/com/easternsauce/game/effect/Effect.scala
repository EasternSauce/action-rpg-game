package com.easternsauce.game.effect

import com.easternsauce.game.utils.SimpleTimer

class Effect {

  protected var effectTimer: SimpleTimer = SimpleTimer()

  protected var effectEndTime = 0f

  protected var effectActive = false


  def applyEffect(effectTime: Float): Unit = {
    if (effectActive) {
      effectTimer.restart()
      val remainingTime = effectEndTime - effectTimer.time
      effectEndTime = Math.max(remainingTime, effectTime)
    }
    else {
      effectActive = true
      effectTimer.restart()
      effectEndTime = effectTime
    }
  }

  def isActive: Boolean = {
    effectActive
  }

  def update(): Unit = {
    if (effectActive && effectTimer.time > effectEndTime) effectActive = false
  }

  def getRemainingTime: Float = effectEndTime - effectTimer.time

  def stop(): Unit = {
    effectActive = false
    effectEndTime = 0
    effectTimer.stop()
    effectTimer.restart()
  }
}
