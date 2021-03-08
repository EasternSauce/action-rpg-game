package com.easternsauce.game.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.{Music, Sound}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.{TiledMap, TmxMapLoader}

object Assets {
  var grassyMap: TiledMap = _
  var jungleMap: TiledMap = _
  var male1SpriteSheet: SpriteSheet = _
  var skeletonSpriteSheet: SpriteSheet = _
  var wolfSpriteSheet: SpriteSheet = _
  var goblinSpriteSheet: SpriteSheet = _
  var ghostSpriteSheet: SpriteSheet = _
  var slashSpriteSheet: SpriteSheet = _
  var slashWindupSpriteSheet: SpriteSheet = _
  var tridentThrustSpriteSheet: SpriteSheet = _
  var tridentThrustWindupSpriteSheet: SpriteSheet = _
  var explosionSpriteSheet: SpriteSheet = _
  var explosionWindupSpriteSheet: SpriteSheet = _

  var iconsSpriteSheet: SpriteSheet = _

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

  var niceItemIcons: SpriteSheet = _


  var arrowTexture: Texture = _

  var abandonedPlainsMusic: Music = _
  var fireDemonMusic: Music = _

  def createAssets(): Unit = {
    male1SpriteSheet = new SpriteSheet("assets/packed/male1_pack.atlas")
    skeletonSpriteSheet = new SpriteSheet("assets/packed/skeleton_pack.atlas")
    wolfSpriteSheet = new SpriteSheet("assets/packed/wolf_pack.atlas")
    goblinSpriteSheet = new SpriteSheet("assets/packed/goblin_pack.atlas")
    ghostSpriteSheet = new SpriteSheet("assets/packed/ghost_pack.atlas")

    iconsSpriteSheet = new SpriteSheet("assets/packed/icon_pack.atlas")

    slashSpriteSheet = new SpriteSheet("assets/packed/slash_pack.atlas")
    slashWindupSpriteSheet = new SpriteSheet("assets/packed/slash_windup_pack.atlas")

    tridentThrustSpriteSheet = new SpriteSheet("assets/packed/trident_thrust.atlas")
    tridentThrustWindupSpriteSheet = new SpriteSheet("assets/packed/trident_thrust_windup.atlas")

    explosionSpriteSheet = new SpriteSheet("assets/packed/explosion_pack.atlas")
    explosionWindupSpriteSheet = new SpriteSheet("assets/packed/explosion_windup_pack.atlas")

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

  def getItemIcon(x: Int, y: Int): Sprite = {
    iconsSpriteSheet.getSprite(x, y)
  }

}
