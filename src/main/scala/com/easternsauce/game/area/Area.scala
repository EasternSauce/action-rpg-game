package com.easternsauce.game.area

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.backends.lwjgl.audio.Wav
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.item.loot.{LootPile, Treasure}
import com.easternsauce.game.projectile.Arrow
import com.easternsauce.game.spawn._
import space.earlygrey.shapedrawer.ShapeDrawer

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Area(val id: String, val tiledMap: TiledMap, scale: Float, val spawnLocationsContainer: SpawnLocationsContainer) {

  val tiledMapRenderer = new OrthogonalTiledMapRenderer(Assets.grassyMap, scale)

  var creaturesManager: CreaturesManager = CreaturesManager(this)

  private var enemyRespawnAreaList: mutable.ListBuffer[EnemyRespawnArea] = ListBuffer()
  private var mobSpawnPointList: mutable.ListBuffer[MobSpawnPoint] = ListBuffer()

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
    // TODO
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

    creature.rect.setX(x)
    creature.rect.setY(y)
  }

  def removeCreature(id: String): Unit = {
    creatures.remove(id)
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

  }

  def creatures: mutable.Map[String, Creature] = {
    creaturesManager.creatures
  }

}
