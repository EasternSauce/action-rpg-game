package com.easternsauce.game.assets

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Sprite, TextureAtlas}
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up, WalkDirection}

class DeprecatedSpriteSheet(var atlasFileLocation: String) {
  var textureAtlas = new TextureAtlas(atlasFileLocation)
  val dirMap = Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1)

  textureAtlas.getTextures.forEach(texture => texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest))


  def getSprite(dir: WalkDirection, frame: Int): Sprite = {
    textureAtlas.createSprite("Tile_" + dirMap(dir), frame)
  }

  def getSprite(x: Int, y: Int): Sprite = {
    textureAtlas.createSprite("Tile_" + (y + 1), x + 1)
  }
}
