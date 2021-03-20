package com.easternsauce.game.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.{Music, Sound}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{TextureAtlas, TextureRegion}
import com.badlogic.gdx.maps.tiled.{TiledMap, TmxMapLoader}
import com.easternsauce.game.wrappers.EsSpriteSheet

object Assets {
  var textureAtlas: TextureAtlas = _
  var grassyMap: TiledMap = _
  var jungleMap: TiledMap = _
  var male1SpriteSheet: EsSpriteSheet = _
  var skeletonSpriteSheet: EsSpriteSheet = _
  var wolfSpriteSheet: EsSpriteSheet = _
  var goblinSpriteSheet: EsSpriteSheet = _
  var ghostSpriteSheet: EsSpriteSheet = _
  var fireDemonSpriteSheet: EsSpriteSheet = _
  var slashSpriteSheet: EsSpriteSheet = _
  var slashWindupSpriteSheet: EsSpriteSheet = _
  var tridentThrustSpriteSheet: EsSpriteSheet = _
  var tridentThrustWindupSpriteSheet: EsSpriteSheet = _
  var explosionSpriteSheet: EsSpriteSheet = _
  var explosionWindupSpriteSheet: EsSpriteSheet = _

  var iconsSpriteSheet: EsSpriteSheet = _

  var attackSound: Sound = _
  var painSound: Sound = _
  var arrowWhizzSound: Sound = _
  var bloodSquirtSound: Sound = _
  var boneClickSound: Sound = _
  var boneCrushSound: Sound = _
  var bounceSound: Sound = _
  var bowPullSound: Sound = _
  var bowReleaseSound: Sound = _
  var darkLaughSound: Sound = _
  var dogBarkSound: Sound = _
  var dogWhineSound: Sound = _
  var evilYellingSound: Sound = _
  var explosionSound: Sound = _
  var flybySound: Sound = _
  var glassBreakSound: Sound = _
  var gruntSound: Sound = _
  var monsterGrowlSound: Sound = _
  var punchSound: Sound = _
  var roarSound: Sound = _
  var runningSound: Sound = _
  var strongPunchSound: Sound = _
  var swooshSound: Sound = _

  var arrowTexture: Texture = _

  var abandonedPlainsMusic: Music = _
  var fireDemonMusic: Music = _

  def createAssets(): Unit = {
    textureAtlas = new TextureAtlas("assets/atlas/packed_atlas.atlas")
    textureAtlas.getTextures.forEach(texture => texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear))

    male1SpriteSheet = new EsSpriteSheet("male1", 32, 32)
    skeletonSpriteSheet = new EsSpriteSheet("skeleton", 64, 64)
    wolfSpriteSheet = new EsSpriteSheet("wolf", 50, 35)
    goblinSpriteSheet = new EsSpriteSheet("goblin", 32, 32)
    ghostSpriteSheet = new EsSpriteSheet("ghost", 32, 32)

    iconsSpriteSheet = new EsSpriteSheet("nice_icons", 32, 32)

    slashSpriteSheet = new EsSpriteSheet("slash", 40, 40)
    slashWindupSpriteSheet = new EsSpriteSheet("slash_windup", 40, 40)

    tridentThrustSpriteSheet = new EsSpriteSheet("trident_thrust", 64, 32)
    tridentThrustWindupSpriteSheet = new EsSpriteSheet("trident_thrust_windup", 64, 32)

    explosionSpriteSheet = new EsSpriteSheet("explosion", 64, 64)
    explosionWindupSpriteSheet = new EsSpriteSheet("explosion_windup", 64, 64)


    grassyMap = new TmxMapLoader().load("assets/grassy_terrain/tile_map.tmx")
    jungleMap = new TmxMapLoader().load("assets/jungle_terrain/tile_map.tmx")

    attackSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/swoosh.wav"))
    painSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/pain.wav"))
    arrowWhizzSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/arrow-whizz.wav"))
    bloodSquirtSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/blood-squirt.wav"))
    boneClickSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/bone-click.wav"))
    boneCrushSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/bone-crush.wav"))
    bowPullSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/bow-pull.wav"))
    bowReleaseSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/bow-release.wav"))
    darkLaughSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/dark-laugh.wav"))
    dogBarkSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/dogbark.wav"))
    dogWhineSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/dogwhine.wav"))
    evilYellingSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/evil-yelling.wav"))
    explosionSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/explosion.wav"))
    flybySound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/flyby.wav"))
    glassBreakSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/glass-break.wav"))
    gruntSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/grunt.wav"))
    monsterGrowlSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/monster-growl.wav"))
    punchSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/punch.wav"))
    roarSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/roar.wav"))
    runningSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/running.wav"))
    strongPunchSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/strong-punch.wav"))
    swooshSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/swoosh.wav"))

    arrowTexture = new Texture(Gdx.files.internal("assets/projectiles/arrow.png"))

    abandonedPlainsMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/abandoned_plains.wav"))
    fireDemonMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/fire_demon.wav"))

  }

  def getItemIcon(x: Int, y: Int): TextureRegion = {
    iconsSpriteSheet.spriteTextures(y)(x)
  }

}
