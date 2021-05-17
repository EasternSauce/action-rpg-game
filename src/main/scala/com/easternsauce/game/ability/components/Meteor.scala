package com.easternsauce.game.ability.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, CircleShape, FixtureDef}
import com.easternsauce.game.ability.Ability
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.ability.util.AbilityState.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class Meteor private (
    override val mainAbility: Ability,
    val startTime: Float,
    val posX: Float,
    val posY: Float,
    val radius: Float,
    speed: Float
) extends AbilityComponent(mainAbility) {

  override protected val activeTime: Float = 1.8f / speed
  override protected val channelTime: Float = 1.2f / speed
  override protected val abilityAnimation: EsAnimation =
    new EsAnimation(Assets.explosionSpriteSheet, 0.092f / speed)
  override protected val abilityWindupAnimation: EsAnimation =
    new EsAnimation(Assets.explosionWindupSpriteSheet, 0.2f / speed)

  override var state: AbilityState = AbilityState.Inactive
  override var started = false
  override var body: Body = _
  override var destroyed = false

  def start(): Unit = {
    started = true
    state = AbilityState.Channeling
    channelTimer.restart()
    abilityWindupAnimation.restart()
  }

  override def onUpdateActive(): Unit = {
    if (started) {
      if (state == AbilityState.Channeling)
        if (channelTimer.time > channelTime) {
          onActiveStart()
        }
      if (state == AbilityState.Active) {
        if (!destroyed && activeTimer.time >= 0.2f) {
          body.getWorld.destroyBody(body)
          destroyed = true
        }
        if (activeTimer.time > activeTime) {
          // on active stop
          state = AbilityState.Inactive
        }
      }
    }

  }

  private def onActiveStart(): Unit = {
    state = AbilityState.Active
    Assets.explosionSound.play(0.01f)
    abilityAnimation.restart()
    activeTimer.restart()
    initBody(posX, posY)
  }

  def initBody(x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position
      .set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)

    bodyDef.`type` = BodyDef.BodyType.StaticBody
    body = mainAbility.abilityCreature.area.world.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: CircleShape = new CircleShape()
    shape.setRadius(radius / GameSystem.PixelsPerMeter)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Channeling) {
      val spriteWidth = 64
      val scale = radius * 2 / spriteWidth
      val image = abilityWindupAnimation.currentFrame
      batch.draw(
        image,
        posX - radius,
        posY - radius,
        0,
        0,
        image.getRegionWidth,
        image.getRegionHeight,
        scale,
        scale,
        0.0f
      )
    }
    if (state == AbilityState.Active) {
      val spriteWidth = 64
      val scale = radius * 2 / spriteWidth
      val image = abilityAnimation.currentFrame
      batch.draw(
        image,
        posX - radius,
        posY - radius,
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

  override def onCollideWithCreature(creature: Creature): Unit = {
    if (
      !(mainAbility.abilityCreature.isMob && creature.isMob) && creature.isAlive
    ) {
      if (!creature.isImmune) creature.takeDamage(40f, immunityFrames = true)
    }
  }

}

object Meteor {
  def apply(
      mainAbility: Ability,
      startTime: Float,
      posX: Float,
      posY: Float,
      radius: Float,
      speed: Float
  ): Meteor = {
    new Meteor(mainAbility, startTime, posX, posY, radius, speed)
  }
}
