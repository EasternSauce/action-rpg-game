package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.ability.ExplodeAbility
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.CustomRectangle
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.Timer
import system.GameSystem

class Ghost(override val id: String, override val mobSpawnPoint: MobSpawnPoint, weaponType: String) extends Mob(id, mobSpawnPoint) {
  actionTimer = Timer(true)

  dropTable.put("ironSword", 0.03f)
  dropTable.put("poisonDagger", 0.005f)
  dropTable.put("healingPowder", 0.3f)
  dropTable.put("steelArmor", 0.03f)
  dropTable.put("steelGreaves", 0.05f)
  dropTable.put("steelGloves", 0.05f)
  dropTable.put("steelHelmet", 0.05f)


  loadSprites(Assets.ghostSpriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 0)


  override val hitbox = new CustomRectangle(17, 15, 30, 46)

  override val onGettingHitSound: Sound = Assets.evilYellingSound

  override val baseSpeed = 300f

  private var explodeAbility: ExplodeAbility = _


  creatureType = "goblin"

  maxHealthPoints = 300f
  healthPoints = maxHealthPoints

  //grantWeapon(weapon)

  override def performAggroedBehavior(): Unit = {
    super.performAggroedBehavior()

    assert(aggroedCreature.nonEmpty)

    if (GameSystem.distance(aggroedCreature.get.rect, this.rect) < (if (attackDistance == null.asInstanceOf[Float]) attackType.attackDistance else attackDistance)) {
      if (healthPoints <= maxHealthPoints * 0.50) if (explodeAbility.canPerform) explodeAbility.perform()
    }
  }

  override def onInit(): Unit = {
    super.onInit()

    explodeAbility = ExplodeAbility(this)

    explodeAbility.onChannelAction = () => {Assets.darkLaughSound.play(0.1f)}

    abilityList += explodeAbility
  }

  override def onDeath(): Unit = {
    isRunningAnimationActive = false

    GameSystem.lootSystem.spawnLootPile(area, rect.center.x, rect.center.y, dropTable)
    for (ability <- abilityList) {
      if (!ability.isInstanceOf[ExplodeAbility]) {
        ability.stopAbility()

      }
    }
    currentAttack.stopAbility()
  }
}
