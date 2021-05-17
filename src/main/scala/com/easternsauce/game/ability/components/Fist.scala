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

class Fist private (
    override val mainAbility: Ability,
    val startTime: Float,
    val posX: Float,
    val posY: Float,
    var radius: Float
) extends AbilityComponent(mainAbility) {

  override protected val activeTime: Float = 0.2f
  override protected val channelTime: Float = 0.4f
  override val abilityAnimation: EsAnimation =
    new EsAnimation(Assets.fistSlamSpriteSheet, 0.04f)
  override val abilityWindupAnimation: EsAnimation =
    new EsAnimation(Assets.fistSlamWindupSpriteSheet, 0.08f)
  val scale: Float = 3f
  override var state: AbilityState = AbilityState.Inactive
  override var started: Boolean = false
  override var body: Body = _
  override var destroyed: Boolean = false

  def start(): Unit = {
    started = true
    state = AbilityState.Channeling
    channelTimer.restart()
    abilityWindupAnimation.restart()
  }

  override def onUpdateActive(): Unit = {
    if (started) {
      if (state == AbilityState.Channeling) {
        if (channelTimer.time > channelTime) {
          state = AbilityState.Active
          Assets.glassBreakSound.play(0.1f)
          abilityAnimation.restart()
          activeTimer.restart()
          initBody(posX, posY)
        }
      }
      if (state == AbilityState.Active) {
        if (activeTimer.time > activeTime) {
          state = AbilityState.Inactive
        }
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

  def initBody(x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position
      .set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)

    bodyDef.`type` = BodyDef.BodyType.StaticBody
    body = mainAbility.abilityCreature.area.world.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: CircleShape = new CircleShape()
    shape.setRadius(radius * scale / GameSystem.PixelsPerMeter)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Channeling) {
      val image = abilityWindupAnimation.currentFrame

      val shift = image.getRegionWidth * scale / 2f
      batch.draw(
        image,
        posX - shift,
        posY - shift,
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

      val image = abilityAnimation.currentFrame

      val shift = image.getRegionWidth * scale / 2f

      batch.draw(
        image,
        posX - shift,
        posY - shift,
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
      !(mainAbility.abilityCreature.isMob && creature.isMob) && creature.isAlive && activeTimer.time < 0.15f
    ) {
      if (!creature.isImmune) creature.takeDamage(50f, immunityFrames = true)
    }
  }
}

object Fist {
  def apply(
      mainAbility: Ability,
      startTime: Float,
      posX: Float,
      posY: Float,
      radius: Float
  ): Fist = {
    new Fist(mainAbility, startTime, posX, posY, radius)
  }
}
