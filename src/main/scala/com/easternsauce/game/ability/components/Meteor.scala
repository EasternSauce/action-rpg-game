package com.easternsauce.game.ability.components

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, CircleShape, FixtureDef}
import com.easternsauce.game.ability.MeteorRainAbility
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.ability.util.AbilityState.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.utils.SimpleTimer
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class Meteor(override val mainAbility: MeteorRainAbility, val startTime: Float, val posX: Float, val posY: Float, val explosionRange: Float, speed: Float) extends AbilityComponent(mainAbility) {

  var activeTimer: SimpleTimer = SimpleTimer()
  var channelTimer: SimpleTimer = SimpleTimer()

  var activeTime: Float = 1.8f / speed
  var channelTime: Float = 1.2f / speed

  var state: AbilityState = AbilityState.Inactive

  var started = false

  var body: Body = _

  var destroyed = false

  var explosionAnimation: EsAnimation = new EsAnimation(Assets.explosionSpriteSheet, 0, 0.092f / speed)
  var explosionWindupAnimation: EsAnimation = new EsAnimation(Assets.explosionWindupSpriteSheet, 0, 0.2f / speed)

  def start(): Unit = {
    started = true
    state = AbilityState.Channeling
    channelTimer.restart()
    explosionWindupAnimation.restart()
  }

  def initBody(x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)

    bodyDef.`type` = BodyDef.BodyType.StaticBody
    body = mainAbility.abilityCreature.area.world.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: CircleShape = new CircleShape()
    shape.setRadius(explosionRange / GameSystem.PixelsPerMeter)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
    //body.setLinearDamping(10f)
  }

  override def onUpdateActive(): Unit = {
    if (started) {
      if (state == AbilityState.Channeling) if (channelTimer.time > channelTime) {
        //on active start
        state = AbilityState.Active
        Assets.explosionSound.play(0.01f)
        explosionAnimation.restart()
        activeTimer.restart()
        initBody(posX, posY)
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

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Channeling) {
      val spriteWidth = 64
      val scale = explosionRange * 2 / spriteWidth
      val image = explosionWindupAnimation.currentFrame
      batch.draw(image, posX - explosionRange, posY - explosionRange, 0, 0,
        image.getRegionWidth, image.getRegionHeight, scale, scale, 0.0f)
    }
    if (state == AbilityState.Active) {
      val spriteWidth = 64
      val scale = explosionRange * 2 / spriteWidth
      val image = explosionAnimation.currentFrame
      batch.draw(image, posX - explosionRange, posY - explosionRange, 0, 0,
        image.getRegionWidth, image.getRegionHeight, scale, scale, 0.0f)
    }
  }

  override def onCollideWithCreature(creature: Creature): Unit = {
    super.onCollideWithCreature(creature)

    if (!(mainAbility.abilityCreature.isMob && creature.isMob) && creature.isAlive) { // mob can't hurt a mob?
      if (!creature.isImmune) creature.takeDamage(50f, immunityFrames = true, 0, 0, 0)
    }
  }

}
