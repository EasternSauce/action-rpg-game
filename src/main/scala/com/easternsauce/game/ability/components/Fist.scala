package com.easternsauce.game.ability.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, CircleShape, FixtureDef}
import com.easternsauce.game.ability.{Ability, MeteorCrashAbility}
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.ability.util.AbilityState.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.utils.SimpleTimer
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class Fist(override val mainAbility: Ability, val startTime: Float, val posX: Float, val posY: Float, var radius: Float) extends AbilityComponent(mainAbility) {

  var activeTimer: SimpleTimer = SimpleTimer()
  var channelTimer: SimpleTimer = SimpleTimer()

  var activeTime = 0.2f
  var channelTime = 1.6f

  var state: AbilityState = AbilityState.Inactive

  var started = false

  var abilityAnimation: EsAnimation = new EsAnimation(Assets.fistSlamSpriteSheet, 0, 0.04f)
  var windupAnimation: EsAnimation =  new EsAnimation(Assets.fistSlamWindupSpriteSheet, 0, 0.28f)

  var scale = 2.5f

  var body: Body = _

  var destroyed: Boolean = false

  def start(): Unit = {
    started = true
    state = AbilityState.Channeling
    channelTimer.restart()
    windupAnimation.restart()
  }

  def initBody(x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)

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

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch) = {
    if (state == AbilityState.Channeling) {
      val image = windupAnimation.currentFrame

      val shift = image.getRegionWidth * scale / 2f
      batch.draw(image, posX - shift, posY - shift, 0, 0,
        image.getRegionWidth, image.getRegionHeight, scale, scale, 0.0f)
    }
    if (state == AbilityState.Active) {

      val image = abilityAnimation.currentFrame

      val shift = image.getRegionWidth * scale / 2f

      batch.draw(image, posX - shift, posY - shift, 0, 0,
        image.getRegionWidth, image.getRegionHeight, scale, scale, 0.0f)
    }
  }

  override def onCollideWithCreature(creature: Creature): Unit = {
    super.onCollideWithCreature(creature)

    if (!(mainAbility.abilityCreature.isMob && creature.isMob) && creature.isAlive && activeTimer.time < 0.15f) { // mob can't hurt a mob?
      if (!creature.isImmune) creature.takeDamage(50f, immunityFrames = true, 0, 0, 0)
    }
  }
}
