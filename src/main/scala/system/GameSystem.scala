package system

import java.io.{File, PrintWriter}

import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx.graphics.g2d._
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.{Intersector, Polygon, Vector2}
import com.badlogic.gdx.{Gdx, Input}
import com.easternsauce.game.area.{Area, AreaGate}
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.mob.{Ghost, Goblin, Skeleton, Wolf}
import com.easternsauce.game.creature.player.PlayerCharacter
import com.easternsauce.game.dialogue.DialogueWindow
import com.easternsauce.game.gui.{Hud, LootOptionWindow}
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.inventory.InventoryWindow
import com.easternsauce.game.item.loot.LootSystem
import com.easternsauce.game.item.util.ItemType
import com.easternsauce.game.shapes.{CustomBatch, CustomPolygon, CustomRectangle, CustomVector2}
import com.easternsauce.game.spawn.PlayerRespawnPoint
import com.easternsauce.game.utils.Timer

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Random

object GameSystem {

  var currentArea: Option[Area] = None
  var gameTimer: Timer = Timer(true)

  var camera: OrthographicCamera = _

  val TiledMapCellSize = 64

  var cameraFocussedCreature: Option[Creature] = None

  var inventoryWindow = new InventoryWindow()

  var dialogueWindow = new DialogueWindow()

  var lootSystem = new LootSystem()

  var lootOptionWindow = new LootOptionWindow()

  var areas: mutable.Map[String, Area] = mutable.Map()



  var gateList: ListBuffer[AreaGate] = ListBuffer()

  var creaturesToMove: ListBuffer[Creature] = ListBuffer()

  var hud: Hud = _

  var playerCharacter: PlayerCharacter = _


  val random: Random = new Random()

  val textureRegionPrefix = "Tile_"
  val textureRegionName: String = textureRegionPrefix + "1"


  val dirKeysMap: mutable.Map[Int, Boolean] = mutable.Map(
    Input.Keys.A -> false,
    Input.Keys.D -> false,
    Input.Keys.W -> false,
    Input.Keys.S -> false
  )

  var shapeRenderer: ShapeRenderer = _

  val ScreenProportion: Float = 3 / 4f


  def getTiledMapRealWidth(tiledMap: TiledMap): Int = {
    val layer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
    layer.getWidth * TiledMapCellSize
  }

