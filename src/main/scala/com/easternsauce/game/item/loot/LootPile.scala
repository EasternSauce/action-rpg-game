package com.easternsauce.game.item.loot

import com.badlogic.gdx.graphics.Color
import com.easternsauce.game.area.Area
import com.easternsauce.game.item.Item
import com.easternsauce.game.shapes.{CustomBatch, CustomRectangle}

import scala.collection.mutable.ListBuffer

class LootPile(val area: Area, x: Float, y: Float) {
  private val width = 10
  private val height = 10


  var visible = true

  val rect: CustomRectangle = new CustomRectangle(x,y,width,height)

  val itemList: ListBuffer[Item] = ListBuffer()

  def render(spriteBatch: CustomBatch): Unit = {
    if (visible) {
      spriteBatch.drawRect(rect, Color.GREEN)
    }
  }

}
