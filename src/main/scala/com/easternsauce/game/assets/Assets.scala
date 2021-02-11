package com.easternsauce.game.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.{TiledMap, TmxMapLoader}

object Assets {
  var grassyMap: TiledMap = _
  var male1: SpriteSheet = _
  var skeleton: SpriteSheet = _
  var wolf: SpriteSheet = _
  var goblin: SpriteSheet = _
  var ghost: SpriteSheet = _
  var slash: SpriteSheet = _
  var slashWindup: SpriteSheet = _
  var attackSound: Sound = _
  var painSound: Sound = _
  var arrowTexture: Texture = _

  def createAssets(): Unit = {
    male1 = new SpriteSheet("assets/packed/male1_pack.atlas")
    skeleton = new SpriteSheet("assets/packed/skeleton_pack.atlas")
    wolf = new SpriteSheet("assets/packed/wolf_pack.atlas")
    goblin = new SpriteSheet("assets/packed/goblin_pack.atlas")
    ghost = new SpriteSheet("assets/packed/ghost_pack.atlas")

    slash = new SpriteSheet("assets/packed/slash_pack.atlas")
    slashWindup = new SpriteSheet("assets/packed/slash_windup_pack.atlas")

    grassyMap = new TmxMapLoader().load("assets/grassy_terrain/tile_map.tmx")

    attackSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/swoosh.wav"))
    painSound =  Gdx.audio.newSound(Gdx.files.internal("assets/sounds/pain.wav"))

    arrowTexture = new Texture(Gdx.files.internal("assets/projectiles/arrow.png"))
  }
}
