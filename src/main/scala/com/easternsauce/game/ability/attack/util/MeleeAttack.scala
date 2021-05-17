package com.easternsauce.game.ability.attack.util

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, FixtureDef, PolygonShape}
import com.easternsauce.game.ability.attack.util
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.CustomPolygon
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.language.implicitConversions

abstract class MeleeAttack(override val abilityCreature: Creature)
    extends Attack(abilityCreature) {

  var scale: Float
  var attackRange: Float
  var body: Body = _
  var hitbox: AttackHitbox = _
  var toRemoveBody = false
  var bodyActive =
    false // IMPORTANT: do NOT use body after already destroyed (otherwise weird behavior occurs, because, for some reason,
  //protected val weaponSound: Sound = Assets.attackSound
  protected var aimed: Boolean
  protected var width: Float
  protected var height: Float
  protected var knockbackPower: Float
  // the reference can STILL be attached to some other random body after destruction, like arrow bodies)

  implicit def rectConversion(s: com.badlogic.gdx.math.Rectangle): Rectangle =
    new Rectangle(s.x, s.y, s.width, s.height)

  override def onActiveStart(): Unit = {
    super.onActiveStart()

    abilityAnimation.restart()

    abilityCreature.takeStaminaDamage(15f)

    Assets.attackSound.play(0.1f)

    var attackVector = abilityCreature.attackVector
    val theta = new Vector2(attackVector.x, attackVector.y).angleDeg()

    if (attackVector.len() > 0f) {
      attackVector = new Vector2(
        attackVector.x / attackVector.len(),
        attackVector.y / attackVector.len()
      )
    }

    val attackShiftX = attackVector.x * attackRange
    val attackShiftY = attackVector.y * attackRange

    val attackRectX = attackShiftX + abilityCreature.posX
    val attackRectY = attackShiftY + abilityCreature.posY

    val poly = new CustomPolygon(new Rectangle(0, 0, width, height))

    poly.setOrigin(0, height / 2)
    poly.setRotation(theta)
    //poly.setPosition(attackRectX, attackRectY)
    poly.translate(0, -height / 2)
    poly.setScale(scale, scale)

    hitbox = util.AttackHitbox(attackRectX, attackRectY, poly)

    initBody(hitbox)
    bodyActive = true

    toRemoveBody = false
  }

  def initBody(hitbox: AttackHitbox): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(
      hitbox.x / GameSystem.PixelsPerMeter,
      hitbox.y / GameSystem.PixelsPerMeter
    )

    bodyDef.`type` = BodyDef.BodyType.KinematicBody
    body = abilityCreature.area.world.createBody(bodyDef)
    body.setUserData(this)

    val converted = hitbox.polygon.getTransformedVertices.map(a =>
      a / GameSystem.PixelsPerMeter
    )

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: PolygonShape = new PolygonShape()
    shape.set(converted)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
  }

  override def onUpdateActive(): Unit = {
    super.onUpdateActive()

  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    super.render(shapeDrawer, batch)

    if (state == AbilityState.Channeling) {
      val image = abilityWindupAnimation.currentFrame

      val attackVector = abilityCreature.attackVector
      val theta = new Vector2(attackVector.x, attackVector.y).angleDeg()

      batch.draw(
        image,
        hitbox.x,
        hitbox.y - height / 2,
        0,
        height / 2,
        image.getRegionWidth,
        image.getRegionHeight,
        scale,
        scale,
        theta
      )
    }
    if (state == AbilityState.Active) {
      val image = abilityAnimation.currentFrame

      val attackVector = abilityCreature.attackVector
      val theta = new Vector2(attackVector.x, attackVector.y).angleDeg()

      batch.draw(
        image,
        hitbox.x,
        hitbox.y - height / 2,
        0,
        height / 2,
        image.getRegionWidth,
        image.getRegionHeight,
        scale,
        scale,
        theta
      )
    }
  }

  override def onChannellingStart(): Unit = {
    abilityCreature.attackVector = abilityCreature.facingVector
    abilityWindupAnimation.restart()
    abilityCreature.isAttacking = true

    var attackVector = abilityCreature.attackVector
    val theta = new Vector2(attackVector.x, attackVector.y).angleDeg()

    if (attackVector.len() > 0f) {
      attackVector = new Vector2(
        attackVector.x / attackVector.len(),
        attackVector.y / attackVector.len()
      )
    }

    val attackShiftX = attackVector.x * attackRange
    val attackShiftY = attackVector.y * attackRange

    val attackRectX = attackShiftX + abilityCreature.posX
    val attackRectY = attackShiftY + abilityCreature.posY

    val poly = new CustomPolygon(new Rectangle(0, 0, width, height))

    poly.setOrigin(0, height / 2)
    poly.setRotation(theta)
    //poly.setPosition(attackRectX, attackRectY)
    poly.translate(0, -height / 2)
    poly.setScale(scale, scale)

    hitbox = util.AttackHitbox(attackRectX, attackRectY, poly)

  }

  override def update(): Unit = {
    super.update()

    if (body != null && toRemoveBody) {
      body.getWorld.destroyBody(body)
      toRemoveBody = false
      bodyActive = false
    }
  }

  override def updateHitbox(): Unit = {
    super.updateHitbox()

    if (hitbox != null) {
      var attackVector = abilityCreature.attackVector

      if (attackVector.len() > 0f) {
        attackVector = new Vector2(
          attackVector.x / attackVector.len(),
          attackVector.y / attackVector.len()
        )
      }

      val attackShiftX = attackVector.x * attackRange
      val attackShiftY = attackVector.y * attackRange

      hitbox.x = attackShiftX + abilityCreature.posX
      hitbox.y = attackShiftY + abilityCreature.posY

      if (bodyActive) {
        body.setTransform(
          hitbox.x / GameSystem.PixelsPerMeter,
          hitbox.y / GameSystem.PixelsPerMeter,
          0f
        )
      }
    }

  }

  override def onStop() {
    super.onStop()

    if (state == AbilityState.Active)
      toRemoveBody = true // IMPORTANT: ability has to be active
    // if we remove during channeling we could remove it before body is created, causing BOX2D crash

  }

  override def onCollideWithCreature(creature: Creature): Unit = {
    super.onCollideWithCreature(creature)
    if (!(abilityCreature.isMob && creature.isMob)) {
      if (
        abilityCreature != creature && state == AbilityState.Active && !creature.isImmune
      ) {
        creature.takeDamage(
          abilityCreature.weaponDamage,
          immunityFrames = true,
          30f
        )
      }
    }
  }
}
