package com.easternsauce.game.wrappers

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.easternsauce.game.assets.Assets

class EsSpriteSheet private (regionName: String, tileWidth: Int, tileHeight: Int) {
  private val animationTexture: TextureRegion =
    Assets.textureAtlas.findRegion(regionName)
  val spriteTextures: Array[Array[TextureRegion]] =
    animationTexture.split(tileWidth, tileHeight)

}

object EsSpriteSheet {
  def apply(regionName: String, tileWidth: Int, tileHeight: Int) = new EsSpriteSheet(regionName, tileWidth, tileHeight)
}
