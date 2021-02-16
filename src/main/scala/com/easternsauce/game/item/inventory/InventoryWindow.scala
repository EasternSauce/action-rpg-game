package com.easternsauce.game.item.inventory

import java.util
import java.util.Arrays

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.util.ItemType
import com.easternsauce.game.shapes.{CustomBatch, CustomRectangle}
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class InventoryWindow {
  private val background: CustomRectangle = new CustomRectangle((Gdx.graphics.getWidth * 0.1).toInt, (Gdx.graphics.getHeight * 0.1).toInt, (Gdx.graphics.getWidth * 0.6).toInt, (Gdx.graphics.getHeight * 0.6).toInt)

  private var slotList: ListBuffer[CustomRectangle] = ListBuffer()
  private var equipmentSlotList: ListBuffer[CustomRectangle] = ListBuffer()
  private var traderInventorySlotList: ListBuffer[CustomRectangle] = ListBuffer()


  private var inventoryOpen: Boolean = false


  private var currentSelected: Int = 0

  private var moving: Boolean = false
  private var currentMoved: Int = 0
  private var movingInEquipment: Boolean = false

  private var inventoryItems: mutable.Map[Integer, Item] = mutable.Map()


  private var equipmentItems: mutable.Map[Integer, Item] = mutable.Map()

  private var traderInventoryItems: mutable.Map[Integer, Item] = mutable.Map()


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

  private var gold: Int = 0

  ItemType.loadItemTypes()

  for (i <- 0 until inventorySlots) {
    val col = i % inventoryColumns
    val row = i / inventoryColumns
    val slot = new CustomRectangle(background.getX + margin + (space + slotWidth) * col, background.getY + margin + (space + slotHeight) * row, slotWidth, slotHeight)
    slotList += slot
  }

  for (i <- 0 until equipmentSlots) {
    val col = inventoryColumns + 2
    val slot = new CustomRectangle(background.getX + margin + (space + slotWidth) * col, background.getY + margin + (space + slotHeight) * i, slotWidth, slotHeight)
    equipmentSlotList += slot
  }

  for (i <- 0 until tradeInventorySlots) {
    val col = inventoryColumns + 1 + i % tradeInventoryColumns
    val row = i / tradeInventoryColumns
    val slot = new CustomRectangle(background.getX + margin + (space + slotWidth) * col, background.getY + margin + (space + slotHeight) * row + 30, slotWidth, slotHeight)
    traderInventorySlotList += slot
  }

  def render(batch: CustomBatch): Unit = {
    if (inventoryOpen) {

      batch.drawRect(background, Color.DARK_GRAY)

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

        batch.drawRect(traderInventorySlotList(i), color)
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


  def renderInventory(batch: CustomBatch) = ???

  def renderItemDescription(batch: CustomBatch) = ???

  def renderEquipment(batch: CustomBatch) = ???

}
