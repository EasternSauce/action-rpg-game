package com.easternsauce.game.wrappers

import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.utils.EsTimer

class EsAnimation(
    spriteSheet: EsSpriteSheet,
    frameDuration: Float,
    val row: Int = 0
) {
  val dirMap = Map(Left -> 1, Right -> 2, Up -> 3, Down -> 0)

  val animationTimer: EsTimer = EsTimer(true)

  var animation: com.badlogic.gdx.graphics.g2d.Animation[TextureRegion] =
    new Animation[TextureRegion](
      frameDuration,
      spriteSheet.spriteTextures(row): _*
    )

  def currentFrame: TextureRegion = {
    animation.getKeyFrame(animationTimer.time, true)
  }

  def getFrameByIndex(i: Int): TextureRegion = animation.getKeyFrames.array(i)

  def restart(): Unit = animationTimer.restart()

}
