package com.easternsauce.game.item.loot

import com.badlogic.gdx.graphics.Color
import com.easternsauce.game.area.Area
import com.easternsauce.game.shapes.CustomBatch

class Treasure(override val area: Area, x: Float, y: Float) extends LootPile(area, x, y) {

  override def render(spriteBatch: CustomBatch): Unit = {
    if (visible) {
      spriteBatch.drawRect(rect, Color.PINK)
    }
  }
}
