package com.easternsauce.game.area

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.easternsauce.game.shapes.Rectangle
import system.GameSystem

class AreaGate(val areaFrom: Area, val fromPosX: Int, val fromPosY: Int, val areaTo: Area, val toPosX: Int, val toPosY: Int) {
  val fromRect = new Rectangle(fromPosX, fromPosY, 50, 50)
  val toRect = new Rectangle(toPosX, toPosY, 50, 50)

  def renderShapes(shapeRenderer: ShapeRenderer): Unit = {
    shapeRenderer.setColor(Color.BLUE)

    val currentArea = GameSystem.currentArea.getOrElse {
      throw new RuntimeException("current area not specified")
    }

    if (currentArea == areaFrom) shapeRenderer.rect(fromRect.getX, fromRect.getY, fromRect.getWidth, fromRect.getHeight)
    if (currentArea == areaTo) shapeRenderer.rect(toRect.getX, toRect.getY, toRect.getWidth, toRect.getHeight)
  }

  def update(): Unit = {
    for (area <- GameSystem.areaList) {
      area.creaturesManager.updateGatesLogic(this, GameSystem.currentAreaHolder)
    }
  }
}