  def getTiledMapRealHeight(tiledMap: TiledMap): Int = {
    val layer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]
    layer.getHeight * TiledMapCellSize
  }

  def areaCreatures: Iterable[Creature] = currentArea match {
    case Some(value) => value.creatures.values
    case None => throw new RuntimeException("currentArea is not set")
  }

  def adjustCamera(rect: CustomRectangle): Unit = {
    camera.position.x = rect.x + rect.width / 2
    camera.position.y = rect.y + rect.height / 2 - Gdx.graphics.getHeight * (1 - ScreenProportion) / 2
    camera.update()
  }

  def distance(rect1: CustomRectangle, rect2: CustomRectangle): Float = {
    val center1 = rect1.center
    val x1 = center1.x
    val y1 = center1.y
    val center2 = rect2.center
    val x2 = center2.x
    val y2 = center2.y
    Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)).toFloat
  }

  def getVectorPerpendicular(vector: Vector2): CustomVector2 = {
    CustomVector2(-vector.y, vector.x)
  }

  def checkCollision(polygon1: CustomPolygon, rect: CustomRectangle): Boolean = { // TODO: to improve, use Box2d for collision in future?


    val polygon2 = new CustomPolygon(rect)

    val v1 = new Vector2
    polygon1.getBoundingRectangle.getCenter(v1)

    val v2 = new Vector2
    polygon2.getBoundingRectangle.getCenter(v2)

    val result = Intersector.intersectPolygons(polygon1, polygon2, null)

    result
  }

  def create(): Unit = {
    Assets.createAssets()

    //init()

    val w = Gdx.graphics.getWidth
    val h = Gdx.graphics.getHeight
    camera = new OrthographicCamera
    camera.setToOrtho(false, w, h)

    loadGame() // TODO: move to main menu

  }

  def init(): Unit = {
    GameSystem.playerCharacter = new PlayerCharacter("protagonist")
    val skele: Skeleton = new Skeleton("skellie123") // TODO: load from file
    val wolf: Wolf = new Wolf("wolf352") // TODO: load from file
    val ghost: Ghost = new Ghost("32532") // TODO: load from file
    val goblin: Goblin = new Goblin("3255323523") // TODO: load from file


    areas += ("area1" -> new Area("area1", Assets.grassyMap, 4.0f))
    areas += ("area2" -> new Area("area2", null, 1.0f)) // TODO: load assets

    areas.get("area1") match {
      case Some(area) =>
        area.addRespawnPoint(new PlayerRespawnPoint(400, 500, area))
        area.addRespawnPoint(new PlayerRespawnPoint(3650, 4909, area))
        area.addNewCreature(playerCharacter, 1000f, 1000f)
        area.addNewCreature(skele, 600f, 600f) // TODO: load from file
        area.addNewCreature(wolf, 1200f, 1000f)
        area.addNewCreature(ghost, 1200f, 1000f)
        area.addNewCreature(goblin, 1200f, 1000f)

        currentArea = Some(area) // TODO: load from file
        area.creatures.values.foreach(creature => creature.onInit()) // TODO: do it while loading saves

      case None => throw new RuntimeException("area doesn't exist")
    }

    areas.get("area2") match {
      case Some(area) =>
        area.addRespawnPoint(new PlayerRespawnPoint(594, 133, area))
        area.addRespawnPoint(new PlayerRespawnPoint(1342, 2099, area))
      case None => throw new RuntimeException("area doesn't exist")
    }

    hud = new Hud()

    cameraFocussedCreature = Some(playerCharacter)
  }

  def update(): Unit = {
    Timer.updateTimers()

    if (Gdx.input.isButtonPressed(Buttons.LEFT)) if (playerCharacter.currentAttack.canPerform) playerCharacter.currentAttack.perform()

    if (Gdx.input.isKeyJustPressed(Keys.F5)) saveGame()


    areaCreatures.foreach(c => c.update())

    camera.update()

    val area: Area = currentArea match {
      case Some(value) => value
      case None => throw new RuntimeException("currentArea is not set")
    }

    area.tiledMapRenderer.setView(camera)

    currentArea match {
      case Some(area) => area.creaturesManager.updateRenderPriorityQueue()
      case _ => throw new RuntimeException("current area not set")
    }

    hud.update()
  }

  def render(spriteBatch: CustomBatch, hudBatch: CustomBatch, shapeRenderer: ShapeRenderer, polygonBatch: PolygonSpriteBatch): Unit = {
    spriteBatch.setProjectionMatrix(camera.combined)
    polygonBatch.setProjectionMatrix(camera.combined)
    shapeRenderer.setProjectionMatrix(camera.combined)

    Gdx.gl.glClearColor(0, 0, 0, 1)

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
      | (if (Gdx.graphics.getBufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0))

    val area: Area = currentArea match {
      case Some(value) => value
      case None => throw new RuntimeException("currentArea is not set")
    }

    area.tiledMapRenderer.render()

    spriteBatch.begin()

    area.creaturesManager.renderCreatures(spriteBatch)
//    areaCreatures.foreach(_.render(spriteBatch))

    //anim.currentFrame().draw(batch)

    gateList.foreach(_.renderShapes(spriteBatch))

    spriteBatch.end()

    hudBatch.begin()

    hud.render(hudBatch)

    hudBatch.end()


  }

  def loadGame(): Unit = {
    init()

    var creature: Creature = null

    val fileContents = Source.fromFile("saves/savegame.sav")
    try {
      for (line <- fileContents.getLines) {
        val s = line.split(" ")
        if(s(0).equals("creature")) {
          var foundCreature: Creature = null

          areas.values.foreach (area => {
            if (foundCreature == null) {
              foundCreature = area.creaturesManager.getCreatureById(s(1))
            }
          })

          creature = foundCreature

        }
        if(s(0).equals("pos")) {
          if (creature != null) {
            if (creature.area == null) throw new RuntimeException("position cannot be set before creature is spawned in area")
          }

          creature.rect.x = s(1).toFloat
          creature.rect.y = s(2).toFloat
        }

        if(s(0).equals("health")) {
          if (creature != null) {
            creature.healthPoints = s(1).toFloat
          }
        }

        if(s(0).equals("equipment_item")) {
          if (creature != null) {
            val equipmentItems: mutable.Map[Int, Item] = creature.equipmentItems
            val item: Item = new Item(itemType = ItemType.getItemType(s(2)),
              damage = if (s(3).equals("0")) null.asInstanceOf[Float] else s(3).toInt.toFloat,
              armor = if (s(4).equals("0")) null.asInstanceOf[Float] else s(4).toInt.toFloat)
            equipmentItems += (s(1).toInt -> item)
          }
        }

        if (creature.isPlayer) {
          if (!creature.alive) {
            creature.onDeath()
          }
        }
      }
    }
    finally fileContents.close()

    val invFileContents = Source.fromFile("saves/inventory.sav")
    try {
      for (line <- invFileContents.getLines) {
        val s = line.split(" ")

        if (s(0) == "inventory_item") {
          // TODO
        }

        if (s(0) == "gold") {
          // TODO
        }
      }
    }
    finally invFileContents.close()

    val respawnFileContents = Source.fromFile("saves/respawn_points.sav")
    try {
      for (line <- respawnFileContents.getLines) {
        val s = line.split(" ")

        if (s(0) == "respawnPoint") {
          // TODO
        }

      }
    }
    finally respawnFileContents.close()


    val treasureFileContents = Source.fromFile("saves/treasure_collected.sav")
    try {
      for (line <- treasureFileContents.getLines) {
        val s = line.split(" ")

        if (s(0) == "treasure") {
          // TODO
        }

      }
    }
    finally treasureFileContents.close()

    if (currentArea.isEmpty) currentArea = areas.get("area1")

    currentArea match {
      case Some(area) => area.onEntry()
      case _ => throw new RuntimeException("current area is not set")
    }
  }

  def saveGame(): Unit = {
    val writer = new PrintWriter(new File("saves/savegame.sav"))

    for (area <- areas.values) {
      area.creaturesManager.saveToFile(writer)
    }

    writer.close()

    val inventoryWriter = new PrintWriter(new File("saves/inventory.sav"))

    // TODO

    writer.close()

    val respawnWriter = new PrintWriter(new File("saves/respawn_points.sav"))

    // TODO

    writer.close()


  }
}