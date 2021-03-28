package com.easternsauce.game.ability

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.{Body, BodyDef, CircleShape, FixtureDef, PolygonShape}
import com.easternsauce.game.ability.attack.AttackHitbox
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.mob.Mob
import com.easternsauce.game.wrappers.EsAnimation
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class ExplodeAbility(override val abilityCreature: Creature) extends Ability(abilityCreature) {

  protected var explosionAnimation = new EsAnimation(Assets.explosionSpriteSheet, 0, 0.05f)
  protected var explosionRange: Float = _

  override protected val isStoppable: Boolean = false

  var body: Body = _

  var exploded = false

  override def init(): Unit = {
    cooldownTime = 0.8f
    activeTime = 0.9f
    channelTime = 1.3f
    explosionRange = 150f

  }

  override protected def onActiveStart(): Unit = {
    explosionAnimation.restart()
    abilityCreature.takeStaminaDamage(25f)
    abilityCreature.takeDamage(700f, immunityFrames = false, 0, 0, 0)
    Assets.explosionSound.play(0.07f)

    initBody(abilityCreature.posX, abilityCreature.posY)
  }

  override protected def onUpdateActive(): Unit = {
    if (!exploded && activeTimer.time > 0.1f) {
      body.getWorld.destroyBody(body)
      exploded = true
    }
  }

  override def onChannellingStart(): Unit = {
    abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime)
    exploded = false
  }

  override def render(shapeDrawer: ShapeDrawer, batch: SpriteBatch): Unit = {
    if (state == AbilityState.Active) {
      val image = explosionAnimation.currentFrame

      val scale = explosionRange * 2 / image.getRegionWidth

      val scaledWidth = image.getRegionWidth * scale
      val scaledHeight = image.getRegionHeight * scale

      batch.draw(image, abilityCreature.posX - scaledWidth / 2f, abilityCreature.posY - scaledHeight / 2f, 0,0,
        image.getRegionWidth, image.getRegionHeight, scale, scale, 0.0f)
    }
  }

  def initBody(x: Float, y: Float): Unit = {
    val bodyDef = new BodyDef()
    bodyDef.position.set(x / GameSystem.PixelsPerMeter, y / GameSystem.PixelsPerMeter)

    bodyDef.`type` = BodyDef.BodyType.StaticBody
    body = abilityCreature.area.world.createBody(bodyDef)
    body.setUserData(this)

    val fixtureDef: FixtureDef = new FixtureDef()
    val shape: CircleShape = new CircleShape()
    shape.setRadius(explosionRange / GameSystem.PixelsPerMeter)
    fixtureDef.shape = shape
    fixtureDef.isSensor = true
    body.createFixture(fixtureDef)
    //body.setLinearDamping(10f)
  }

  override def onStop() {
    super.onStop()

  }

  override def onCollideWithCreature(creature: Creature): Unit = {
    if (!(this.abilityCreature.isMob && creature.isMob) && creature.alive) { // mob can't hurt a mob?
      if (!creature.isImmune) creature.takeDamage(700f, immunityFrames = true, 0, 0, 0)
    }
  }
}

object ExplodeAbility {
  def apply(creature: Creature): ExplodeAbility = {
    val ability = new ExplodeAbility(creature)
    ability.init()
    ability
  }

}