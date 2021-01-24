package com.easternsauce.game.creature.traits

import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.easternsauce.game.animation.Animation
import com.easternsauce.game.assets.SpriteSheet
import com.easternsauce.game.creature.util.WalkDirection
import com.easternsauce.game.creature.util.WalkDirection.WalkDirection
import com.easternsauce.game.utils.Timer
import system.GameSystem

import scala.collection.mutable

trait AnimatedWalking extends CreatureProperties {
  protected val walkAnimationFrameDuration = 0.1f
  protected val walkAnimationTimer: Timer = Timer()
  protected var neutralPositionIndex: Int = _
  protected var isRunningAnimationActive = false

  protected var walkAnimation: mutable.Map[WalkDirection, Animation] = mutable.Map()

  var lastMovingDir: WalkDirection = WalkDirection.Down

  def loadSprites(spriteSheet: SpriteSheet, directionalMapping: Map[WalkDirection, Int], neutralPositionIndex: Int): Unit = {

    this.neutralPositionIndex = neutralPositionIndex

    WalkDirection.values.foreach(dir => {
      walkAnimation(dir) = new Animation(spriteSheet, walkAnimationFrameDuration, rect.width, rect.height,
        GameSystem.textureRegionPrefix + directionalMapping(dir))
    })

  }

  def drawRunningAnimation(batch: SpriteBatch): Unit = {
    if (isRunningAnimationActive) {
      val currentFrame: Sprite = new Sprite(walkAnimation(lastMovingDir).currentFrame())

      currentFrame.setPosition(rect.x, rect.y)

      currentFrame.draw(batch)
    }
    else {
      val currentFrame: Sprite = new Sprite(walkAnimation(lastMovingDir).getFrameByIndex(neutralPositionIndex))

      if (!alive) {
        currentFrame.rotate90(true)
      }

      currentFrame.setPosition(rect.x, rect.y)

      currentFrame.draw(batch)
    }
  }
}
