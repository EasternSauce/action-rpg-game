package com.easternsauce.game.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.{Music, Sound}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{TextureAtlas, TextureRegion}
import com.badlogic.gdx.maps.tiled.{TiledMap, TmxMapLoader}
import com.easternsauce.game.wrappers.EsSpriteSheet

object Assets {
  var textureAtlas: TextureAtlas = _
  var area1Map: TiledMap = _
  var area2Map: TiledMap = _
  var area3Map: TiledMap = _
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
  var fistSlamSpriteSheet: EsSpriteSheet = _
  var fistSlamWindupSpriteSheet: EsSpriteSheet = _

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
  var chestOpeningSound: Sound = _
  var coinBagSound: Sound = _
  var matchIgniteSound: Sound = _

  var arrowTexture: Texture = _
  var treasureTexture: Texture = _
  var bagTexture: Texture = _
  var gobletTexture: Texture = _
  var gobletLitTexture: Texture = _
  var downArrowTexture: Texture = _

  var abandonedPlainsMusic: Music = _
  var fireDemonMusic: Music = _

  def createAssets(): Unit = {
    textureAtlas = new TextureAtlas("assets/atlas/packed_atlas.atlas")
    textureAtlas.getTextures.forEach(
      texture =>
        texture
          .setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
    )

    male1SpriteSheet = EsSpriteSheet("male1", 32, 32)
    skeletonSpriteSheet = EsSpriteSheet("skeleton", 64, 64)
    wolfSpriteSheet = EsSpriteSheet("wolf2", 32, 34)
    goblinSpriteSheet = EsSpriteSheet("goblin", 32, 32)
    ghostSpriteSheet = EsSpriteSheet("ghost", 32, 32)

    fireDemonSpriteSheet = EsSpriteSheet("taurus", 80, 80)

    iconsSpriteSheet = EsSpriteSheet("nice_icons", 32, 32)

    slashSpriteSheet = EsSpriteSheet("slash", 40, 40)
    slashWindupSpriteSheet = EsSpriteSheet("slash_windup", 40, 40)

    tridentThrustSpriteSheet = EsSpriteSheet("trident_thrust", 64, 32)
    tridentThrustWindupSpriteSheet = EsSpriteSheet("trident_thrust_windup", 64, 32)

    explosionSpriteSheet = EsSpriteSheet("explosion", 64, 64)
    explosionWindupSpriteSheet = EsSpriteSheet("explosion_windup", 64, 64)

    fistSlamSpriteSheet = EsSpriteSheet("fist_slam", 40, 80)
    fistSlamWindupSpriteSheet = EsSpriteSheet("fist_slam_windup", 40, 80)

    area1Map = new TmxMapLoader().load("assets/areas/area1/tile_map.tmx")
    area2Map = new TmxMapLoader().load("assets/areas/area2/tile_map.tmx")
    area3Map = new TmxMapLoader().load("assets/areas/area3/tile_map.tmx")

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
    chestOpeningSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/chest-opening.wav"))
    coinBagSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/coinbag.wav"))
    matchIgniteSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/match-ignite.wav"))

    arrowTexture = new Texture(Gdx.files.internal("assets/projectiles/arrow.png"))
    treasureTexture = new Texture(Gdx.files.internal("assets/treasure/treasure.png"))
    bagTexture = new Texture(Gdx.files.internal("assets/treasure/bag.png"))
    gobletTexture = new Texture(Gdx.files.internal("assets/goblet/goblet.png"))
    gobletLitTexture = new Texture(Gdx.files.internal("assets/goblet/goblet_lit.png"))
    downArrowTexture = new Texture(Gdx.files.internal("assets/downarrow/downarrow.png"))

    abandonedPlainsMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/abandoned_plains.wav"))
    fireDemonMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/fire_demon.wav"))

  }

  def getItemIcon(x: Int, y: Int): TextureRegion = {
    iconsSpriteSheet.spriteTextures(y)(x)
  }

}
