package com.easternsauce.game.wrappers

import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.utils.SimpleTimer

class EsAnimation(spriteSheet: EsSpriteSheet, val row: Int, frameDuration: Float) {
  val dirMap = Map(Left -> 1, Right -> 2, Up -> 3, Down -> 0)

  val animationTimer: SimpleTimer = SimpleTimer(true)

  var animation: com.badlogic.gdx.graphics.g2d.Animation[TextureRegion] = new Animation[TextureRegion](frameDuration, spriteSheet.spriteTextures(row):_*)

  def currentFrame: TextureRegion = {
    animation.getKeyFrame(animationTimer.time, true)
  }

  def getFrameByIndex(i: Int): TextureRegion = animation.getKeyFrames.array(i)

  def restart(): Unit = animationTimer.restart()

}
