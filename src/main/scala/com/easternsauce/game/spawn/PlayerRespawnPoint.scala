package com.easternsauce.game.spawn

import com.badlogic.gdx.graphics.Color
import com.easternsauce.game.area.Area
import com.easternsauce.game.shapes.CustomRectangle
import com.easternsauce.game.utils.SimpleTimer
import space.earlygrey.shapedrawer.ShapeDrawer

class PlayerRespawnPoint(posX: Int, posY: Int, val area: Area) {

  val width = 30f
  val height = 30f

  val rect: CustomRectangle = new CustomRectangle(posX, posY, width, height)

  private var respawnSetTimer = SimpleTimer()

  private val respawnSetTime: Float = 2f

  respawnSetTimer.time = respawnSetTime

  def render(shapeDrawer: ShapeDrawer): Unit = {
    if (respawnSetTimer.time < respawnSetTime) shapeDrawer.setColor(Color.RED)
    else shapeDrawer.setColor(Color.ORANGE)

    shapeDrawer.filledRectangle(rect.center.x, rect.center.y, width, height)

  }

  def onRespawnSet(): Unit = {
    respawnSetTimer.resetStart()
  }


}
