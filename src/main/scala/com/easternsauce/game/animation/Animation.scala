package com.easternsauce.game.animation

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.utils
import com.easternsauce.game.assets.SpriteSheet
import com.easternsauce.game.utils.SimpleTimer
import system.GameSystem



class Animation(spriteSheet: SpriteSheet, frameDuration: Float, spriteWidth: Float, spriteHeight: Float, regionName: String = GameSystem.textureRegionName) {
  import com.badlogic.gdx.graphics.g2d.{Animation => GdxAnimation}

  protected var animation: GdxAnimation[Sprite] = _
  protected val animationTimer: SimpleTimer = SimpleTimer(true)
  protected var sprites: Array[Sprite] = _

  loadAnimation()

  private def loadAnimation(): Unit = {
    val regions: utils.Array[AtlasRegion] = spriteSheet.textureAtlas.findRegions(regionName)

    assert(regions.notEmpty())

    sprites = for (region: AtlasRegion <- regions.toArray) yield new Sprite(region)

    sprites.foreach(sprite => sprite.setSize(spriteWidth, spriteHeight))

    val libgdxArraySprites = new com.badlogic.gdx.utils.Array[Sprite](sprites)

    animation = new GdxAnimation[Sprite](frameDuration, libgdxArraySprites, PlayMode.LOOP)
  }

  def currentFrame(): Sprite = {
    animation.getKeyFrame(animationTimer.time, true)
  }

  def getFrameByIndex(i: Int): Sprite = animation.getKeyFrames.array(i)

  def restart(): Unit = animationTimer.resetStart()
}
