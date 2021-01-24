package com.easternsauce.game.creature.traits

import com.easternsauce.game.utils.Timer

trait CreatureTimers {
  protected var healthRegenTimer: Timer = Timer(true)
  protected var staminaRegenTimer: Timer = Timer(true)
  protected var poisonTickTimer: Timer = Timer()
  protected var staminaOveruseTimer: Timer = Timer()
  protected var healingTimer: Timer = Timer()
  protected var healingTickTimer: Timer = Timer()
  protected var knockbackTimer: Timer = Timer()

}
