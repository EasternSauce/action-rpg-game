package com.easternsauce.game.item.inventory

import java.io.{FileWriter, IOException}

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.{Gdx, Input}
import com.easternsauce.game.area.Area
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.loot.Treasure
import com.easternsauce.game.item.util.ItemType
import com.easternsauce.game.shapes.{CustomBatch, CustomRectangle}
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class InventoryWindow {
  private val background: CustomRectangle = new CustomRectangle((Gdx.graphics.getWidth * 0.2).toInt, (Gdx.graphics.getHeight * 0.3).toInt, (Gdx.graphics.getWidth * 0.6).toInt, (Gdx.graphics.getHeight * 0.6).toInt)

  private var slotList: ListBuffer[CustomRectangle] = ListBuffer()
  private var equipmentSlotList: ListBuffer[CustomRectangle] = ListBuffer()
  private var traderInventorySlotList: ListBuffer[CustomRectangle] = ListBuffer()




  private var currentSelected: Int = 0

  private var moving: Boolean = false
  private var currentMoved: Int = 0
  private var movingInEquipment: Boolean = false


  private var traderInventoryItems: mutable.Map[Int, Item] = mutable.Map()


  private var equipmentSlotNameList: ListBuffer[String] = ListBuffer("Weapon", "Helmet", "Body", "Gloves", "Ring", "Boots")

  private var inEquipment: Boolean = false

  private var inTraderInventory: Boolean = false

  private var trading: Boolean = false

  private val inventoryRows: Int = 4
  private val inventoryColumns: Int = 5
  private val inventorySlots: Int = inventoryRows * inventoryColumns

  private val space: Int = 10
  private val margin: Int = 10
  private val slotWidth: Int = 40
  private val slotHeight: Int = 40

  private val equipmentSlots: Int = 6

  private val tradeInventoryRows: Int = 6
  private val tradeInventoryColumns: Int = 2
  private val tradeInventorySlots: Int = tradeInventoryRows * tradeInventoryColumns

  var inventoryItems: mutable.Map[Int, Item] = mutable.Map()

  var inventoryOpen: Boolean = false

  var gold: Int = 0

  implicit class InventoryMapImprovements(map: mutable.Map[Int, Item]) {
    def containsNonNull(n: Int): Boolean = {
      map.contains(n) && map(n) != null.asInstanceOf[Item]
    }
  }


  for (i <- 0 until inventorySlots) {
    val col = i % inventoryColumns
    val row = i / inventoryColumns
    val slot = new CustomRectangle(background.getX + margin + (space + slotWidth) * col, background.getY + background.getHeight - (margin + (space + slotHeight) * (row + 1)), slotWidth, slotHeight)
    slotList += slot
  }

  for (i <- 0 until equipmentSlots) {
    val col = inventoryColumns + 2
    val slot = new CustomRectangle(background.getX + margin + (space + slotWidth) * col, background.getY + background.getHeight - (margin + (space + slotHeight) * (i + 1)), slotWidth, slotHeight)
    equipmentSlotList += slot
  }

  for (i <- 0 until tradeInventorySlots) {
    val col = inventoryColumns + 1 + i % tradeInventoryColumns
    val row = i / tradeInventoryColumns
    val slot = new CustomRectangle(background.getX + margin + (space + slotWidth) * col, background.getY + background.getHeight - (margin + (space + slotHeight) * (row + 1) + 30), slotWidth, slotHeight)
    traderInventorySlotList += slot
  }

  private def equipmentItems: mutable.Map[Int, Item] = GameSystem.playerCharacter.equipmentItems


  def render(batch: CustomBatch): Unit = {
    if (inventoryOpen) {

      batch.drawRect(background, Color.LIGHT_GRAY)

      renderInventory(batch)
      renderEquipment(batch)
      renderTraderInventory(batch)
      renderItemDescription(batch)
    }
  }

  private def renderTraderInventory(batch: CustomBatch): Unit = {
    if (trading) {
      for (i <- 0 until tradeInventorySlots) {
        var color = Color.BLACK
        if (inTraderInventory) if (currentSelected == i) color = Color.RED

        batch.drawRectBorder(traderInventorySlotList(i), color)
        if (traderInventoryItems.get(i) != null) {
          batch.draw(traderInventoryItems(i).itemType.texture, traderInventorySlotList(i).getX, traderInventorySlotList(i).getY, slotWidth, slotHeight)
          if (traderInventoryItems(i).quantity > 1) {
            GameSystem.font.setColor(Color.CYAN)
            GameSystem.font.draw(batch, "" + traderInventoryItems(i).quantity, traderInventorySlotList(i).getX, traderInventorySlotList(i).getY)
          }
        }
      }
      GameSystem.font.setColor(Color.WHITE)
      GameSystem.font.draw(batch, "Trader:", traderInventorySlotList.head.getX + 5f, background.getY + 15f)
    }
  }


  def renderInventory(batch: CustomBatch): Unit = {
    for (i <- 0 until inventorySlots) {
      var color = Color.BLACK
      if (moving && currentMoved == i && !movingInEquipment) color = Color.ORANGE
      else if (!inEquipment && !inTraderInventory) if (currentSelected == i) color = Color.RED

      batch.drawRectBorder(slotList(i), color)

      if (inventoryItems.containsNonNull(i)) {
        batch.draw(inventoryItems(i).itemType.texture, slotList(i).getX, slotList(i).getY, slotWidth, slotHeight)
        if (inventoryItems(i).quantity > 1) {
          GameSystem.font.setColor(Color.CYAN)
          GameSystem.font.draw(batch, "" + inventoryItems(i).quantity, slotList(i).getX, slotList(i).getY)
        }
      }
    }
    GameSystem.font.setColor(Color.YELLOW)
    GameSystem.font.draw(batch, "Gold: " + gold, background.getX + 5, background.getY + 20f)
  }

  def renderItemDescription(batch: CustomBatch): Unit = {
    GameSystem.font.setColor(Color.WHITE)
    if (inEquipment) {
      if (equipmentItems.containsNonNull(currentSelected)) {
        val item = equipmentItems(currentSelected)

        GameSystem.font.setColor(Color.ORANGE)
        GameSystem.font.draw(batch, item.name, background.getX + space, background.getY + background.getHeight - (margin + (space + slotHeight) * inventoryRows + space))

        GameSystem.font.draw(batch, item.getItemInformation(false), background.getX + space, background.getY + background.getHeight - (margin + (space + slotHeight) * inventoryRows + space + 50))

      }
    }
    else if (inTraderInventory) {
      if (traderInventoryItems.containsNonNull(currentSelected)) {
        val item = traderInventoryItems(currentSelected)

        GameSystem.font.setColor(Color.ORANGE)
        GameSystem.font.draw(batch, item.name, background.getX + space, background.getY + background.getHeight - (margin + (space + slotHeight) * inventoryRows + space))

        GameSystem.font.draw(batch, item.getItemInformation(trader = true), background.getX + space, background.getY + background.getHeight - (margin + (space + slotHeight) * inventoryRows + space + 50))

      }

    }
    else {
      if (inventoryItems.containsNonNull(currentSelected)) {
        val item = inventoryItems(currentSelected)

        GameSystem.font.setColor(Color.ORANGE)
        GameSystem.font.draw(batch, item.name, background.getX + space, background.getY + background.getHeight - (margin + (space + slotHeight) * inventoryRows + space))

        GameSystem.font.draw(batch, item.getItemInformation(false), background.getX + space, background.getY + background.getHeight - (margin + (space + slotHeight) * inventoryRows + space + 50))

      }
    }
  }

  def renderEquipment(batch: CustomBatch): Unit = {
    if (!trading) {

      for (i <- 0 until equipmentSlots) {
        var color = Color.BLACK
        if (moving && currentMoved == i && movingInEquipment) color = Color.ORANGE
        else if (inEquipment) if (currentSelected == i) color = Color.RED

        batch.drawRectBorder(equipmentSlotList(i), color)

        if (equipmentItems.containsNonNull(i)) {
          batch.draw(equipmentItems(i).itemType.texture, equipmentSlotList(i).getX, equipmentSlotList(i).getY, slotWidth, slotHeight)
          if (equipmentItems(i).quantity > 1) {
            batch.setColor(Color.CYAN)
            GameSystem.font.draw(batch, "" + equipmentItems(i).quantity, equipmentSlotList(i).getX, equipmentSlotList(i).getY)
          }
        }
        batch.setColor(Color.WHITE)
        GameSystem.font.draw(batch, equipmentSlotNameList(i), equipmentSlotList(i).getX - 60, equipmentSlotList(i).getY + 30)
      }
    }
  }


  def update(): Unit = {
    val player = GameSystem.playerCharacter

    if (Gdx.input.isKeyJustPressed(Input.Keys.I)) if (!inventoryOpen) openInventory()
    else closeInventory()
    if (inventoryOpen) {

      if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) if (!GameSystem.escRecently) {
        closeInventory()
        GameSystem.escRecently = true
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
        if (inEquipment) {
          if (currentSelected > 0) currentSelected -= 1
        }
        else if (inTraderInventory) {
          if (currentSelected >= tradeInventoryColumns) currentSelected = currentSelected - tradeInventoryColumns
        }
        else if (currentSelected >= inventoryColumns) currentSelected = currentSelected - inventoryColumns


      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
        if (inEquipment) {
          if (currentSelected * inventoryColumns + (inventoryColumns - 1) < inventorySlots) {
            inEquipment = false
            currentSelected = currentSelected * inventoryColumns + (inventoryColumns - 1)
          }
        } else if (inTraderInventory) {
          if (currentSelected % tradeInventoryColumns == 0) {
            if (currentSelected / tradeInventoryColumns < inventoryColumns - 1) {
              inTraderInventory = false
              currentSelected = currentSelected / tradeInventoryColumns * inventoryColumns + (inventoryColumns - 1)
            }
          }
          else if (currentSelected >= 1) currentSelected -= 1

        }
        else if (currentSelected >= 1) currentSelected -= 1

      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
        if (inEquipment) {
          if (currentSelected < equipmentSlots - 1) currentSelected += 1
        }
        else if (inTraderInventory) {
          if (currentSelected <= tradeInventorySlots - tradeInventoryColumns - 1) currentSelected = currentSelected + tradeInventoryColumns
        }
        else if (currentSelected <= inventorySlots - inventoryColumns - 1) currentSelected = currentSelected + inventoryColumns
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
        if (inTraderInventory) {
          if (currentSelected < tradeInventorySlots - 1) currentSelected += 1
        }
        else if (!inEquipment) {
          if ((currentSelected + 1) % inventoryColumns == 0) {
            if (!trading) {
              inEquipment = true
              currentSelected = currentSelected / inventoryColumns
            }
            else {
              inTraderInventory = true
              currentSelected = currentSelected / inventoryColumns * tradeInventoryColumns
            }
          }
          else if (currentSelected < inventorySlots - 1) currentSelected += 1
        }
      }

      if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) if (!trading) if (!moving) {
        var itemExistsInSlot = false
        if (inEquipment) {
          itemExistsInSlot = equipmentItems(currentSelected) != null
        }
        else itemExistsInSlot = inventoryItems.containsNonNull(currentSelected)
        if (itemExistsInSlot) {
          currentMoved = currentSelected
          moving = true
          movingInEquipment = inEquipment
        }
      }
      else {
        if (movingInEquipment) if (inEquipment) {
          val from = equipmentItems.getOrElse(currentMoved, null)
          val to = equipmentItems.getOrElse(currentSelected, null)
          val currentEquipmentType = getEquipmentSlotName(currentSelected)
          if (from == null || from.itemType.equipmentType == currentEquipmentType) {
            equipmentItems.put(currentMoved, to)
            equipmentItems.put(currentSelected, from)
            moving = false
            player.updateAttackType()
          }
        }
        else {
          val from = equipmentItems.getOrElse(currentMoved, null)
          val to = inventoryItems.getOrElse(currentSelected, null)
          val currentEquipmentType = getEquipmentSlotName(currentMoved)
          if (to == null || to.itemType.equipmentType == currentEquipmentType) {
            equipmentItems.put(currentMoved, to)
            inventoryItems.put(currentSelected, from)
            moving = false
            player.updateAttackType()
          }
        }
        else if (inEquipment) {
          val from = inventoryItems.getOrElse(currentMoved, null)
          val to = equipmentItems.getOrElse(currentSelected, null)
          val currentEquipmentType = getEquipmentSlotName(currentSelected)
          if (from == null || from.itemType.equipmentType == currentEquipmentType) {
            inventoryItems.put(currentMoved, to)
            equipmentItems.put(currentSelected, from)
            moving = false
            player.updateAttackType()
          }
        }
        else {
          val from = inventoryItems(currentMoved)
          val to = inventoryItems.getOrElse(currentSelected, null.asInstanceOf[Item])
          inventoryItems.put(currentMoved, to)
          inventoryItems.put(currentSelected, from)
          moving = false
          player.updateAttackType()
        }

        if (player.healthPoints > player.maxHealthPoints) player.healthPoints = player.maxHealthPoints
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.E)) if (inventoryOpen) if (trading) if (!inTraderInventory) if (inventoryItems(currentSelected) != null) sellSelectedItem()
      else if (traderInventoryItems(currentSelected) != null) if (gold - traderInventoryItems(currentSelected).itemType.worth >= 0) {
        takeItem(traderInventoryItems(currentSelected))
        gold -= traderInventoryItems(currentSelected).itemType.worth
        traderInventoryItems.remove(currentSelected)
      }
      else if (!inEquipment && !inTraderInventory) {
        val item = inventoryItems(currentSelected)
        if (item != null && item.itemType.consumable) {
          player.useItem(item)
          if (item.quantity <= 1) inventoryItems.remove(currentSelected)
          else item.quantity = item.quantity - 1
        }
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.ALT_LEFT)) { // drop item
        if (inventoryOpen) if (!inEquipment && !inTraderInventory) {
          if (inventoryItems.containsNonNull(currentSelected)) {
            val item = inventoryItems(currentSelected)

            assert(GameSystem.currentArea.nonEmpty)
            GameSystem.lootSystem.spawnLootPile(GameSystem.currentArea.get, player.rect.center.x, player.rect.center.y, item)
            inventoryItems.remove(currentSelected)
          }
        }
      }
    }
  }

  def getEquipmentSlotName(currentSelected: Int): String = {
    var currentEquipmentType: String = null
    if (currentSelected == 0) currentEquipmentType = "weapon"
    if (currentSelected == 1) currentEquipmentType = "helmet"
    if (currentSelected == 2) currentEquipmentType = "body"
    if (currentSelected == 3) currentEquipmentType = "gloves"
    if (currentSelected == 4) currentEquipmentType = "ring"
    if (currentSelected == 5) currentEquipmentType = "boots"
    currentEquipmentType
  }

  def openInventory(): Unit = {
    inventoryOpen = true
  }

  def closeInventory(): Unit = {
    inventoryOpen = false
    currentSelected = 0
    moving = false
    trading = false
    inTraderInventory = false
    inEquipment = false
    moving = false
    currentMoved = 0
    currentSelected = 0
    movingInEquipment = false
  }

  def sellSelectedItem(): Unit = {
    gold += (inventoryItems(currentSelected).itemType.worth * 0.3f).asInstanceOf[Int]
    inventoryItems.remove(currentSelected)
  }

  def pickUpItem(item: Item, itemList: ListBuffer[Item]): Boolean = {
    val itemType: ItemType = item.itemType
    val stackable: Boolean = itemType.stackable
    if (stackable) {
      var invPos: Int = -1
      for ((key, value) <- inventoryItems) {
        if ( invPos == -1 && value != null && (value.itemType == itemType)) {
          invPos = key
        }
      }
      if (invPos != -(1)) { // add quantity to existing item
        inventoryItems(invPos).quantity = inventoryItems(invPos).quantity + item.quantity
        if (item.lootPileBackref.itemList.size == 1) {
          item.lootPileBackref.visible = false
        }
        item.removeFromLoot()
        itemList -= item
        return true
      }
    }
    for (i <- 0 until inventorySlots) {
      if (inventoryItems.get(i) == null) { // if slot empty
        inventoryItems.put(i, item)
        item.lootPileBackref match {
          case treasure: Treasure => //register treasure picked up, dont spawn it again for this save
            try {
              val writer: FileWriter = new FileWriter("saves/treasure_collected.sav", true)
              val area: Area = item.lootPileBackref.area
              writer.write("treasure " + area.id + " " + area.treasureList.indexOf(treasure) + "\n")
              writer.close()
            } catch {
              case e: IOException =>
                e.printStackTrace()
            }
          case _ =>
        }
        if (item.lootPileBackref.itemList.size == 1) {
          item.lootPileBackref.visible = false
        }
        item.removeFromLoot()
        itemList -= item
        return true
      }
    }
    false
  }

  def takeItem(item: Item): Boolean = {
    val itemType = item.itemType
    val stackable = itemType.stackable
    if (stackable) {
      var invPos = -1

      for ((key, value) <- inventoryItems) {
        if (invPos == -1 && value != null && (value.itemType == itemType)) {
          invPos = key
        }
      }
      if (invPos != -1) { // add quantity to existing item
        inventoryItems(invPos).quantity = inventoryItems(invPos).quantity + item.quantity
        return true
      }
    }
    for (i <- 0 until inventorySlots) {
      if (inventoryItems.get(i) == null) { // if slot empty
        inventoryItems.put(i, item)
        return true
      }
    }
    false
  }

  def openTradeWindow(): Unit = {
    inventoryOpen = true
    trading = true
  }

  def setTraderInventory(traderInventory: List[Item]): Unit = {
    traderInventoryItems = mutable.Map()
    var i: Int = 0
    for (traderItem <- traderInventory) {
      traderInventoryItems.put(i, traderItem)
      i += 1
    }
  }

}
