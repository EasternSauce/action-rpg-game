package com.easternsauce.game.wrappers

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.easternsauce.game.assets.Assets

class EsSpriteSheet(regionName: String, tileWidth: Int, tileHeight: Int) {
  private val animationTexture: TextureRegion =
    Assets.textureAtlas.findRegion(regionName)
  val spriteTextures: Array[Array[TextureRegion]] =
    animationTexture.split(tileWidth, tileHeight)

}
