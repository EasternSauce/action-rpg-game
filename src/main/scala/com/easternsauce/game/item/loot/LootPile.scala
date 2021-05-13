package com.easternsauce.game.item.loot

import com.badlogic.gdx.graphics.{Color, Texture}
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, FixtureDef, PolygonShape}
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.easternsauce.game.area.Area
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.item.Item
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.collection.mutable.ListBuffer

class LootPile(val area: Area, x: Float, y: Float) {
  private val width = 10
  private val height = 10

  var body: Body = _

  var visible = true

  val rect: Rectangle = new Rectangle(x,y,width,height)

  val itemList: ListBuffer[Item] = ListBuffer()

  var bodyCreated = false

  val bagImage: Image = new Image(Assets.bagTexture)

  bagImage.setX(x)
  bagImage.setY(y)


  def render(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    if (visible) {
      bagImage.draw(batch, 1.0f)
    }
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
