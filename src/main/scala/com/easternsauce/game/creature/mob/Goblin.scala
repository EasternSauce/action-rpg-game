package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.CustomRectangle
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.SimpleTimer

class Goblin(override val id: String, override val mobSpawnPoint: MobSpawnPoint, weaponType: String) extends Mob(id, mobSpawnPoint) {
  actionTimer = SimpleTimer(true)

  dropTable.put("ironSword", 0.03f)
  dropTable.put("poisonDagger", 0.005f)
  dropTable.put("healingPowder", 0.3f)
  dropTable.put("steelArmor", 0.03f)
  dropTable.put("steelGreaves", 0.05f)
  dropTable.put("steelGloves", 0.05f)
  dropTable.put("steelHelmet", 0.05f)

  loadSprites(Assets.goblinSpriteSheet, Map(Left -> 1, Right -> 3, Up -> 2, Down -> 4), 0)

  override val hitbox = new CustomRectangle(17, 15, 30, 46)

  override val onGettingHitSound: Sound = Assets.evilYellingSound

  override val baseSpeed = 12f

  creatureType = "goblin"

  maxHealthPoints = 10f
  healthPoints = maxHealthPoints

  //grantWeapon(weapon)
}
