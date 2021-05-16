package com.easternsauce.game.creature.mob.boss

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.easternsauce.game.ability.{DashAbility, FistSlamAbility, MeteorCrashAbility, MeteorRainAbility}
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.EsTimer
import system.GameSystem

class FireDemon(override val id: String, override val mobSpawnPoint: MobSpawnPoint, val weaponType: String) extends Boss(id, mobSpawnPoint) {

  protected var meteorRainAbility: MeteorRainAbility = _
  protected var fistSlamAbility: FistSlamAbility = _
  protected var meteorCrashAbility: MeteorCrashAbility = _
  protected var dashAbility: DashAbility = _

  override val scale = 3.0f

  override val hitbox = new Rectangle(0, 0, 80 * scale, 80 * scale)

  actionTimer = EsTimer(true)

  dropTable.put("ironSword", 0.3f)
  dropTable.put("poisonDagger", 0.3f)
  dropTable.put("steelArmor", 0.8f)
  dropTable.put("steelHelmet", 0.5f)
  dropTable.put("thiefRing", 1.0f)

  loadSprites(Assets.fireDemonSpriteSheet, Map(Left -> 3, Right -> 1, Up -> 0, Down -> 2), 0)

  maxHealthPoints = 4000f
  healthPoints = maxHealthPoints

  name = "Magma Stalker"

  aggroDistance = 800f
  attackDistance = 200f
  walkUpDistance = 800f

  bossMusic = Assets.fireDemonMusic

  override val onGettingHitSound: Sound = Assets.roarSound

  override val baseSpeed = 25f

  override val mass: Float = 10000f

  creatureType = "fireDemon"

  grantWeapon("demonTrident")

  override def performAggroedBehavior(): Unit = {
    super.performAggroedBehavior()

    if (!effectMap("immobilized").isActive && isNoAbilityActive && aggroedCreature.nonEmpty) {
      if (meteorRainAbility.canPerform && healthPoints < maxHealthPoints * 0.65f) {
        meteorRainAbility.perform()
        Assets.monsterGrowlSound.play(0.3f)
      }
      else if (dashAbility.canPerform && GameSystem.distance(aggroedCreature.get.body, body) > 500f && healthPoints < maxHealthPoints * 0.9f) {
        dashAbility.setDashVector(new Vector2(destinationX - posX, destinationY - posY).nor())
        dashAbility.perform()
      }
      else if (fistSlamAbility.canPerform && GameSystem.distance(aggroedCreature.get.body, body) < 120f) fistSlamAbility.perform()
      else if (meteorCrashAbility.canPerform && GameSystem.distance(aggroedCreature.get.body, body) > 220f) meteorCrashAbility.perform()

    }
  }

  override protected def defineCustomAbilities(): Unit = {

    tridentAttack.attackRange = 45f
    tridentAttack.scale = 2.5f
    meteorRainAbility = MeteorRainAbility(this)
    fistSlamAbility = FistSlamAbility(this)
    meteorCrashAbility = MeteorCrashAbility(this)
    dashAbility = DashAbility(this, 400f)
    abilityList += meteorRainAbility
    abilityList += fistSlamAbility
    abilityList += meteorCrashAbility
    abilityList += dashAbility
  }

  override def onAggroed(): Unit = {
    if (!bossBattleStarted) {
      bossBattleStarted = true

      bossMusic.setVolume(0.1f)
      bossMusic.setLooping(true)
      bossMusic.play()

      GameSystem.hud.bossHealthBar.onBossBattleStart(this)
      mobSpawnPoint.blockade.active = true
      Assets.monsterGrowlSound.play(0.1f)
    }
  }
}
