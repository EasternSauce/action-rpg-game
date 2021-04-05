package com.easternsauce.game.projectile

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, CircleShape, FixtureDef}
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.easternsauce.game.area.Area
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Arrow(var startX: Float, var startY: Float, val area: Area, var dirVector: Vector2, var arrowList: ListBuffer[Arrow], val tiledMap: TiledMap, val creatures: mutable.Map[String, Creature], val shooter: Creature) {


  private val arrowTexture: Texture = Assets.arrowTexture

  private val arrowImage: Image = new Image(arrowTexture)

  var markedForDeletion: Boolean = false

  val maxSpeedRelative = 18f

  var body: Body = _

  val damage: Float = shooter.weaponDamage

  var isActive: Boolean = true


  val shooterRelatedVelocity: Vector2 = new Vector2(dirVector.x * maxSpeedRelative, dirVector.y * maxSpeedRelative).add(shooter.body.getLinearVelocity)
  dirVector = shooterRelatedVelocity.cpy().nor()

  arrowImage.setOriginX(arrowTexture.getWidth / 2)
  arrowImage.setOriginY(arrowTexture.getHeight / 2)
  arrowImage.rotateBy(dirVector.angleDeg())

  initBody(startX, startY)

  def render(batch: SpriteBatch): Unit = {

    arrowImage.draw(batch, 1.0f)
  }

  def update(): Unit = {
    if (isActive) {
      val acceleration = 3f

      var accX = 0f
      var accY = 0f

      // accelerate proportional to dirVector value!

      if (dirVector.x < 0) {
        if (body.getLinearVelocity.x > shooterRelatedVelocity.x) {
          accX = -acceleration * Math.abs(dirVector.x)
        }
      }
      else if (dirVector.x > 0) {
        if (body.getLinearVelocity.x < shooterRelatedVelocity.x) {
          accX = acceleration * Math.abs(dirVector.x)
        }
      }

      if (dirVector.y < 0) {
        if (body.getLinearVelocity.y > shooterRelatedVelocity.y) {
          accY = -acceleration * Math.abs(dirVector.y)
        }
      }
      else if (dirVector.y > 0) {
        if (body.getLinearVelocity.y < shooterRelatedVelocity.y) {
          accY = acceleration * Math.abs(dirVector.y)
        }
      }

      body.applyLinearImpulse(new Vector2(accX, accY), body.getWorldCenter, true)

      arrowImage.setX(body.getPosition.x * GameSystem.PixelsPerMeter - arrowImage.getImageWidth / 2f)
      arrowImage.setY(body.getPosition.y * GameSystem.PixelsPerMeter - arrowImage.getImageHeight / 2f)

      val margin = 50
      if (!((body.getPosition.x * GameSystem.PixelsPerMeter >= 0 - margin
        && body.getPosition.x * GameSystem.PixelsPerMeter < GameSystem.getTiledMapRealWidth(tiledMap) + margin)
        && (body.getPosition.y * GameSystem.PixelsPerMeter >= 0 - margin
        && body.getPosition.y * GameSystem.PixelsPerMeter < GameSystem.getTiledMapRealHeight(tiledMap) + margin))) markedForDeletion = true

    }
  }

  def onCollideWithCreature(creature: Creature): Unit = {
    if (!(shooter.isMob && creature.isMob) && isActive ) {
      if (shooter != creature && creature.isAlive) {
        creature.takeDamage(damage, immunityFrames = true, 30f, startX, startY)
        markedForDeletion = true
      }
    }
  }

  def onCollideWithTerrain(): Unit = {
    body.setLinearVelocity(new Vector2(0f, 0f))
    isActive = false
  }

  def initBody(x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)

    bodyDef.`type` = BodyDef.BodyType.DynamicBody
    body = area.world.createBody(bodyDef)
    body.setUserData(this)

    val radius = 10f

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: CircleShape = new CircleShape()
    shape.setRadius(radius / GameSystem.PixelsPerMeter)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
  }
}
