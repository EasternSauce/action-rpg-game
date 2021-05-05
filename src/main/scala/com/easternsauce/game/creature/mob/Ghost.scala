package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Rectangle
import com.easternsauce.game.ability.ExplosionAbility
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.SimpleTimer
import system.GameSystem

class Ghost(override val id: String, override val mobSpawnPoint: MobSpawnPoint, weaponType: String) extends Mob(id, mobSpawnPoint) {
  actionTimer = SimpleTimer(true)

  dropTable.put("ironSword", 0.03f)
  dropTable.put("poisonDagger", 0.005f)
  dropTable.put("healingPowder", 0.3f)
  dropTable.put("steelArmor", 0.03f)
  dropTable.put("steelGreaves", 0.05f)
  dropTable.put("steelGloves", 0.05f)
  dropTable.put("steelHelmet", 0.05f)


  loadSprites(Assets.ghostSpriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 0)


  override val hitbox = new Rectangle(17, 15, 30, 46)

  override val onGettingHitSound: Sound = Assets.evilYellingSound

  override val baseSpeed = 12f

  private var explodeAbility: ExplosionAbility = _


  creatureType = "ghost"

  maxHealthPoints = 300f
  healthPoints = maxHealthPoints

  override def performAggroedBehavior(): Unit = {
    super.performAggroedBehavior()

    assert(aggroedCreature.nonEmpty)

    if (GameSystem.distance(aggroedCreature.get.body, this.body) < (if (attackDistance == null.asInstanceOf[Float]) attackType.attackDistance else attackDistance)) {
      if (healthPoints <= maxHealthPoints * 0.50) if (explodeAbility.canPerform) explodeAbility.perform()
    }
  }

  override def onInit(): Unit = {
    super.onInit()

    explodeAbility = ExplosionAbility(this)

    explodeAbility.onChannelAction = () => {Assets.darkLaughSound.play(0.1f)}

    abilityList += explodeAbility
  }

  override def onDeath(): Unit = {
    super.onDeath()
  }
}
