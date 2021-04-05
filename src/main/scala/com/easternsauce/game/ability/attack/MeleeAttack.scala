package com.easternsauce.game.ability.attack

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, FixtureDef, PolygonShape}
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.CustomPolygon
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

import scala.language.implicitConversions

abstract class MeleeAttack(override val abilityCreature: Creature) extends Attack(abilityCreature) {
  protected var activeAnimation: EsAnimation = _
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

  var toRemoveBody = false

  implicit def rectConversion(s: com.badlogic.gdx.math.Rectangle): Rectangle = new Rectangle(s.x, s.y, s.width, s.height)

  override def onActiveStart(): Unit = {
    super.onActiveStart()

    activeAnimation.restart()

    abilityCreature.takeStaminaDamage(15f)

    Assets.attackSound.play(0.1f)

    var attackVector = abilityCreature.attackVector
    val theta = new Vector2(attackVector.x, attackVector.y).angleDeg()

    if (attackVector.len() > 0f) {
      attackVector = new Vector2(attackVector.x / attackVector.len(), attackVector.y / attackVector.len())
    }

    val attackShiftX = attackVector.x * attackRange
    val attackShiftY = attackVector.y * attackRange

    val attackRectX = attackShiftX + abilityCreature.posX
    val attackRectY = attackShiftY + abilityCreature.posY

    val poly = new CustomPolygon(new Rectangle(0,0, width, height))

    poly.setOrigin(0, height / 2)
    poly.setRotation(theta)
    //poly.setPosition(attackRectX, attackRectY)
    poly.translate(0, -height / 2)
    poly.setScale(scale, scale)

    hitbox = AttackHitbox(attackRectX, attackRectY, poly)

    initBody(hitbox)

    toRemoveBody = false
  }

  override def onUpdateActive(): Unit = {
    super.onUpdateActive()

  }


  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    super.render(shapeDrawer, batch)

    if (state == AbilityState.Channeling) {
      val image = windupAnimation.currentFrame

      val attackVector = abilityCreature.attackVector
      val theta = new Vector2(attackVector.x, attackVector.y).angleDeg()

      batch.draw(image, hitbox.x, hitbox.y - height / 2, 0, height / 2,
        image.getRegionWidth, image.getRegionHeight, scale, scale, theta)
    }
    if (state == AbilityState.Active) {
      val image = activeAnimation.currentFrame

      val attackVector = abilityCreature.attackVector
      val theta = new Vector2(attackVector.x, attackVector.y).angleDeg()

      batch.draw(image, hitbox.x, hitbox.y - height / 2, 0, height / 2,
        image.getRegionWidth, image.getRegionHeight, scale, scale, theta)
    }
  }

  override def onChannellingStart(): Unit = {
    abilityCreature.attackVector = abilityCreature.facingVector
    windupAnimation.restart()
    abilityCreature.isAttacking = true

    var attackVector = abilityCreature.attackVector
    val theta = new Vector2(attackVector.x, attackVector.y).angleDeg()

    if (attackVector.len() > 0f) {
      attackVector = new Vector2(attackVector.x / attackVector.len(), attackVector.y / attackVector.len())
    }

    val attackShiftX = attackVector.x * attackRange
    val attackShiftY = attackVector.y * attackRange

    val attackRectX = attackShiftX + abilityCreature.posX
    val attackRectY = attackShiftY + abilityCreature.posY

    val poly = new CustomPolygon(new Rectangle(0,0, width, height))

    poly.setOrigin(0, height / 2)
    poly.setRotation(theta)
    //poly.setPosition(attackRectX, attackRectY)
    poly.translate(0, -height / 2)
    poly.setScale(scale, scale)

    hitbox = AttackHitbox(attackRectX, attackRectY, poly)

  }


  override def update(): Unit = {
    super.update()

    if (body != null && toRemoveBody) {
      body.getWorld.destroyBody(body)
      toRemoveBody = false
    }
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
    shape.set(converted)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
  }

  override def onUpdateHitbox(): Unit = {
    super.onUpdateHitbox()

    if (hitbox != null) {
      var attackVector = abilityCreature.attackVector

      if (attackVector.len() > 0f) {
        attackVector = new Vector2(attackVector.x / attackVector.len(), attackVector.y / attackVector.len())
      }

      val attackShiftX = attackVector.x * attackRange
      val attackShiftY = attackVector.y * attackRange

      hitbox.x = attackShiftX + abilityCreature.posX
      hitbox.y = attackShiftY + abilityCreature.posY

      if (body != null) {
        body.setTransform(hitbox.x / GameSystem.PixelsPerMeter, hitbox.y / GameSystem.PixelsPerMeter, 0f)
      }
    }

  }

  override def onStop() {
    super.onStop()

    if (state == AbilityState.Active) toRemoveBody = true // IMPORTANT: ability has to be active
    // if we remove during channeling we could remove it before body is created, causing BOX2D crash

  }

  override def onCollideWithCreature(creature: Creature): Unit = {
    if (!(abilityCreature.isMob && creature.isMob)) {
      if (abilityCreature != creature && state == AbilityState.Active) {
        creature.takeDamage(abilityCreature.weaponDamage, immunityFrames = true, 30f, 0f, 0f)
      }
    }
  }
}

