package com.easternsauce.game.area

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d._
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.easternsauce.game.assets.Assets
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class AreaGate(
  val areaFrom: Area,
  val fromPosX: Int,
  val fromPosY: Int,
  val areaTo: Area,
  val toPosX: Int,
  val toPosY: Int
) {

  private val width = 48f
  private val height = 48f

  val fromRect = new Rectangle(fromPosX, fromPosY, width, height)
  val toRect = new Rectangle(toPosX, toPosY, width, height)

  private val downArrowImageFrom = new Image(Assets.downArrowTexture)
  private val downArrowImageTo = new Image(Assets.downArrowTexture)

  downArrowImageFrom.setPosition(fromPosX, fromPosY)
  downArrowImageTo.setPosition(toPosX, toPosY)
  downArrowImageFrom.setScale(1.5f)
  downArrowImageTo.setScale(1.5f)

  private var body: Body = _

  initBody(areaFrom, fromRect)
  initBody(areaTo, toRect)

  def render(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    val currentArea = GameSystem.currentArea.getOrElse {
      throw new RuntimeException("current area not specified")
    }

    if (currentArea == areaFrom) downArrowImageFrom.draw(batch, 1.0f)
    if (currentArea == areaTo) downArrowImageTo.draw(batch, 1.0f)
  }

  def initBody(area: Area, rect: Rectangle): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position
      .set((rect.x + width / 2) / GameSystem.PixelsPerMeter, (rect.y + height / 2) / GameSystem.PixelsPerMeter)
    bodyDef.`type` = BodyDef.BodyType.StaticBody
    body = area.world.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef: FixtureDef = new FixtureDef()

    fixtureDef.isSensor = true
    val shape: PolygonShape = new PolygonShape()

    shape.setAsBox((rect.width / 2) / GameSystem.PixelsPerMeter, (rect.height / 2) / GameSystem.PixelsPerMeter)

    fixtureDef.shape = shape
    body.createFixture(fixtureDef)

  }
}

object AreaGate {
  def apply(areaFrom: Area, fromPosX: Int, fromPosY: Int, areaTo: Area, toPosX: Int, toPosY: Int) =
    new AreaGate(areaFrom, fromPosX, fromPosY, areaTo, toPosX, toPosY)
}
