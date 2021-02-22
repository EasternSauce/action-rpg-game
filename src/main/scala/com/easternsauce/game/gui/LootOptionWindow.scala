package com.easternsauce.game.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.{Gdx, Input}
import com.easternsauce.game.item.Item
import system.GameSystem

import scala.collection.mutable.ListBuffer

class LootOptionWindow {


  private var currentSelected = 0


  private var itemList: ListBuffer[Item] = ListBuffer()

  private var scroll = 0

  var activated = false

  var visible = false


  def render(batch: SpriteBatch): Unit = {
    if (visible) for (i <- 0 until Math.min(4, itemList.size)) {

      GameSystem.font.setColor(Color.WHITE)
      GameSystem.font.draw(batch, (if (currentSelected == (i + scroll) && activated) ">"
      else "") + itemList(i + scroll).name, 10, Gdx.graphics.getHeight - (Gdx.graphics.getHeight * GameSystem.ScreenProportion + 10 + 30 * i))
    }
  }

  def update(): Unit = {
    if (visible) if (activated) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.W)) if (currentSelected > 0) {
        currentSelected -= 1
        if (scroll > currentSelected) scroll -= 1
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.S)) if (currentSelected < itemList.size - 1) {
        currentSelected += 1
        if (scroll + 4 <= currentSelected) scroll += 1
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.E)) if (itemList.nonEmpty) if (!GameSystem.inventoryWindow.inventoryOpen) {
        val isPickedUp = GameSystem.inventoryWindow.pickUpItem(itemList(currentSelected), itemList)
        if (isPickedUp) {
          if (currentSelected > 0) currentSelected -= 1
          if (scroll > 0) scroll -= 1
          if (itemList.isEmpty) {
            currentSelected = 0
            activated = false
          }
        }
      }
      if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) if (!GameSystem.escRecently) {
        activated = false
        currentSelected = 0
        GameSystem.escRecently = true
      }
    }
    else if (Gdx.input.isKeyJustPressed(Input.Keys.E)) if (itemList.nonEmpty) {
      activated = true
      currentSelected = 0
    }
  }

  def setLootOptions(itemsInLoot: ListBuffer[Item]): Unit = {
    if (!(itemsInLoot == itemList)) {
      itemList = ListBuffer.from(itemsInLoot)
    }
  }

}
