package system

import java.io.{File, PrintWriter}

import com.badlogic.gdx.Input.{Buttons, Keys}
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.g2d._
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Body, Box2DDebugRenderer}
import com.badlogic.gdx.utils.viewport.{FitViewport, Viewport}
import com.badlogic.gdx.{Gdx, Input}
import com.easternsauce.game.area.{Area, AreaGate}
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.mob.boss.Boss
import com.easternsauce.game.creature.npc.NonPlayerCharacter
import com.easternsauce.game.creature.player.PlayerCharacter
import com.easternsauce.game.desktop.DesktopApplication
import com.easternsauce.game.dialogue.DialogueWindow
import com.easternsauce.game.gui.{Hud, LootOptionWindow, MainMenu}
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.inventory.InventoryWindow
import com.easternsauce.game.item.loot.LootSystem
import com.easternsauce.game.item.util.ItemType
import com.easternsauce.game.projectile.Arrow
import com.easternsauce.game.spawn.{PlayerRespawnPoint, SpawnLocationsContainer}
import com.easternsauce.game.utils.SimpleTimer
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameState.{GameState, MainMenu}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Random

object GameSystem {
  var currentArea: Option[Area] = None
  var gameTimer: SimpleTimer = SimpleTimer(true)

  var camera: OrthographicCamera = _

  val TiledMapCellSize = 64

  var originalWidth: Float = DesktopApplication.windowWidth
  var originalHeight: Float = DesktopApplication.windowHeight


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

  val generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/font/YoungSerif-Regular.ttf"))
  val parameter = new FreeTypeFontGenerator.FreeTypeFontParameter
  parameter.size = 16
  val font: BitmapFont = generator.generateFont(parameter)
  generator.dispose()

  val generator2 = new FreeTypeFontGenerator(Gdx.files.internal("assets/font/YoungSerif-Regular.ttf"))
  val parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter
  parameter2.size = 64
  val hugeFont: BitmapFont = generator2.generateFont(parameter2)
  generator2.dispose()


  val dirKeysMap: mutable.Map[Int, Boolean] = mutable.Map(
    Input.Keys.A -> false,
    Input.Keys.D -> false,
    Input.Keys.W -> false,
    Input.Keys.S -> false
  )

  var escRecently = false

  val ScreenProportion: Float = 3 / 4f

  var state: GameState = MainMenu

  var mainMenu: MainMenu = new MainMenu()

  var hudShapeDrawer: ShapeDrawer = _
  var worldShapeDrawer: ShapeDrawer = _

  var worldBatch: SpriteBatch = _
  var hudBatch: SpriteBatch = _

  var markRespawnAreaForReset: Boolean = false

  var loadingScreenVisible: Boolean = true

  var drawAttackHitboxes: Boolean = false

  var testCounter: Int = 0

  var debugRenderer: Box2DDebugRenderer = _

  var PixelsPerMeter: Float = 32f


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

  def adjustCamera(creature: Creature): Unit = {

    // smooth camera following
    val lerp = 30f
    val position = camera.position
    position.x += (creature.posX - position.x) * lerp * Gdx.graphics.getDeltaTime
    position.y += (creature.posY - Gdx.graphics.getHeight * (1 - ScreenProportion) / 2 - position.y) * lerp * Gdx.graphics.getDeltaTime

    camera.update()
  }

  def getVectorPerpendicular(vector: Vector2): Vector2 = {
    new Vector2(-vector.y, vector.x)
  }

  def create(worldBatch: SpriteBatch, hudBatch: SpriteBatch): Unit = {
    Assets.createAssets()

    ItemType.loadItemTypes()

    camera = new OrthographicCamera
    camera.setToOrtho(false, originalWidth, originalHeight)

    this.hudBatch = hudBatch
    this.worldBatch = worldBatch

    val hudRegion = createTextureAndRegion()
    hudShapeDrawer = new ShapeDrawer(hudBatch, hudRegion)

    val worldRegion = createTextureAndRegion()
    worldShapeDrawer = new ShapeDrawer(worldBatch, worldRegion)

    debugRenderer = new Box2DDebugRenderer()

    init()

  }

