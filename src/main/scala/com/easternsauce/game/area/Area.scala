package com.easternsauce.game.area

import java.io.{FileWriter, PrintWriter}
import java.util
import java.util.{LinkedList, List, Map}

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.loot.{LootPile, Treasure}
import com.easternsauce.game.projectile.Arrow
import com.easternsauce.game.spawn.{Blockade, EnemyRespawnArea, MobSpawnPoint, PlayerRespawnPoint, SpawnLocationsContainer}

import scala.collection.mutable

class Area(val id: String, val tiledMap: TiledMap, scale: Float) {
//  val creatures: mutable.Map[String, Creature] = mutable.Map()
  val tiledMapRenderer = new OrthogonalTiledMapRenderer(Assets.grassyMap, scale)

  var creaturesManager: CreaturesManager = CreaturesManager(this)

  private val spawnLocationsContainer: SpawnLocationsContainer = null

  private var enemyRespawnAreaList: mutable.ListBuffer[EnemyRespawnArea] = null
  private var mobSpawnPointList: mutable.ListBuffer[MobSpawnPoint] = null


  private var respawnList: mutable.ListBuffer[PlayerRespawnPoint] = null

  private var arrowList: mutable.ListBuffer[Arrow] = null

  private var lootPileList: mutable.ListBuffer[LootPile] = null

  private var treasureList: mutable.ListBuffer[Treasure] = null

  private var remainingTreasureList: mutable.ListBuffer[Treasure] = null

  private var abandonedPlains = null //music

  var blockadeList: mutable.ListBuffer[Blockade] = null

  private def loadSpawns(): Unit = {
    // TODO
  }

  def render(): Unit = {
    // TODO
  }

  def updateSpawns(): Unit = {
    // TODO
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
    // TODO
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
    // TODO
  }

  def creatures: mutable.Map[String, Creature] = {
    creaturesManager.creatures
  }

}
