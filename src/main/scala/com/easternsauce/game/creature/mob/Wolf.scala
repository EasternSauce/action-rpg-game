package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Rectangle
import com.easternsauce.game.ability.DashAbility
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.CustomVector2
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.SimpleTimer
import system.GameSystem

class Wolf(override val id: String, override val mobSpawnPoint: MobSpawnPoint) extends Mob(id, mobSpawnPoint) {

  override val baseSpeed: Float = 200f

  private var dashAbility: DashAbility = null

  actionTimer = SimpleTimer(true)

  dropTable.put("ringmailGreaves", 0.1f)
  dropTable.put("leatherArmor", 0.05f)
  dropTable.put("hideGloves", 0.1f)
  dropTable.put("leatherHelmet", 0.1f)
  dropTable.put("healingPowder", 0.5f)

  loadSprites(Assets.wolfSpriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 0)

  override def hitbox: Rectangle = new Rectangle(17, 15, 30, 46)

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

    if (hasDestination) if (dashAbility.canPerform && GameSystem.distance(aggroedCreature.get.body, body) < dashDistance) {
      dashAbility.setDashVector(new CustomVector2(destinationX - posX, destinationY - posY).normal)
      dashAbility.perform()
    }
  }
}
