package com.easternsauce.game.spawn

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, FixtureDef, PolygonShape}
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.easternsauce.game.area.Area
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.utils.EsTimer
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class PlayerRespawnPoint(posX: Int, posY: Int, val area: Area) {

  val width = 64f
  val height = 64f

  val rect: Rectangle = new Rectangle(posX, posY, width, height)

  val respawnSetTimer = EsTimer()

  val respawnSetTime: Float = 5f

  val gobletImage = new Image(Assets.gobletTexture)
  val gobletLitImage = new Image(Assets.gobletLitTexture)

  var body: Body = _

  initBody()

  respawnSetTimer.time = respawnSetTime

  gobletImage.setPosition(posX, posY)
  gobletLitImage.setPosition(posX, posY)

  def render(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    if (respawnSetTimer.time < respawnSetTime) gobletLitImage.draw(batch, 1.0f)
    else gobletImage.draw(batch, 1.0f)


  }

  def onRespawnSet(): Unit = {
    respawnSetTimer.restart()
    Assets.matchIgniteSound.play(0.4f)
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