  def init(): Unit = {

    val area1SpawnPoints: SpawnLocationsContainer = new SpawnLocationsContainer("assets/areas/area1/spawns.txt")
    val area2SpawnPoints: SpawnLocationsContainer = new SpawnLocationsContainer("assets/areas/area2/spawns.txt")
    val area3SpawnPoints: SpawnLocationsContainer = new SpawnLocationsContainer("assets/areas/area3/spawns.txt")

    areas += ("area1" -> new Area("area1", Assets.area1Map, 4.0f, area1SpawnPoints))
    areas += ("area2" -> new Area("area2", Assets.area2Map, 4.0f, area2SpawnPoints))
    areas += ("area3" -> new Area("area3", Assets.area3Map, 4.0f, area3SpawnPoints))

    areas("area1").addRespawnPoint(new PlayerRespawnPoint(2650, 6270, areas("area1")))

    areas("area2").addRespawnPoint(new PlayerRespawnPoint(657, 1517, areas("area2")))

    areas("area3").addRespawnPoint(new PlayerRespawnPoint(349, 349, areas("area3")))

    GameSystem.playerCharacter = new PlayerCharacter("protagonist")
    areas("area1").moveInCreature(playerCharacter, 2674, 6292)


    lootSystem.placeTreasure(areas("area1"), 1594, 6044, ItemType.getItemType("healingPowder"))
    lootSystem.placeTreasure(areas("area1"), 90, 3792, ItemType.getItemType("woodenSword"))
    lootSystem.placeTreasure(areas("area1"), 3338, 3810, ItemType.getItemType("leatherArmor"))
    lootSystem.placeTreasure(areas("area1"), 6238, 1951, ItemType.getItemType("crossbow"))
    lootSystem.placeTreasure(areas("area1"), 6282, 5953, ItemType.getItemType("leatherHelmet"))
    lootSystem.placeTreasure(areas("area1"), 4963, 94, ItemType.getItemType("steelGloves"))

    lootSystem.placeTreasure(areas("area2"), 233, 1827, ItemType.getItemType("healingPowder"))
    lootSystem.placeTreasure(areas("area2"), 3007, 545, ItemType.getItemType("ironSword"))
    lootSystem.placeTreasure(areas("area2"), 4871, 5803, ItemType.getItemType("lifeRing"))
    lootSystem.placeTreasure(areas("area2"), 2339, 6113, ItemType.getItemType("trident"))


    val nonPlayerCharacter = new NonPlayerCharacter("Johnny", true, Assets.male1SpriteSheet, "a1")
    areas("area2").moveInCreature(nonPlayerCharacter, 344, 1431)
    val nonPlayerCharacter2 = new NonPlayerCharacter("Rita", true, Assets.male1SpriteSheet, "a1")
    areas("area2").moveInCreature(nonPlayerCharacter2, 1279, 1383)

    hud = new Hud()

    cameraFocussedCreature = Some(playerCharacter)

    gateList = ListBuffer()

    gateList += new AreaGate(areas("area1"), 6366, 472, areas("area3"), 550, 150)
    gateList += new AreaGate(areas("area1"), 63, 2003, areas("area2"), 1870, 295)

    creaturesToMove = ListBuffer()

    markRespawnAreaForReset = false
  }

  private def createTextureAndRegion(): TextureRegion = {
    import com.badlogic.gdx.graphics.Pixmap.Format
    import com.badlogic.gdx.graphics.g2d.TextureRegion
    import com.badlogic.gdx.graphics.{Pixmap, Texture}
    val pixmap = new Pixmap(1, 1, Format.RGBA8888)
    pixmap.setColor(Color.WHITE)
    pixmap.drawPixel(0, 0)
    val texture = new Texture(pixmap) //remember to dispose of later

    pixmap.dispose()
    new TextureRegion(texture, 0, 0, 1, 1)
  }

