package com.easternsauce.game.area

import java.util
import java.util.List

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.item.loot.{LootPile, Treasure}
import com.easternsauce.game.projectile.Arrow
import com.easternsauce.game.spawn._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Area(val id: String, val tiledMap: TiledMap, scale: Float, val spawnLocationsContainer: SpawnLocationsContainer) {

  val tiledMapRenderer = new OrthogonalTiledMapRenderer(Assets.grassyMap, scale)

  var creaturesManager: CreaturesManager = CreaturesManager(this)

  private var enemyRespawnAreaList: mutable.ListBuffer[EnemyRespawnArea] = ListBuffer()
  private var mobSpawnPointList: mutable.ListBuffer[MobSpawnPoint] = ListBuffer()


  private var abandonedPlains = null //music

  var respawnList: mutable.ListBuffer[PlayerRespawnPoint] = ListBuffer()


  var blockadeList: mutable.ListBuffer[Blockade] = ListBuffer()

  var lootPileList: ListBuffer[LootPile] = ListBuffer()
  var remainingTreasureList: ListBuffer[Treasure] = ListBuffer()
  var treasureList: ListBuffer[Treasure] = ListBuffer()

  var arrowList: mutable.ListBuffer[Arrow] = ListBuffer()

  loadSpawns()

  private def loadSpawns(): Unit = {
    for (spawnLocation <- spawnLocationsContainer.spawnLocationList) {
      val posX = spawnLocation.posX
      val posY = spawnLocation.posY
      if (spawnLocation.spawnType == "respawnArea") enemyRespawnAreaList += new EnemyRespawnArea(posX, posY, 3, this, spawnLocation.creatureType)
      else if (spawnLocation.spawnType == "spawnPoint") {
        println("loaded " + spawnLocation.creatureType)
        val mobSpawnPoint = new MobSpawnPoint(posX, posY, this, spawnLocation.creatureType)
        mobSpawnPointList += mobSpawnPoint
        if (spawnLocation.hasBlockade) addBlockade(mobSpawnPoint, spawnLocation.blockadePosX, spawnLocation.blockadePosY)
      }
    }
  }

  def render(): Unit = {
    // TODO
  }

  def updateSpawns(): Unit = {
    for (enemyRespawnArea <- enemyRespawnAreaList) {
      enemyRespawnArea.update()
    }

    for (mobSpawnPoint <- mobSpawnPointList) {
      mobSpawnPoint.update()
    }
  }


  def saveTerrainLayoutToFile(fileName: String): Unit = {
    // TODO
  }

  def setTile(x: Int, y: Int, id: String): Unit = {
    // TODO
  }

  def loadLayoutTiles(): Unit = {
    // TODO
  }

  def renderSpawns(): Unit = {
    // TODO
  }

  def addRespawnPoint(respawnPoint: PlayerRespawnPoint): Unit = {
    // TODO
  }

  def onLeave(): Unit = {
    // TODO
  }

  def onEntry(): Unit = {
    // TODO
  }

  def moveInCreature(creature: Creature, x: Float, y: Float): Unit = {
    creaturesManager.addCreature(creature)
    creature.area = this

    creature.rect.setX(x)
    creature.rect.setY(y)
  }

  def removeCreature(id: String): Unit = {
    // TODO
  }

  def addNewCreature(creature: Creature, x: Float, y: Float): Unit = {
    creaturesManager.addCreature(creature)
    creature.area = this

    creature.rect.x = x
    creature.rect.y = y

    creature.startingPosX = x
    creature.startingPosY = y
  }

  def reset(): Unit = {
    // TODO
  }

  def softReset(): Unit = {
    // TODO
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

  }

  def creatures: mutable.Map[String, Creature] = {
    creaturesManager.creatures
  }


}
