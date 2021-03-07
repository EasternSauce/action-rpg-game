package com.easternsauce.game.area

import com.badlogic.gdx.graphics.Color
import com.easternsauce.game.shapes.CustomRectangle
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class AreaGate(val areaFrom: Area, val fromPosX: Int, val fromPosY: Int, val areaTo: Area, val toPosX: Int, val toPosY: Int) {

  private val width = 50
  private val height = 50

  val fromRect = new CustomRectangle(fromPosX, fromPosY, width, height)
  val toRect = new CustomRectangle(toPosX, toPosY, width, height)

  def render(shapeDrawer: ShapeDrawer): Unit = {
    val currentArea = GameSystem.currentArea.getOrElse {
      throw new RuntimeException("current area not specified")
    }

    if (currentArea == areaFrom) shapeDrawer.filledRectangle(fromRect, Color.BLUE)
    if (currentArea == areaTo) shapeDrawer.filledRectangle(toRect, Color.BLUE)
  }

  def update(): Unit = {
    for (area <- GameSystem.areas.values) {
      area.creaturesManager.updateGatesLogic(this)
    }
  }
}
