package com.easternsauce.game.creature

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.ability.attack.{BowAttack, SwordAttack, TridentAttack, UnarmedAttack}
import com.easternsauce.game.area.{Area, AreaGate}
import com.easternsauce.game.creature.traits.{CreatureAbilities, CreatureTimers, WalkingEntity}
import com.easternsauce.game.effect.Effect
import com.easternsauce.game.item.Item
import system.GameSystem

import scala.collection.mutable.ListBuffer

abstract class Creature(val id: String) extends WalkingEntity with CreatureTimers with CreatureAbilities with Ordered[Creature] {

  def setFacingDirection(): Unit = {

  }

  def update(): Unit = {
    if (alive) {
      onUpdateStart()

      performActions()

      controlMovement()
      processMovement()

      setFacingDirection()
    }

    for (effect <- effectMap.values) {
      effect.update()
    }

    for (ability <- abilityList) {
      ability.update()
    }

    currentAttack.update()


    if (GameSystem.cameraFocussedCreature.nonEmpty
      && this == GameSystem.cameraFocussedCreature.get) {
      GameSystem.adjustCamera(rect)
    }
  }

  def render(batch: SpriteBatch): Unit = {
    drawRunningAnimation(batch)

    abilityList.foreach(ability => ability.renderSprites(batch))
    currentAttack.renderSprites(batch)
  }

  def performActions(): Unit


  def takeDamage(damage: Float, immunityFrames: Boolean, knockbackPower: Float, x: Float, y: Float): Unit = {
    healthPoints -= damage
    // TODO

    if (alive) {
      val beforeHP = healthPoints

      val actualDamage = damage * 100f / (100f + totalArmor)

      if (healthPoints - actualDamage > 0) healthPoints -= actualDamage
      else healthPoints = 0f

      if (beforeHP != healthPoints && healthPoints == 0f) onDeath()

      if (immunityFrames) { // immunity frames on hit
        getEffect("immune").applyEffect(0.75f)
        // stagger on hit
        getEffect("immobilized").applyEffect(0.35f)
      }

      onGettingHitSound.play(0.1f)
    }
  }

  def renderAbilities(batch: SpriteBatch): Unit = {
    for (ability <- abilityList) {
      ability.renderSprites(batch)
    }
    currentAttack.renderSprites(batch)
  }

  def defineStandardAbilities(): Unit = {
    abilityList = ListBuffer()
    attackList = ListBuffer()

    bowAttack = BowAttack(this)
    unarmedAttack = UnarmedAttack(this)
    swordAttack = SwordAttack(this)
    tridentAttack = TridentAttack(this)

    attackList += bowAttack
    attackList += unarmedAttack
    attackList += swordAttack
    attackList += tridentAttack

    currentAttack = swordAttack
  }

  def onInit(): Unit = {
    defineEffects()

    defineStandardAbilities()

    defineCustomAbilities()

    updateAttackType()
  }

  protected def defineCustomAbilities(): Unit = {
  }

  protected def defineEffects(): Unit = {
    effectMap.put("immune", new Effect(this))
    effectMap.put("immobilized", new Effect(this))
    effectMap.put("staminaRegenStopped", new Effect(this))
    effectMap.put("poisoned", new Effect(this))

  }

  def updateAttackType(): Unit = {

  }

  def onPassedGate(gatesList: ListBuffer[AreaGate]): Unit = {
    // TODO
  }

  def regenerate(): Unit = {
    // TODO
  }

  def abilityActive: Boolean = {
    // TODO
    false
  }

  def heal(healValue: Float): Unit = {
    // TODO
  }

  def becomePoisoned(): Unit = {
    // TODO
  }

  def totalArmor: Float = {
    // TODO
    0f
  }

  def onDeath(): Unit = {
    isRunningAnimationActive = false
  }

  def kill: Unit = {
    // TODO
  }

  def moveToArea(area: Area, posX: Float, posY: Float): Unit = {
    // TODO

  }

  def takeStaminaDamage(staminaDamage: Float): Unit = {
    // TODO

  }

  def useItem(item: Item): Unit = {
    // TODO
  }

  private def startHealing(healingPower: Float): Unit = {
    // TODO
  }

  def reset(): Unit = {
    // TODO
  }

  def onAttack(): Unit = {
    // TODO
  }

  def isNoAbilityActive: Boolean = {
    // TODO
    false
  }

  def onAggroed(): Unit = {
    // TODO
  }

  def getEffect(effectName: String): Effect = {
    effectMap.get(effectName) match {
      case Some(effect) => effect
      case _ => throw new RuntimeException("tried to access non-existing effect: " + effectName)
    }
  }

  def isEffectActive(effectName: String): Boolean = {
    effectMap.get(effectName) match {
      case Some(effect) => effect.isActive
      case _ => throw new RuntimeException("tried to access non-existing effect: " + effectName)
    }
  }

  def isImmune: Boolean = isEffectActive("immune")

  def compare(other: Creature): Int = {
    if (healthPoints <= 0.0f) {
      return 1
    }
    if (other.healthPoints <= 0.0f) {
      return -1
    }
    if (rect.y == other.rect.y) {
      return 0
    }
    if (rect.getY - other.rect.getY > 0.0f) 1 else -1

  }
}
