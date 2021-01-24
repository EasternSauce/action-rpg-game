package system

import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.graphics.g2d._
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Gdx, Input}
import com.easternsauce.game.animation.Animation
import com.easternsauce.game.area.{Area, AreaGate, CurrentAreaHolder}
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.mob.Skeleton
import com.easternsauce.game.creature.player.PlayerCharacter
import com.easternsauce.game.dialogue.DialogueWindow
import com.easternsauce.game.gui.{Hud, LootOptionWindow}
import com.easternsauce.game.item.inventory.InventoryWindow
import com.easternsauce.game.item.loot.LootSystem
import com.easternsauce.game.shapes.{Polygon, Rectangle}
import com.easternsauce.game.spawn.PlayerRespawnPoint
import com.easternsauce.game.utils.Timer
import system.GameSystem.shapeRenderer

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

object GameSystem {

  var currentArea: Option[Area] = None
  var gameTimer: Timer = Timer(true)

  var camera: OrthographicCamera = _

  val TiledMapCellSize = 64

  var cameraFocussedCreature: Option[Creature] = None

  var areaList: ListBuffer[Area] = _

  var inventoryWindow = new InventoryWindow()

  var dialogueWindow = new DialogueWindow()

  var lootSystem = new LootSystem()

  var lootOptionWindow = new LootOptionWindow()

  val currentAreaHolder: CurrentAreaHolder = CurrentAreaHolder()

  var areas: mutable.Map[String, Area] = mutable.Map()



  var gateList: ListBuffer[AreaGate] = ListBuffer()

  var creaturesToMove: ListBuffer[Creature] = ListBuffer()

  var hud: Hud = new Hud()

  var playerCharacter: PlayerCharacter = _


  val random: Random = new Random()

  val textureRegionPrefix = "Tile_"
  val textureRegionName: String = textureRegionPrefix + "1"




  var testPolygon: Polygon = new Polygon()

  val dirKeysMap: mutable.Map[Int, Boolean] = mutable.Map(
    Input.Keys.A -> false,
    Input.Keys.D -> false,
    Input.Keys.W -> false,
    Input.Keys.S -> false
  )

//  var grassyArea: Area = _

  var shapeRenderer: ShapeRenderer = _

//  var anim: Animation = _


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

  def adjustCamera(rect: Rectangle): Unit = {
    camera.position.x = rect.x + rect.width / 2
    camera.position.y = rect.y + rect.height / 2
    camera.update()
  }

  def distance(rect1: Rectangle, rect2: Rectangle): Float = {
    val center1 = rect1.center
    val x1 = center1.x
    val y1 = center1.y
    val center2 = rect2.center
    val x2 = center2.x
    val y2 = center2.y
    Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)).toFloat
  }

  def getVectorPerpendicular(vector: Vector2): Vector2 = {
    new Vector2(-vector.y, vector.x)
  }

  def checkCollision(polygon: Polygon, rect: Rectangle): Boolean = { // TODO: to improve
    val points = List(
      new Vector2(rect.x, rect.y),
      new Vector2(rect.x, rect.y + rect.width),
      new Vector2(rect.x+rect.width, rect.y),
      new Vector2(rect.x+rect.width, rect.y+rect.height)
    )

    points.exists(point => polygon.contains(point))
  }

  def create(): Unit = {
    Assets.createAssets()

    GameSystem.playerCharacter = new PlayerCharacter("protagonist")
    val skele: Skeleton = new Skeleton("skellie123") // TODO: load from file

    areas += ("area1" -> new Area(Assets.grassyMap, 4.0f))
    areas += ("area2" -> new Area(null, 1.0f))

    areas.get("area1") match {
      case Some(area) =>
        area.addRespawnPoint(new PlayerRespawnPoint(400, 500, area))
        area.addRespawnPoint(new PlayerRespawnPoint(3650, 4909, area))
        area.addNewCreature(playerCharacter, 1000f, 1000f)
        area.addNewCreature(skele, 600f, 600f) // TODO: load from file

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


    cameraFocussedCreature = Some(playerCharacter)


    val w = Gdx.graphics.getWidth
    val h = Gdx.graphics.getHeight
    camera = new OrthographicCamera
    camera.setToOrtho(false, w, h)

    //    grassyArea = new Area(Assets.grassyMap, 4.0f)
//    grassyArea.creatures += player.id -> player
//    grassyArea.creatures += skele.id -> skele

//    player.area = grassyArea
//    skele.area = grassyArea

//    grassyArea.creatures.values.foreach(creature => creature.onInit())
//
//    currentArea = Some(grassyArea)
//
//    val w = Gdx.graphics.getWidth
//    val h = Gdx.graphics.getHeight
//    camera = new OrthographicCamera
//    camera.setToOrtho(false, w, h)
//
//    anim = new Animation(Assets.slashWindup, 0.1f, 40, 40)
  }

  def update(): Unit = {
    Timer.updateTimers()

    if (Gdx.input.isButtonPressed(Buttons.LEFT)) if (playerCharacter.currentAttack.canPerform) playerCharacter.currentAttack.perform()

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
  }

  def render(spriteBatch: SpriteBatch, shapeRenderer: ShapeRenderer, polygonBatch: PolygonSpriteBatch): Unit = {
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


    spriteBatch.end()



    val pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pix.setColor(0xDEADBEFF)

    pix.fill()
    val textureSolid = new Texture(pix)

    val a = 100

    val poly = testPolygon

    val polygonRegion = new PolygonRegion(new TextureRegion(textureSolid), poly.getVertices, Array[Short](0, 1, 2, 0, 2, 3))


    val polygonSprite = new PolygonSprite(polygonRegion)

    polygonBatch.begin()

    polygonSprite.setPosition(poly.getX, poly.getY)
    polygonSprite.setOrigin(poly.getOriginX, poly.getOriginY)
    polygonSprite.setRotation(poly.getRotation)

    //polygonSprite.draw(polygonBatch)

    polygonBatch.end()

    shapeRenderer.begin(ShapeType.Filled)


    gateList.foreach(_.renderShapes(shapeRenderer))


    shapeRenderer.rect(0, 0, 500, 500)


    //shapeRenderer.polygon(poly.getVertices)

    shapeRenderer.end()


  }
}