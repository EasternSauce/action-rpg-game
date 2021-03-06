package com.easternsauce.game.item.loot

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.area.Area
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.util.ItemType
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class LootSystem private {
  private var visibleItems: ListBuffer[Item] = ListBuffer()

  def render(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {

    assert(GameSystem.currentArea.nonEmpty)

    for (lootPile <- GameSystem.currentArea.get.lootPileList) {
      lootPile.render(batch, shapeDrawer)
    }

    for (treasure <- GameSystem.currentArea.get.remainingTreasureList) {
      treasure.render(batch, shapeDrawer)
    }
  }

  def update(): Unit = {

    assert(GameSystem.currentArea.nonEmpty)

    visibleItems = new ListBuffer[Item]

    for (lootPile <- GameSystem.currentArea.get.lootPileList) {
      if (GameSystem.currentArea.get == lootPile.area && lootPile.bodyCreated) {
        if (GameSystem.distance(GameSystem.playerCharacter.body, lootPile.body) < 40f) {
          GameSystem.lootOptionWindow.visible = true
          visibleItems.addAll(lootPile.itemList)
        }
      }

      if (!lootPile.bodyCreated) {
        lootPile.initBody()
        lootPile.bodyCreated = true
      }
    }

    for (treasure <- GameSystem.currentArea.get.remainingTreasureList) {
      if (GameSystem.currentArea.get == treasure.area && treasure.bodyCreated) {
        if (GameSystem.distance(GameSystem.playerCharacter.body, treasure.body) < 40f) {
          GameSystem.lootOptionWindow.visible = true
          visibleItems.addAll(treasure.itemList)
        }
      }

      if (!treasure.bodyCreated) {
        treasure.initBody()
        treasure.bodyCreated = true
      }
    }
    GameSystem.lootOptionWindow.setLootOptions(visibleItems)
    GameSystem.lootOptionWindow.update()
  }

  def spawnLootPile(area: Area, x: Float, y: Float, dropTable: mutable.Map[String, Float]): Unit = {
    val newLootPile = LootPile(area, x, y)
    for ((key, value) <- dropTable) {
      if (GameSystem.random.nextFloat < value) {
        val item = Item(ItemType.getItemType(key), newLootPile)
        newLootPile.itemList += item
      }
    }
    if (newLootPile.itemList.nonEmpty) area.lootPileList += newLootPile
  }

  def spawnLootPile(area: Area, x: Float, y: Float, item: Item): Unit = {
    val newLootPile = LootPile(area, x, y)
    newLootPile.itemList += item
    if (newLootPile.itemList.nonEmpty) {
      item.lootPileBackref = newLootPile
      area.lootPileList += newLootPile
    }
  }

  def placeTreasure(area: Area, x: Float, y: Float, itemType: ItemType): Unit = {
    val treasure = Treasure(area, x, y)
    treasure.itemList += Item(itemType, treasure)
    area.treasureList += treasure
    area.remainingTreasureList += treasure
  }

  def getVisibleItemsCount: Int = visibleItems.size
}

object LootSystem {
  def apply() = new LootSystem()
}
