package com.easternsauce.game.creature.mob.boss

import com.badlogic.gdx.audio.Music
import com.easternsauce.game.creature.mob.Mob
import com.easternsauce.game.shapes.CustomVector2
import com.easternsauce.game.spawn.MobSpawnPoint
import system.GameSystem

class Boss(override val id: String, override val mobSpawnPoint: MobSpawnPoint) extends Mob(id, mobSpawnPoint) {
  protected var bossBattleStarted = false
  var bossMusic: Music = _

  isBoss = true
  bossBattleStarted = false
  knocbackable = false


  override def onAggroed(): Unit = {
    if (!bossBattleStarted) {
      bossBattleStarted = true
      bossMusic.setLooping(true)
      bossMusic.setVolume(0.3f)
      bossMusic.play()
      GameSystem.hud.bossHealthBar.onBossBattleStart(this)
      mobSpawnPoint.blockade.active = true
    }
  }

  override def onDeath(): Unit = {
    GameSystem.lootSystem.spawnLootPile(area, centerPosX, centerPosY, dropTable)
    for (ability <- abilityList) {
      ability.stopAbility()
    }
    currentAttack.stopAbility()
    bossMusic.stop()
    if (GameSystem.hud.bossHealthBar.boss == this) GameSystem.hud.bossHealthBar.hide()
    mobSpawnPoint.blockade.active = false
  }

  override def performIdleBehavior(): Unit = {
    // stay put
  }

  override def takeDamage(damage: Float, immunityFrames: Boolean, knockbackPower: Float, sourceX: Float, sourceY: Float): Unit = {
    if (alive) {
      val beforeHP = healthPoints
      val actualDamage = damage * 100f / (100f + totalArmor)
      if (healthPoints - actualDamage > 0) healthPoints -= actualDamage
      else healthPoints = 0f
      if (beforeHP != healthPoints && healthPoints == 0f) onDeath()
      effectMap("immune").applyEffect(500)
      if (knocbackable && !knockback && knockbackPower > 0f) {
        this.knockbackPower = knockbackPower
        knockbackVector = new CustomVector2(posX - sourceX, posY - sourceY).normal
        knockback = true
        knockbackTimer.restart()
      }
      if (GameSystem.random.nextFloat() < 0.3) onGettingHitSound.play(0.1f)
    }
  }
}
