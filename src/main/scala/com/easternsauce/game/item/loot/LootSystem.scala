package com.easternsauce.game.item.loot

import com.easternsauce.game.area.Area
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.util.ItemType
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class LootSystem {
  private var visibleItems: ListBuffer[Item] = ListBuffer()

  def render(shapeDrawer: ShapeDrawer): Unit = {

    assert(GameSystem.currentArea.nonEmpty)

    for (lootPile <- GameSystem.currentArea.get.lootPileList) {
      lootPile.render(shapeDrawer)
    }

    for (treasure <- GameSystem.currentArea.get.remainingTreasureList) {
      treasure.render(shapeDrawer)
    }
  }


  def update(): Unit = {

    assert(GameSystem.currentArea.nonEmpty)

    visibleItems = new ListBuffer[Item]

    for (lootPile <- GameSystem.currentArea.get.lootPileList) {
      if (GameSystem.currentArea.get == lootPile.area) if (GameSystem.distance(GameSystem.playerCharacter.body, lootPile.body) < 40f) {
        GameSystem.lootOptionWindow.visible = true
        visibleItems.addAll(lootPile.itemList)
      }
    }

    for (treasure <- GameSystem.currentArea.get.remainingTreasureList) {
      if (GameSystem.currentArea.get == treasure.area) if (GameSystem.distance(GameSystem.playerCharacter.body, treasure.body) < 40f) {
        GameSystem.lootOptionWindow.visible = true
        visibleItems.addAll(treasure.itemList)
      }
    }
    GameSystem.lootOptionWindow.setLootOptions(visibleItems)
    GameSystem.lootOptionWindow.update()
  }

  def spawnLootPile(area: Area, x: Float, y: Float, dropTable: mutable.Map[String, Float]): Unit = {
    val newLootPile = new LootPile(area, x, y)
    for ((key, value) <- dropTable) {
      if (GameSystem.random.nextFloat < value) {
        val item = new Item(ItemType.getItemType(key), newLootPile)
        newLootPile.itemList += item
      }
    }
    if (newLootPile.itemList.nonEmpty) area.lootPileList += newLootPile
  }

  def spawnLootPile(area: Area, x: Float, y: Float, item: Item): Unit = {
    val newLootPile = new LootPile(area, x, y)
    newLootPile.itemList += item
    if (newLootPile.itemList.nonEmpty) {
      item.lootPileBackref = newLootPile
      area.lootPileList += newLootPile
    }
  }

  def placeTreasure(area: Area, x: Float, y: Float, itemType: ItemType): Unit = {
    val treasure = new Treasure(area, x, y)
    treasure.itemList += new Item(itemType, treasure)
    area.treasureList += treasure
    area.remainingTreasureList += treasure
  }

  def getVisibleItemsCount: Int = visibleItems.size
}
