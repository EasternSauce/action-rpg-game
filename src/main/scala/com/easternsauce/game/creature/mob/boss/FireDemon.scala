package com.easternsauce.game.creature.mob.boss

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.easternsauce.game.ability.{DashAbility, FistSlamAbility, MeteorCrashAbility, MeteorRainAbility}
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.SimpleTimer
import system.GameSystem

class FireDemon(override val id: String, override val mobSpawnPoint: MobSpawnPoint, val weaponType: String) extends Boss(id, mobSpawnPoint) {

  protected var meteorRainAbility: MeteorRainAbility = _
  protected var fistSlamAbility: FistSlamAbility = _
  protected var meteorCrashAbility: MeteorCrashAbility = _
  protected var dashAbility: DashAbility = _

  scale = 2.0f

  override val hitbox = new Rectangle(0, 0, 80 * scale, 80 * scale)

  actionTimer = SimpleTimer(true)

  dropTable.put("ironSword", 0.3f)
  dropTable.put("poisonDagger", 0.3f)
  dropTable.put("steelArmor", 0.8f)
  dropTable.put("steelHelmet", 0.5f)
  dropTable.put("thiefRing", 1.0f)

  loadSprites(Assets.fireDemonSpriteSheet, Map(Left -> 3, Right -> 1, Up -> 0, Down -> 2), 0)

  maxHealthPoints = 2500f
  healthPoints = maxHealthPoints

  name = "Magma Stalker"

  aggroDistance = 500f
  attackDistance = 500f
  walkUpDistance = 500f

  bossMusic = Assets.fireDemonMusic

  override val onGettingHitSound: Sound = Assets.roarSound

  override val baseSpeed = 0.1f

  creatureType = "fireDemon"

  override def performAggroedBehavior(): Unit = {
    if (!effectMap("immobilized").isActive && isNoAbilityActive && aggroedCreature.nonEmpty) {
      if (meteorRainAbility.canPerform && healthPoints < maxHealthPoints * 0.7) {
        meteorRainAbility.perform()
        Assets.monsterGrowlSound.play(0.5f)
      }
      else if (fistSlamAbility.canPerform && GameSystem.distance(aggroedCreature.get.body, body) < 80f) fistSlamAbility.perform()
      else if (meteorCrashAbility.canPerform && GameSystem.distance(aggroedCreature.get.body, body) > 220f) meteorCrashAbility.perform()
      else if (currentAttack.canPerform && GameSystem.distance(aggroedCreature.get.body, body) < 170f) currentAttack.perform()
      else if (dashAbility.canPerform && GameSystem.distance(aggroedCreature.get.body, body) > 300f) if (hasDestination) {
        dashAbility.setDashVector(new Vector2(destinationX - posX, destinationY - posY).nor())
        dashAbility.perform()
      }
    }
  }

  override protected def defineCustomAbilities(): Unit = {

    tridentAttack.attackRange = 45f
    tridentAttack.scale = 2.0f
    meteorRainAbility = MeteorRainAbility(this)
    fistSlamAbility = FistSlamAbility(this)
    meteorCrashAbility = MeteorCrashAbility(this)
    dashAbility = DashAbility(this)
    abilityList += meteorRainAbility
    abilityList += fistSlamAbility
    abilityList += meteorCrashAbility
    abilityList += dashAbility
  }

  override def onAggroed(): Unit = {
    if (!bossBattleStarted) {
      bossBattleStarted = true

      bossMusic.setVolume(0.1f)
      bossMusic.play()

      GameSystem.hud.bossHealthBar.onBossBattleStart(this)
      mobSpawnPoint.blockade.active = true
      Assets.monsterGrowlSound.play(0.1f)
    }
  }
}
