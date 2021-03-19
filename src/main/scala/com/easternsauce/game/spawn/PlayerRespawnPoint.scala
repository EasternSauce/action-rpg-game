package com.easternsauce.game.spawn

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, FixtureDef, PolygonShape}
import com.easternsauce.game.area.Area
import com.easternsauce.game.shapes.CustomRectangle
import com.easternsauce.game.utils.SimpleTimer
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class PlayerRespawnPoint(posX: Int, posY: Int, val area: Area) {

  val width = 30f
  val height = 30f

  val rect: CustomRectangle = new CustomRectangle(posX, posY, width, height)

  private var respawnSetTimer = SimpleTimer()

  private val respawnSetTime: Float = 2f

  var body: Body = _

  initBody()

  respawnSetTimer.time = respawnSetTime

  def render(shapeDrawer: ShapeDrawer): Unit = {
    if (respawnSetTimer.time < respawnSetTime) shapeDrawer.setColor(Color.RED)
    else shapeDrawer.setColor(Color.ORANGE)

    shapeDrawer.filledRectangle(rect.x, rect.y, width, height)

  }

  def onRespawnSet(): Unit = {
    respawnSetTimer.restart()
  }

  def initBody(): Unit = {
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
