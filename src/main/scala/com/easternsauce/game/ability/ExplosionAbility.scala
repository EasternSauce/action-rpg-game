package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, CircleShape, FixtureDef}
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class ExplosionAbility private (override val abilityCreature: Creature)
    extends Ability(abilityCreature) {

  override protected val isStoppable: Boolean = false
  override protected var channelTime: Float = 1.3f
  override protected var activeTime: Float = 0.9f
  override protected var cooldownTime: Float = 0.8f
  var body: Body = _
  var exploded = false
  protected var explosionAnimation =
    new EsAnimation(Assets.explosionSpriteSheet, 0.05f)
  protected var explosionRange: Float = 320f

  override def onChannellingStart(): Unit = {
    abilityCreature
      .getEffect("immobilized")
      .applyEffect(channelTime + activeTime)
    exploded = false
  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Active) {
      val image = explosionAnimation.currentFrame

      val scale = explosionRange * 2 / image.getRegionWidth

      val scaledWidth = image.getRegionWidth * scale
      val scaledHeight = image.getRegionHeight * scale

      batch.draw(
        image,
        abilityCreature.posX - scaledWidth / 2f,
        abilityCreature.posY - scaledHeight / 2f,
        0,
        0,
        image.getRegionWidth,
        image.getRegionHeight,
        scale,
        scale,
        0.0f
      )
    }
  }

  override def onStop() {
    super.onStop()

  }

  override def onCollideWithCreature(creature: Creature): Unit = {
    super.onCollideWithCreature(creature)
    if (!(this.abilityCreature.isMob && creature.isMob) && creature.isAlive) {
      if (!creature.isImmune) creature.takeDamage(700f, immunityFrames = true)
    }
  }

  override protected def onActiveStart(): Unit = {
    explosionAnimation.restart()
    abilityCreature.takeStaminaDamage(25f)
    abilityCreature.takeDamage(700f, immunityFrames = false)
    Assets.explosionSound.play(0.07f)

    initBody(abilityCreature.posX, abilityCreature.posY)
  }

  def initBody(x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position
      .set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)

    bodyDef.`type` = BodyDef.BodyType.StaticBody
    body = abilityCreature.area.world.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: CircleShape = new CircleShape()
    shape.setRadius(explosionRange / GameSystem.PixelsPerMeter)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
  }

  override protected def onUpdateActive(): Unit = {
    if (!exploded && activeTimer.time > 0.1f) {
      body.getWorld.destroyBody(body)
      exploded = true
    }
  }
}

object ExplosionAbility {
  def apply(creature: Creature) = new ExplosionAbility(creature)
}
