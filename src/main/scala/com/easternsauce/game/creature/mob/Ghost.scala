package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.CustomRectangle
import com.easternsauce.game.utils.Timer

class Ghost(id: String) extends Mob(id) {
  actionTimer = Timer(true)

  dropTable.put("ironSword", 0.03f)
  dropTable.put("poisonDagger", 0.005f)
  dropTable.put("healingPowder", 0.3f)
  dropTable.put("steelArmor", 0.03f)
  dropTable.put("steelGreaves", 0.05f)
  dropTable.put("steelGloves", 0.05f)
  dropTable.put("steelHelmet", 0.05f)


  loadSprites(Assets.ghost, Map(Left -> 2, Right -> 4, Up -> 1, Down -> 3), 0)


  override val hitbox = new CustomRectangle(17, 15, 30, 46)

  override val onGettingHitSound: Sound = Assets.painSound//TODO: Assets.evilYellingSound

  override val baseSpeed = 300f

  creatureType = "goblin"

  maxHealthPoints = 300f
  healthPoints = maxHealthPoints

  //grantWeapon(weapon)
}
