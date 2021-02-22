package com.easternsauce.game.item.loot

import com.badlogic.gdx.graphics.Color
import com.easternsauce.game.area.Area
import space.earlygrey.shapedrawer.ShapeDrawer

class Treasure(override val area: Area, x: Float, y: Float) extends LootPile(area, x, y) {

  override def render(shapeDrawer: ShapeDrawer): Unit = {
    if (visible) {
      shapeDrawer.filledRectangle(rect, Color.PINK)
    }
  }
}
