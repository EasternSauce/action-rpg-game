package com.easternsauce.game.wrappers

import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.utils.EsTimer

class EsAnimation private (spriteSheet: EsSpriteSheet, frameDuration: Float, val row: Int) {
  val dirMap = Map(Left -> 1, Right -> 2, Up -> 3, Down -> 0)

  val animationTimer: EsTimer = EsTimer(true)

  val animation: com.badlogic.gdx.graphics.g2d.Animation[TextureRegion] =
    new Animation[TextureRegion](frameDuration, spriteSheet.spriteTextures(row): _*)

  def currentFrame: TextureRegion = {
    animation.getKeyFrame(animationTimer.time, true)
  }

  def getFrameByIndex(i: Int): TextureRegion = animation.getKeyFrames.array(i)

  def restart(): Unit = animationTimer.restart()

}

object EsAnimation {
  def apply(spriteSheet: EsSpriteSheet, frameDuration: Float, row: Int = 0) =
    new EsAnimation(spriteSheet, frameDuration, row)
}