  def update(): Unit = {

    escRecently = false

    if (state == GameState.MainMenu) {
      mainMenu.update()
    }
    else if (state == GameState.Gameplay) {
      SimpleTimer.updateTimers()

      if (Gdx.input.isButtonPressed(Buttons.LEFT)) if (playerCharacter.currentAttack.canPerform) {
        playerCharacter.currentAttack.perform()
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
        playerCharacter.interact()
      }

      if (Gdx.input.isKeyJustPressed(Keys.F5)) saveGame()

      val area: Area = getCurrentArea

      area.update()

      GameSystem.currentArea.get.tiledMapRenderer.setView(camera)


      creaturesToMove.clear()

      for (area <- areas.values) {
        area.creaturesManager.processAreaChanges(creaturesToMove)
      }

      for (creature <- creaturesToMove) {
        if (creature.isPlayer && creature.pendingArea != null) {
          val oldArea = creature.area
          val newArea = creature.pendingArea
          if (oldArea != null) oldArea.removeCreature(creature.id)
          oldArea.world.destroyBody(creature.body)
          newArea.moveInCreature(creature, creature.pendingX, creature.pendingY)
          creature.area = newArea
          creature.pendingArea = null
          creature.passedGateRecently = true
        }
      }

      if (markRespawnAreaForReset) {
        markRespawnAreaForReset = false
        playerCharacter.respawnArea.reset()
      }

      area.creaturesManager.updateRenderPriorityQueue()

      inventoryWindow.update()

      dialogueWindow.update()

      lootSystem.update()

      hud.update()

      if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) if (!escRecently) if (!inventoryWindow.inventoryOpen && !lootOptionWindow.activated) {
        escRecently = true
        state = GameState.MainMenu
      }
    }
  }

  def render(): Unit = {
    worldBatch.setProjectionMatrix(camera.combined)

    Gdx.gl.glClearColor(0, 0, 0, 1)

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
      | (if (Gdx.graphics.getBufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0))


    if (state == GameState.MainMenu) {
      hudBatch.begin()

      mainMenu.render(hudBatch)

      hudBatch.end()
    }
    else if (state == GameState.Gameplay) {

      val area: Area = getCurrentArea

      area.tiledMapRenderer.render(Array(0,1)) // has to be outside world batch for some reason, otherwise camera issues

      worldBatch.begin()

      area.render(worldShapeDrawer)

      gateList.foreach(_.render(worldShapeDrawer))

      area.creaturesManager.renderDeadCreatures(worldBatch, worldShapeDrawer)

      lootSystem.render(worldBatch, worldShapeDrawer)

      area.creaturesManager.renderAliveCreatures(worldBatch, worldShapeDrawer)

      area.creaturesManager.renderHealthBars(worldBatch, worldShapeDrawer)

      area.creaturesManager.renderAbilities(worldBatch, worldShapeDrawer)


      area.arrowList.foreach((arrow: Arrow) => arrow.render(worldBatch))

      worldBatch.end()

      area.tiledMapRenderer.render(Array(2))


      hudBatch.begin()

      inventoryWindow.render(hudBatch, hudShapeDrawer)

      hud.render(hudBatch, hudShapeDrawer)

      dialogueWindow.render(hudBatch)

      lootOptionWindow.render(hudBatch)

      renderDeathScreen(hudBatch)

      renderLoadingScreen(hudShapeDrawer)

      hudBatch.end()

      //DEBUG
      //debugRenderer.render(currentArea.get.world, camera.combined.scale(PixelsPerMeter, PixelsPerMeter, 1))

    }

  }

  private def getCurrentArea = {
    val area: Area = currentArea match {
      case Some(value) => value
      case None => throw new RuntimeException("currentArea is not set")
    }
    area
  }

  private def renderDeathScreen(hudBatch: SpriteBatch) = {
    if (playerCharacter.respawning) {
      GameSystem.hugeFont.setColor(Color.RED)
      GameSystem.hugeFont.draw(hudBatch, "YOU DIED", GameSystem.originalWidth / 2f - 170,
        GameSystem.originalHeight - GameSystem.originalHeight * GameSystem.ScreenProportion / 2f + 100)
    }
  }

  def renderLoadingScreen(hudShapeDrawer: ShapeDrawer): Unit = {
    if (loadingScreenVisible) {
      hudShapeDrawer.setColor(Color.BLACK)
      hudShapeDrawer.filledRectangle(0, 0, GameSystem.originalWidth, GameSystem.originalHeight)
    }
  }

  def loadGame(): Unit = {

    var creature: Creature = null

    val fileContents = Source.fromFile("saves/savegame.sav")
    try {
      for (line <- fileContents.getLines) {
        val s = line.split(" ")
        if (s(0).equals("creature")) {
          var foundCreature: Creature = null

          var found = false
          areas.values.foreach(area => {

            if (!found) {
              area.creaturesManager.getCreatureById(s(1)) match {
                case Some(creature) =>
                  foundCreature = creature
                  found = true
                case _ =>
              }
            }
          })

          creature = foundCreature

        }


        if (s(0) == "area") if (creature != null) {

          if (areas(s(1)) != creature.area) {
            if (creature.area != null) {
              creature.area.removeCreature(creature.id)
            }

            creature.area = areas(s(1))

            areas(s(1)).moveInCreature(creature, 0f, 0f)
            if (creature.isPlayer) currentArea = Some(areas(s(1)))
          }

        }

        if (s(0).equals("pos")) {
          if (creature != null) {
            if (creature.area == null) throw new RuntimeException("position cannot be set before creature is spawned in area")
          }

          creature.setPos(s(1).toFloat, s(2).toFloat)
        }

        if (s(0).equals("health")) {
          if (creature != null) {
            creature.healthPoints = s(1).toFloat
          }
        }

        if (s(0).equals("equipment_item")) {
          if (creature != null) {
            val equipmentItems: mutable.Map[Int, Item] = creature.equipmentItems
            val item: Item = new Item(itemType = ItemType.getItemType(s(2)),
              damage = if (s(3).equals("0")) null.asInstanceOf[Float] else s(3).toInt.toFloat,
              armor = if (s(4).equals("0")) null.asInstanceOf[Float] else s(4).toInt.toFloat)
            equipmentItems += (s(1).toInt -> item)
          }
        }

        if (creature.isPlayer) {
          if (!creature.isAlive) {
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

        if (s(0).equals("inventory_item")) {
          if (creature != null) {
            val inventoryItems: mutable.Map[Int, Item] = inventoryWindow.inventoryItems
            inventoryItems.put(s(1).toInt, new Item(ItemType.getItemType(s(2)), lootPileBackref = null,
              damage = if (s(3) == "0") {
                null.asInstanceOf[Float]
              } else {
                s(3).toInt.toFloat
              },
              armor = if (s(4) == "0") {
                null.asInstanceOf[Float]
              } else {
                s(4).toInt.toFloat
              },
              quantity = if (s(5) == "0") {
                null.asInstanceOf[Int]
              } else {
                s(5).toInt
              }))
          }
        }

        if (s(0).equals("gold")) {
          inventoryWindow.gold = s(1).toInt
        }
      }
    }
    finally invFileContents.close()

    val respawnFileContents = Source.fromFile("saves/respawn_points.sav")
    try {
      for (line <- respawnFileContents.getLines) {
        val s = line.split(" ")

        if (s(0) == "respawnPoint") {
          val respawnPoint = areas(s(1)).respawnList(s(2).toInt)
          playerCharacter.currentRespawnPoint = respawnPoint

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

    for ((key, value) <- inventoryWindow.inventoryItems) {
      if (value != null.asInstanceOf[Item]) {
        val slotId = key
        val damage = if (value.damage == null.asInstanceOf[Float]) "0"
        else "" + value.damage.intValue
        val armor = if (value.armor == null.asInstanceOf[Float]) "0"
        else "" + value.armor.intValue
        val quantity = if (value.quantity == null.asInstanceOf[Float]) "0"
        else "" + value.quantity
        inventoryWriter.write("inventory_item " + slotId + " " + value.itemType.id + " " + damage + " " + armor + " " + quantity + "\n")
      }
    }

    inventoryWriter.write("gold " + inventoryWindow.gold + "\n")
    inventoryWriter.close()


    val respawnWriter = new PrintWriter(new File("saves/respawn_points.sav"))

    // TODO

    respawnWriter.close()


  }


  def resetArea(): Unit = {
    markRespawnAreaForReset = true
  }

  def stopBossBattleMusic(): Unit = {
    assert(currentArea.nonEmpty)

    for ((_, creature) <- currentArea.get.creatures) {
      if (creature.isBoss) {
        val boss = creature.asInstanceOf[Boss]
        boss.bossMusic.stop()
      }
    }
  }

  def distance(body1: Body, body2: Body): Float = {
    body1.getPosition.dst(body2.getPosition) * GameSystem.PixelsPerMeter
  }

  def distance(vector: Vector2, body: Body): Float = {
    vector.dst(body.getPosition) * GameSystem.PixelsPerMeter
  }

  def drawText(): Unit = {

  }
}