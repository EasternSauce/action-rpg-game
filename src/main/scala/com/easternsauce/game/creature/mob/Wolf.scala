package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.ability.DashAbility
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.{CustomRectangle, CustomVector2}
import com.easternsauce.game.utils.Timer
import system.GameSystem

class Wolf(id: String) extends Mob(id) {

  override val baseSpeed: Float = 200f

  private var dashAbility: DashAbility = null

  actionTimer = Timer(true)

  dropTable.put("ringmailGreaves", 0.1f)
  dropTable.put("leatherArmor", 0.05f)
  dropTable.put("hideGloves", 0.1f)
  dropTable.put("leatherHelmet", 0.1f)
  dropTable.put("healingPowder", 0.5f)

  loadSprites(Assets.wolfSpriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 0)

  override def hitbox: CustomRectangle = new CustomRectangle(17, 15, 30, 46)

  override protected val onGettingHitSound: Sound = Assets.dogWhineSound


  maxHealthPoints = 10f
  healthPoints = maxHealthPoints

  unarmedDamage = 30f

  creatureType = "wolf"



  override def onInit(): Unit = {
    super.onInit()

    dashAbility = DashAbility(this)

    dashAbility.onPerformAction = () => {
      Assets.dogBarkSound.play(0.1f)
    }

    abilityList += dashAbility

  }

  override def performAggroedBehavior(): Unit = {
    super.performAggroedBehavior()

    val dashDistance = 250f

    assert(aggroedCreature.nonEmpty)

    if (hasDestination) if (dashAbility.canPerform && GameSystem.distance(aggroedCreature.get.rect, rect) < dashDistance) {
      dashAbility.setDashVector(new CustomVector2(destinationX - rect.getX, destinationY - rect.getY).normal)
      dashAbility.perform()
    }
  }
}
