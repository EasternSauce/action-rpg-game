package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.Rectangle

class Skeleton(id: String) extends Mob(id) {
  override val rect = new Rectangle(0,4500,64,64)
  override val hitboxBounds = new Rectangle(18, 0, 28, 64)
  override val speed = 300f

  override protected val onGettingHitSound: Sound = Assets.painSound

  loadSprites(Assets.skeleton, Map(Left -> 2, Right -> 4, Up -> 1, Down -> 3), 0)

  override def onDeath(): Unit = {
    super.onDeath()
  }
}
