package com.easternsauce.game.area

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.backends.lwjgl.audio.Wav
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, CircleShape, FixtureDef, PolygonShape, Shape, World}
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.item.loot.{LootPile, Treasure}
import com.easternsauce.game.projectile.Arrow
import com.easternsauce.game.spawn._
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Area(val id: String, val tiledMap: TiledMap, scale: Float, val spawnLocationsContainer: SpawnLocationsContainer) {
  val tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, scale)

  var creaturesManager: CreaturesManager = CreaturesManager(this)

  private var enemyRespawnAreaList: mutable.ListBuffer[EnemyRespawnArea] = ListBuffer()
  private var mobSpawnPointList: mutable.ListBuffer[MobSpawnPoint] = ListBuffer()

  var respawnList: mutable.ListBuffer[PlayerRespawnPoint] = ListBuffer()


  var blockadeList: mutable.ListBuffer[Blockade] = ListBuffer()

  var lootPileList: ListBuffer[LootPile] = ListBuffer()
  var remainingTreasureList: ListBuffer[Treasure] = ListBuffer()
  var treasureList: ListBuffer[Treasure] = ListBuffer()

  var arrowList: mutable.ListBuffer[Arrow] = ListBuffer()

  var world: World = new World(new Vector2(0f,0f), true)

  val layer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]

  for {x <- Seq.range(0, layer.getWidth)
       y <- Seq.range(0, layer.getHeight)} {
    val cell: TiledMapTileLayer.Cell = layer.getCell(x, y)

    val traversable: Boolean = cell.getTile.getProperties.get("traversable").asInstanceOf[Boolean]

    if (!traversable) {
      val rectX = x * layer.getTileWidth * scale
      val rectY = y * layer.getTileHeight * scale
      val rectW = layer.getTileWidth * scale
      val rectH = layer.getTileHeight * scale

      val bodyDef = new BodyDef()
      bodyDef.`type` = BodyDef.BodyType.StaticBody
      bodyDef.position.set((rectX + rectH / 2) / GameSystem.PixelsPerMeter, (rectY + rectH / 2) / GameSystem.PixelsPerMeter)

      val body: Body = world.createBody(bodyDef)

      val shape : PolygonShape = new PolygonShape()

      shape.setAsBox((rectW / 2) / GameSystem.PixelsPerMeter, (rectH / 2) / GameSystem.PixelsPerMeter)

      val fixtureDef: FixtureDef = new FixtureDef

      fixtureDef.shape = shape

      body.createFixture(fixtureDef)
    }

  }

  tiledMap.getLayers.get(0).getObjects.getByType(classOf[RectangleMapObject]).forEach( rectObject => {
    val rect = rectObject.getRectangle
    println("adding " + rect.x + " " + rect.y)

    val bodyDef = new BodyDef()
    bodyDef.`type` = BodyDef.BodyType.StaticBody
    bodyDef.position.set((rect.getX + rect.getWidth / 2) * GameSystem.PixelsPerMeter, (rect.getY + rect.getHeight / 2) * GameSystem.PixelsPerMeter)

    val body: Body = world.createBody(bodyDef)

    val shape : PolygonShape = new PolygonShape()

    shape.setAsBox((rect.getWidth / 2) * GameSystem.PixelsPerMeter, (rect.getHeight / 2) * GameSystem.PixelsPerMeter)

    val fixtureDef: FixtureDef = new FixtureDef

    fixtureDef.shape = shape

    body.createFixture(fixtureDef)

  })
  loadSpawns()

  private def loadSpawns(): Unit = {
    for (spawnLocation <- spawnLocationsContainer.spawnLocationList) {
      val posX = spawnLocation.posX
      val posY = spawnLocation.posY
      if (spawnLocation.spawnType == "respawnArea") enemyRespawnAreaList += new EnemyRespawnArea(posX, posY, 3, this, spawnLocation.creatureType)
      else if (spawnLocation.spawnType == "spawnPoint") {
        val mobSpawnPoint = new MobSpawnPoint(posX, posY, this, spawnLocation.creatureType)
        mobSpawnPointList += mobSpawnPoint
        if (spawnLocation.hasBlockade) addBlockade(mobSpawnPoint, spawnLocation.blockadePosX, spawnLocation.blockadePosY)
      }
    }
  }

  def render(shapeDrawer: ShapeDrawer): Unit = {
    for (blockade <- blockadeList) {
      blockade.render()
    }

    respawnList.foreach(respawnPoint => respawnPoint.render(shapeDrawer))

  }

  def updateSpawns(): Unit = {
    for (enemyRespawnArea <- enemyRespawnAreaList) {
      enemyRespawnArea.update()
    }


    for (mobSpawnPoint <- mobSpawnPointList) {
      mobSpawnPoint.update()
    }
  }

  def addRespawnPoint(respawnPoint: PlayerRespawnPoint): Unit = {
    respawnList += respawnPoint
  }

  def onLeave(): Unit = {
    arrowList.clear()
    lootPileList.clear()

    for (mobSpawnPoint <- mobSpawnPointList) {
      mobSpawnPoint.markForRespawn()
    }

  }

  def onEntry(): Unit = {
    //TODO: add music manager or smth
    Assets.abandonedPlainsMusic.stop()

    if (id == "area1") {
      Assets.abandonedPlainsMusic.setVolume(0.1f)
      Assets.abandonedPlainsMusic.play()
    }
    creaturesManager.onAreaChange()

    reset()

    creaturesManager.initializeCreatures()
  }

  def moveInCreature(creature: Creature, x: Float, y: Float): Unit = {
    creaturesManager.addCreature(creature)
    creature.area = this


    GameSystem.loadingScreenVisible = false

    addBox2dBody(creature, x, y)
  }

  private def addBox2dBody(creature: Creature, x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)
    bodyDef.`type` = BodyDef.BodyType.DynamicBody
    creature.body = world.createBody(bodyDef)

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: CircleShape = new CircleShape()
    shape.setRadius(30 / GameSystem.PixelsPerMeter)
    fixtureDef.shape = shape
    creature.body.createFixture(fixtureDef)
    creature.body.setLinearDamping(9f)
  }

  def removeCreature(id: String): Unit = {
    creatures.remove(id)
  }

  def addNewCreature(creature: Creature, x: Float, y: Float): Unit = {
    creaturesManager.addCreature(creature)
    creature.area = this

    creature.startingPosX = x
    creature.startingPosY = y

  }

  def reset(): Unit = {
    arrowList.clear()
    lootPileList.clear()
    creaturesManager.clearRespawnableCreatures()

    for (mobSpawnPoint <- mobSpawnPointList) {
      mobSpawnPoint.markForRespawn()
    }

    for (blockade <- blockadeList) {
      blockade.active = false
    }

    creaturesManager.initializeCreatures()
  }

  def softReset(): Unit = {
    for (creature <- creaturesManager.creatures.values) {
      if (creature.alive && !creature.isPlayer && !creature.isNPC) creature.reset()
    }
  }

  def addBlockade(mobSpawnPoint: MobSpawnPoint, blockadePosX: Int, blockadePosY: Int): Unit = {
    // TODO
  }

  def update(): Unit = {
    val toBeDeleted = ListBuffer[Arrow]()
    for (arrow <- arrowList) {
      arrow.update()
      if (arrow.markedForDeletion) {
        toBeDeleted += arrow
      }
    }

    arrowList.filterInPlace(!toBeDeleted.contains(_))

    val areaCreatures: mutable.Map[String, Creature] = creatures

    areaCreatures.values.foreach((creature: Creature) => creature.update())

    world.step(1/60f, 6, 2)


  }

  def creatures: mutable.Map[String, Creature] = {
    creaturesManager.creatures
  }

}
