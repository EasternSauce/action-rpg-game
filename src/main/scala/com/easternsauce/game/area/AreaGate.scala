package com.easternsauce.game.area

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d._
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class AreaGate(val areaFrom: Area, val fromPosX: Int, val fromPosY: Int, val areaTo: Area, val toPosX: Int, val toPosY: Int) {

  private val width = 50f
  private val height = 50f

  private var body: Body = _

  val fromRect = new Rectangle(fromPosX - width / 2, fromPosY - height / 2, width, height)
  val toRect = new Rectangle(toPosX - width / 2, toPosY - height / 2, width, height)

  initBody(areaFrom, fromRect)
  initBody(areaTo, toRect)

  def render(shapeDrawer: ShapeDrawer): Unit = {
    val currentArea = GameSystem.currentArea.getOrElse {
      throw new RuntimeException("current area not specified")
    }

    if (currentArea == areaFrom) shapeDrawer.filledRectangle(fromRect, Color.BLUE)
    if (currentArea == areaTo) shapeDrawer.filledRectangle(toRect, Color.BLUE)
  }

  def initBody(area: Area, rect: Rectangle): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position.set((rect.x + width / 2) / GameSystem.PixelsPerMeter, (rect.y + height / 2) / GameSystem.PixelsPerMeter)
    bodyDef.`type` = BodyDef.BodyType.StaticBody
    body = area.world.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef: FixtureDef = new FixtureDef()

    fixtureDef.isSensor = true
    val shape : PolygonShape = new PolygonShape()

    shape.setAsBox((rect.width / 2) / GameSystem.PixelsPerMeter, (rect.height / 2) / GameSystem.PixelsPerMeter)

    fixtureDef.shape = shape
    body.createFixture(fixtureDef)

  }
}
