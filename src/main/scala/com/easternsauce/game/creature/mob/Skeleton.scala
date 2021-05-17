package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Rectangle
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.EsTimer

class Skeleton private (override val id: String, override val mobSpawnPoint: MobSpawnPoint, val weaponType: String)
    extends Mob(id, mobSpawnPoint) {
  override val hitboxBounds = new Rectangle(18, 0, 28, 64)
  override val baseSpeed = 12f

  override val mass = 300f

  override protected val onGettingHitSound: Sound = Assets.boneClickSound

  loadSprites(Assets.skeletonSpriteSheet, Map(Left -> 2, Right -> 4, Up -> 1, Down -> 3), 0)

  override val scale = 1.35f

  actionTimer = EsTimer(true)

  dropTable.put("ringmailGreaves", 0.1f)
  dropTable.put("leatherArmor", 0.05f)
  dropTable.put("hideGloves", 0.1f)
  dropTable.put("leatherHelmet", 0.1f)
  dropTable.put("woodenSword", 0.1f)
  dropTable.put("healingPowder", 0.5f)

  loadSprites(Assets.skeletonSpriteSheet, Map(Left -> 2, Right -> 4, Up -> 1, Down -> 3), 0)

  override def hitbox: Rectangle = new Rectangle(17, 15, 30, 46)

  creatureType = "skeleton"

  maxHealthPoints = 200f
  healthPoints = maxHealthPoints

  grantWeapon(weaponType)

}

object Skeleton {
  def apply(id: String, mobSpawnPoint: MobSpawnPoint, weaponType: String) =
    new Skeleton(id, mobSpawnPoint, weaponType)
}
