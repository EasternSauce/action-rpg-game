package com.easternsauce.game.ability.attack

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, FixtureDef, PolygonShape}
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.{CustomPolygon, CustomRectangle, CustomVector2}
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.language.implicitConversions

abstract class MeleeAttack(override val abilityCreature: Creature) extends Attack(abilityCreature) {
  protected var attackAnimation: EsAnimation = _
  protected var windupAnimation: EsAnimation = _
  //protected val weaponSound: Sound = Assets.attackSound
  protected var aimed = false

  protected var width: Float = _
  protected var height: Float = _
  protected var scale = 1.0f
  protected var attackRange: Float = _

  var knockbackPower = 15f

  var body: Body = _

  var hitbox: AttackHitbox = _

  implicit def rectConversion(s: com.badlogic.gdx.math.Rectangle): CustomRectangle = new CustomRectangle(s.x, s.y, s.width, s.height)

  override def onActiveStart(): Unit = {
    super.onActiveStart()

    attackAnimation.restart()

    abilityCreature.takeStaminaDamage(15f)

    Assets.attackSound.play(0.1f)

    var attackVector = abilityCreature.attackVector
    val theta = CustomVector2(attackVector.x, attackVector.y).angleDeg()

    if (attackVector.len() > 0f) {
      attackVector = CustomVector2(attackVector.x / attackVector.len(), attackVector.y / attackVector.len())
    }

    val attackShiftX = attackVector.x * attackRange
    val attackShiftY = attackVector.y * attackRange

    val attackRectX = attackShiftX + abilityCreature.centerPosX
    val attackRectY = attackShiftY + abilityCreature.centerPosY

    val poly = new CustomPolygon(new CustomRectangle(0,0, width, height))

    poly.setOrigin(0, height / 2)
    poly.setRotation(theta)
    //poly.setPosition(attackRectX, attackRectY)
    poly.translate(0, -height / 2)
    poly.setScale(scale, scale)

    hitbox = AttackHitbox(attackRectX, attackRectY, poly)

    initBody(hitbox)
  }

  override def onUpdateActive(): Unit = {
    super.onUpdateActive()

  }

//  protected def updateAttackRect(): Unit = {
//    val attackVector = abilityCreature.attackVector
//
//    if (attackVector.length() > 0f) attackVector.normalise()
//
//    val attackWidth = width * scale
//    val attackHeight = 32 * scale
//
//
//    val attackShiftX = attackVector.x * attackRange
//    val attackShiftY = attackVector.y * attackRange
//
//    val attackRectX = attackShiftX + abilityCreature.rect.center.x
//    val attackRectY = attackShiftY + abilityCreature.rect.center.y
//
//    meleeAttackRect = new com.easternsauce.game.shapes.Rectangle(attackRectX, attackRectY - height * scale, attackWidth, attackHeight)
//
//    meleeAttackHitbox = new com.easternsauce.game.shapes.Polygon(meleeAttackRect)
//
//    val theta = CustomVector2(attackVector.x, attackVector.y).angleDeg()
//
//    meleeAttackHitbox.rotate(theta)
//
//    meleeAttackHitbox.translate(0, height / 2 * scale)
//  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    super.render(shapeDrawer, batch)

//    val image = windupAnimation.getFrameByIndex(5)
//    val attackVector = abilityCreature.facingVector
//    val theta = -CustomVector2(attackVector.x, attackVector.y).angleDeg()
//
//    if (attackVector.length() > 0f) attackVector.normalise()
//
//    val attackShiftX = attackVector.x * attackRange
//    val attackShiftY = -attackVector.y * attackRange
//
//    println(attackShiftX + " " + attackShiftY)
//
//    val attackRectX = attackShiftX + abilityCreature.rect.center.x
//    val attackRectY = attackShiftY + abilityCreature.rect.center.y
//
//    image.setOrigin(0, height / 2 * scale)
//    image.setRotation(theta)
//    image.setPosition(attackRectX, attackRectY)
//    image.translate(0, -height / 2  * scale)
//
//    image.draw(batch)


    if (state == AbilityState.Channeling) {
      val image = windupAnimation.currentFrame

      val attackVector = abilityCreature.attackVector
      val theta = CustomVector2(attackVector.x, attackVector.y).angleDeg()

      batch.draw(image, hitbox.x, hitbox.y - height / 2, 0, height / 2,
        image.getRegionWidth, image.getRegionHeight, scale, scale, theta)
    }
    if (state == AbilityState.Active) {
      val image = attackAnimation.currentFrame


//      if (GameSystem.drawAttackHitboxes) {
//        shapeDrawer.filledPolygon(poly)
//      }

      val attackVector = abilityCreature.attackVector
      val theta = CustomVector2(attackVector.x, attackVector.y).angleDeg()

      batch.draw(image, hitbox.x, hitbox.y - height / 2, 0, height / 2,
        image.getRegionWidth, image.getRegionHeight, scale, scale, theta)
    }
  }

  override def onChannellingStart(): Unit = {
    abilityCreature.attackVector = abilityCreature.facingVector
    windupAnimation.restart()
    abilityCreature.isAttacking = true

    var attackVector = abilityCreature.attackVector
    val theta = CustomVector2(attackVector.x, attackVector.y).angleDeg()

    if (attackVector.len() > 0f) {
      attackVector = CustomVector2(attackVector.x / attackVector.len(), attackVector.y / attackVector.len())
    }

    val attackShiftX = attackVector.x * attackRange
    val attackShiftY = attackVector.y * attackRange

    val attackRectX = attackShiftX + abilityCreature.centerPosX
    val attackRectY = attackShiftY + abilityCreature.centerPosY

    val poly = new CustomPolygon(new CustomRectangle(0,0, width, height))

    poly.setOrigin(0, height / 2)
    poly.setRotation(theta)
    //poly.setPosition(attackRectX, attackRectY)
    poly.translate(0, -height / 2)
    poly.setScale(scale, scale)

    hitbox = AttackHitbox(attackRectX, attackRectY, poly)

  }


  override def update(): Unit = {
    super.update()

  }

  def initBody(hitbox: AttackHitbox): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(hitbox.x / GameSystem.PixelsPerMeter, hitbox.y / GameSystem.PixelsPerMeter)

    bodyDef.`type` = BodyDef.BodyType.StaticBody
    body = abilityCreature.area.world.createBody(bodyDef)
    body.setUserData(this)

    val converted = hitbox.polygon.getTransformedVertices.map(a => a / GameSystem.PixelsPerMeter)

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: PolygonShape = new PolygonShape()
    //shape.setRadius(30 / GameSystem.PixelsPerMeter)
    shape.set(converted)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
    //body.setLinearDamping(10f)
  }

  override def onUpdateHitbox(): Unit = {
    super.onUpdateHitbox()

    if (hitbox != null) {
      var attackVector = abilityCreature.attackVector

      if (attackVector.len() > 0f) {
        attackVector = CustomVector2(attackVector.x / attackVector.len(), attackVector.y / attackVector.len())
      }

      val attackShiftX = attackVector.x * attackRange
      val attackShiftY = attackVector.y * attackRange

      hitbox.x = attackShiftX + abilityCreature.centerPosX
      hitbox.y = attackShiftY + abilityCreature.centerPosY

      if (body != null) {
        body.setTransform(hitbox.x / GameSystem.PixelsPerMeter, hitbox.y / GameSystem.PixelsPerMeter, 0f)
      }
    }

  }

  override def onStop() {
    super.onStop()

    body.getWorld.destroyBody(body)

  }
}

