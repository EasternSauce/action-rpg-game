package com.easternsauce.game.area

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.{Polygon, Rectangle, Vector2}
import com.badlogic.gdx.physics.box2d._
import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.attack.{Attack, MeleeAttack}
import com.easternsauce.game.ability.util.AbilityState
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

  var world: World = new World(new Vector2(0f, 0f), false)

  val layer: TiledMapTileLayer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]

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
      body.setUserData(this)

      val shape: PolygonShape = new PolygonShape()

      shape.setAsBox((rectW / 2) / GameSystem.PixelsPerMeter, (rectH / 2) / GameSystem.PixelsPerMeter)

      val fixtureDef: FixtureDef = new FixtureDef

      fixtureDef.shape = shape

      body.createFixture(fixtureDef)

    }

  }

  createContactListener()

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
    creaturesManager.onAreaEntry()

    reset()

    creaturesManager.initializeCreatures()
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
            case (creature: Creature, areaGate: AreaGate) =>
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
      creature.moveToArea(destinationArea, destinationRect.x + destinationRect.width / 2, destinationRect.y + destinationRect.height / 2)
      GameSystem.currentArea = Some(destinationArea)
      oldArea.onLeave()
      destinationArea.onEntry()

    }
  }

}
