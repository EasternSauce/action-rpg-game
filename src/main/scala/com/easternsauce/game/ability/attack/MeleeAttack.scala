package com.easternsauce.game.ability.attack

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.util.AbilityState
import com.easternsauce.game.animation.Animation
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.{CustomPolygon, CustomRectangle, CustomVector2}
import system.GameSystem

abstract class MeleeAttack(override protected val abilityCreature: Creature) extends Attack(abilityCreature) {
  protected var attackAnimation: Animation = _
  protected var windupAnimation: Animation = _
  //protected val weaponSound: Sound = Assets.attackSound
  protected var aimed = false

  protected var width: Float = _
  protected var height: Float = _
  protected var scale = 1.0f
  protected var attackRange: Float = _

  protected var knockbackPower = 0f

  implicit def rectConversion(s: com.badlogic.gdx.math.Rectangle): CustomRectangle = new CustomRectangle(s.x, s.y, s.width, s.height)

  override def onActiveStart(): Unit = {
    super.onActiveStart()

    attackAnimation.restart()

    abilityCreature.takeStaminaDamage(15f)

    Assets.attackSound.play(0.1f)
  }

  override def onUpdateActive(): Unit = {
    super.onUpdateActive()

//    updateAttackRect()

    val creatures = abilityCreature.area.creatures.values
    for (creature <- creatures) {
      if (creature != this.abilityCreature) {
        if (GameSystem.checkCollision(meleeAttackHitbox, creature.rect)) if (!(this.abilityCreature.isMob && creature.isMob)) { // mob can't hurt a mob?
          if (!creature.isImmune) {
            //val weapon: Item = this.abilityCreature.getEquipmentItems.get(0)
            val center = abilityCreature.rect.center
            creature.takeDamage(10f, immunityFrames = true, knockbackPower, center.x, center.y)
            //abilityCreature.onAttack()
            //val random: Int = Globals.random.nextInt(100)
            //if (random < weapon.getItemType.getPoisonChance * 100f) creature.becomePoisoned()
          }

        }
      }
    }
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

  override def render(batch: SpriteBatch): Unit = {
    super.render(batch)

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
      val image = windupAnimation.currentFrame()
      var attackVector = abilityCreature.attackVector
      val theta = new CustomVector2(attackVector.x, attackVector.y).angleDeg()

      if (attackVector.len() > 0f) {
        attackVector = CustomVector2(attackVector.x / attackVector.len(), attackVector.y / attackVector.len())
      }

      val attackShiftX = attackVector.x * attackRange
      val attackShiftY = attackVector.y * attackRange

      val attackRectX = attackShiftX + abilityCreature.rect.center.x
      val attackRectY = attackShiftY + abilityCreature.rect.center.y

      image.setOrigin(0, height / 2 * scale)
      image.setRotation(theta)
      image.setPosition(attackRectX, attackRectY)
      image.translate(0, -height / 2  * scale)

      // ----> useful testing rect <----
//      val testX = attackRectX
//      val testY = attackRectY
//
//      batch.drawRect(new CustomRectangle(testX - 3, testY - 3, 6, 6), Color.CYAN)

      image.draw(batch)
    }
    if (state == AbilityState.Active) {
      val image = attackAnimation.currentFrame()
      var attackVector = abilityCreature.attackVector
      val theta = CustomVector2(attackVector.x, attackVector.y).angleDeg()

      if (attackVector.len() > 0f) {
        attackVector = CustomVector2(attackVector.x / attackVector.len(), attackVector.y / attackVector.len())
      }

      val attackShiftX = attackVector.x * attackRange
      val attackShiftY = attackVector.y * attackRange

      val attackRectX = attackShiftX + abilityCreature.rect.center.x
      val attackRectY = attackShiftY + abilityCreature.rect.center.y

      val poly = new CustomPolygon(new CustomRectangle(0,0, width, height))

      poly.setOrigin(0, height / 2 * scale)
      poly.setRotation(theta)
      poly.setPosition(attackRectX, attackRectY)
      poly.translate(0, -height / 2  * scale)

      meleeAttackHitbox = poly

      image.setOrigin(0, height / 2 * scale)
      image.setRotation(theta)
      image.setPosition(attackRectX, attackRectY)
      image.translate(0, -height / 2  * scale)

      image.draw(batch)
    }
  }

  override def onChannellingStart(): Unit = {
    abilityCreature.attackVector = abilityCreature.facingVector
    windupAnimation.restart()
    abilityCreature.isAttacking = true
  }


  override def update(): Unit = {
    super.update()

  }
}
