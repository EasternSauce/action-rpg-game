package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.CustomRectangle
import com.easternsauce.game.utils.Timer

class Skeleton(id: String) extends Mob(id) {
  override val rect = new CustomRectangle(0,4500,64,64)
  override val hitboxBounds = new CustomRectangle(18, 0, 28, 64)
  override val baseSpeed = 300f

  override protected val onGettingHitSound: Sound = Assets.painSound

  loadSprites(Assets.skeleton, Map(Left -> 2, Right -> 4, Up -> 1, Down -> 3), 0)


  actionTimer = Timer(true)

  dropTable.put("ringmailGreaves", 0.1f)
  dropTable.put("leatherArmor", 0.05f)
  dropTable.put("hideGloves", 0.1f)
  dropTable.put("leatherHelmet", 0.1f)
  dropTable.put("woodenSword", 0.1f)
  dropTable.put("healingPowder", 0.5f)

  loadSprites(Assets.skeleton, Map(Left -> 2, Right -> 4, Up -> 1, Down -> 3), 0)

  override def hitbox: CustomRectangle = new CustomRectangle(17, 15, 30, 46);

  //onGettingHitSound = Assets.boneClickSound

  creatureType = "skeleton"

  maxHealthPoints = 200f
  healthPoints = maxHealthPoints

  //grantWeapon(weapon)


}
