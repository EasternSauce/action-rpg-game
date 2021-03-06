package com.easternsauce.game.area

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.physics.box2d._
import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.components.AbilityComponent
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.item.loot.{LootPile, Treasure}
import com.easternsauce.game.projectile.Arrow
import com.easternsauce.game.spawn._
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem
import system.GameSystem.TiledMapCellSize

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Area private (
  val id: String,
  val tiledMap: TiledMap,
  scale: Float,
  val spawnLocationsContainer: SpawnLocationsContainer
) {

  val tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, scale)
  val firstLayer: TiledMapTileLayer =
    tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
  val creaturesManager: CreaturesManager = CreaturesManager(this)
  var respawnList: mutable.ListBuffer[PlayerRespawnPoint] = ListBuffer()
  var blockadeList: mutable.ListBuffer[Blockade] = ListBuffer()
  var lootPileList: ListBuffer[LootPile] = ListBuffer()
  var remainingTreasureList: ListBuffer[Treasure] = ListBuffer()
  var treasureList: ListBuffer[Treasure] = ListBuffer()
  val arrowList: mutable.ListBuffer[Arrow] = ListBuffer()
  var tiles: mutable.Map[(Int, Int, Int), AreaTile] = mutable.Map()
  val world: World = new World(new Vector2(0f, 0f), false)

  for (layerNum <- 0 to 1) { // two layers
    val layer: TiledMapTileLayer =
      tiledMap.getLayers.get(layerNum).asInstanceOf[TiledMapTileLayer]

    for {
      x <- Seq.range(0, layer.getWidth)
      y <- Seq.range(0, layer.getHeight)
    } {
      val cell: TiledMapTileLayer.Cell = layer.getCell(x, y)

      if (cell != null) {
        val traversable: Boolean =
          cell.getTile.getProperties.get("traversable").asInstanceOf[Boolean]
        val flyover: Boolean =
          cell.getTile.getProperties.get("flyover").asInstanceOf[Boolean]

        if (!traversable) {
          val rectX = x * layer.getTileWidth * scale
          val rectY = y * layer.getTileHeight * scale
          val rectW = layer.getTileWidth * scale
          val rectH = layer.getTileHeight * scale

          val bodyDef = new BodyDef()
          bodyDef.`type` = BodyDef.BodyType.StaticBody
          bodyDef.position
            .set((rectX + rectH / 2) / GameSystem.PixelsPerMeter, (rectY + rectH / 2) / GameSystem.PixelsPerMeter)

          val body: Body = world.createBody(bodyDef)

          val tile: AreaTile =
            AreaTile((layerNum, x, y), body, traversable, flyover)

          body.setUserData(tile)

          val shape: PolygonShape = new PolygonShape()

          shape.setAsBox((rectW / 2) / GameSystem.PixelsPerMeter, (rectH / 2) / GameSystem.PixelsPerMeter)

          val fixtureDef: FixtureDef = new FixtureDef

          fixtureDef.shape = shape

          body.createFixture(fixtureDef)

          tiles += (layerNum, x, y) -> tile

        }
      }

    }

  }
  private var mobSpawnPointList: mutable.ListBuffer[MobSpawnPoint] =
    ListBuffer()

  firstLayer.getWidth * TiledMapCellSize

  for { x <- Seq.range(0, firstLayer.getWidth) } {

    var rectX = x * firstLayer.getTileWidth * scale
    var rectY = (-1) * firstLayer.getTileHeight * scale
    var rectW = firstLayer.getTileWidth * scale
    var rectH = firstLayer.getTileHeight * scale

    createBorderTile(rectX, rectY, rectW, rectH)

    rectX = x * firstLayer.getTileWidth * scale
    rectY = firstLayer.getHeight * firstLayer.getTileHeight * scale
    rectW = firstLayer.getTileWidth * scale
    rectH = firstLayer.getTileHeight * scale

    createBorderTile(rectX, rectY, rectW, rectH)
  }

  for { y <- Seq.range(0, firstLayer.getHeight) } {

    var rectX = (-1) * firstLayer.getTileWidth * scale
    var rectY = y * firstLayer.getTileHeight * scale
    var rectW = firstLayer.getTileWidth * scale
    var rectH = firstLayer.getTileHeight * scale

    createBorderTile(rectX, rectY, rectW, rectH)

    rectX = firstLayer.getWidth * firstLayer.getTileWidth * scale
    rectY = y * firstLayer.getTileHeight * scale
    rectW = firstLayer.getTileWidth * scale
    rectH = firstLayer.getTileHeight * scale

    createBorderTile(rectX, rectY, rectW, rectH)
  }

  def render(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    for (blockade <- blockadeList) {
      blockade.render()
    }

    respawnList.foreach(respawnPoint => respawnPoint.render(batch, shapeDrawer))

  }

  createContactListener()

  loadSpawns()

  def updateSpawns(): Unit = {
    for (mobSpawnPoint <- mobSpawnPointList) {
      mobSpawnPoint.update()
    }
  }

  def addRespawnPoint(respawnPoint: PlayerRespawnPoint): Unit = {
    respawnList += respawnPoint
  }

  def moveInCreature(creature: Creature, x: Float, y: Float): Unit = {
    if (!creaturesManager.creatures.contains(creature.id)) {
      creaturesManager.addCreature(creature)
      creature.area = this

      GameSystem.loadingScreenVisible = false

      creature.initBody(x, y)
    }
  }

  def removeCreature(id: String): Unit = {
    if (creatures.contains(id)) {
      creatures.remove(id)
    }
  }

  def softReset(): Unit = {
    for (creature <- creaturesManager.creatures.values) {
      if (creature.isAlive && !creature.isPlayer && !creature.isNPC)
        creature.reset()
    }
  }

  def update(): Unit = {
    val toBeDeleted = ListBuffer[Arrow]()
    for (arrow <- arrowList) {
      arrow.update()
      if (arrow.markedForDeletion) {
        toBeDeleted += arrow
        arrow.area.world.destroyBody(arrow.body)
      }
    }

    arrowList.filterInPlace(!toBeDeleted.contains(_))

    val areaCreatures: mutable.Map[String, Creature] = creatures

    areaCreatures.values.foreach((creature: Creature) => creature.update())

    world.step(Math.min(Gdx.graphics.getDeltaTime, 0.15f), 6, 2)

  }

  def creatures: mutable.Map[String, Creature] = {
    creaturesManager.creatures
  }

  def createContactListener(): Unit = {
    val contactListener: ContactListener = new ContactListener {
      override def beginContact(contact: Contact): Unit = {

        val objA = contact.getFixtureA.getBody.getUserData
        val objB = contact.getFixtureB.getBody.getUserData

        def onContactStart(pair: (AnyRef, AnyRef)): Unit = {
          pair match { // will run onContact twice for same type objects!
            case (creature: Creature, areaGate: AreaGate) =>
              if (!creature.passedGateRecently) {
                onPassedAreaGate(areaGate, creature)
              }
            case (creature: Creature, ability: Ability) =>
              ability.onCollideWithCreature(creature)
            case (creature: Creature, abilityComponent: AbilityComponent) =>
              abilityComponent.onCollideWithCreature(creature)
            case (creature: Creature, arrow: Arrow) =>
              arrow.onCollideWithCreature(creature)
            case (areaTile: AreaTile, arrow: Arrow) =>
              arrow.onCollideWithTerrain(areaTile)
            case _ =>
          }
        }

        onContactStart(objA, objB)
        onContactStart(objB, objA)
      }

      override def endContact(contact: Contact): Unit = {
        val objA = contact.getFixtureA.getBody.getUserData
        val objB = contact.getFixtureB.getBody.getUserData

        def onContactEnd(pair: (AnyRef, AnyRef)): Unit = {
          pair match { // will run onContact twice for same type objects!
            case (creature: Creature, _: AreaGate) =>
              creature.passedGateRecently = false
            case _ =>
          }
        }

        onContactEnd(objA, objB)
        onContactEnd(objB, objA)
      }

      override def preSolve(contact: Contact, oldManifold: Manifold): Unit = {}

      override def postSolve(contact: Contact, impulse: ContactImpulse): Unit = {}
    }

    world.setContactListener(contactListener)
  }

  private def onPassedAreaGate(areaGate: AreaGate, creature: Creature): Unit = {
    if (creature.isPlayer) {
      var oldArea: Area = null
      var destinationArea: Area = null
      var destinationRect: Rectangle = null

      if (this == areaGate.areaFrom) {
        oldArea = areaGate.areaFrom
        destinationArea = areaGate.areaTo
        destinationRect = areaGate.toRect
      }
      if (this == areaGate.areaTo) {
        oldArea = areaGate.areaTo
        destinationArea = areaGate.areaFrom
        destinationRect = areaGate.fromRect
      }

      GameSystem.loadingScreenVisible = true
      creature.moveToArea(
        destinationArea,
        destinationRect.x + destinationRect.width / 2,
        destinationRect.y + destinationRect.height / 2
      )
      GameSystem.currentArea = Some(destinationArea)
      oldArea.onLeave()
      destinationArea.onEntry()

    }
  }

  def onLeave(): Unit = {
    arrowList.clear()
    lootPileList.clear()

    for (mobSpawnPoint <- mobSpawnPointList) {
      mobSpawnPoint.markForRespawn()
    }

  }

  def onEntry(): Unit = {
    //TODO: add music manager or something
    Assets.abandonedPlainsMusic.stop()
    Assets.fireDemonMusic.stop()

    if (id == "area2") {
      Assets.abandonedPlainsMusic.setVolume(0.1f)
      Assets.abandonedPlainsMusic.setLooping(true)
      Assets.abandonedPlainsMusic.play()
    }
    creaturesManager.onAreaEntry()

    reset()

    creaturesManager.initializeCreatures()
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

  private def createBorderTile(rectX: Float, rectY: Float, rectW: Float, rectH: Float) = {
    val bodyDef = new BodyDef()
    bodyDef.`type` = BodyDef.BodyType.StaticBody
    bodyDef.position
      .set((rectX + rectH / 2) / GameSystem.PixelsPerMeter, (rectY + rectH / 2) / GameSystem.PixelsPerMeter)

    val body: Body = world.createBody(bodyDef)

    body.setUserData(this)

    val shape: PolygonShape = new PolygonShape()

    shape.setAsBox((rectW / 2) / GameSystem.PixelsPerMeter, (rectH / 2) / GameSystem.PixelsPerMeter)

    val fixtureDef: FixtureDef = new FixtureDef

    fixtureDef.shape = shape

    body.createFixture(fixtureDef)
  }

  private def loadSpawns(): Unit = {
    for (spawnLocation <- spawnLocationsContainer.spawnLocationList) {
      val posX = spawnLocation.posX
      val posY = spawnLocation.posY

      if (spawnLocation.spawnType == "spawnPoint") {
        val mobSpawnPoint =
          MobSpawnPoint(posX, posY, this, spawnLocation.creatureType)
        mobSpawnPointList += mobSpawnPoint
        if (spawnLocation.hasBlockade)
          addBlockade(mobSpawnPoint, spawnLocation.blockadePosX, spawnLocation.blockadePosY)
      }
    }
  }

  def addBlockade(mobSpawnPoint: MobSpawnPoint, blockadePosX: Int, blockadePosY: Int): Unit = {
    val blockade = Blockade(mobSpawnPoint, blockadePosX, blockadePosY)
    blockadeList += blockade
    mobSpawnPoint.blockade = blockade
  }

}

object Area {
  def apply(id: String, tiledMap: TiledMap, scale: Float, spawnLocationsContainer: SpawnLocationsContainer) =
    new Area(id, tiledMap, scale, spawnLocationsContainer)
}
