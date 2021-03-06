package com.easternsauce.game.creature.mob

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.easternsauce.game.ability.DashAbility
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.spawn.MobSpawnPoint
import com.easternsauce.game.utils.EsTimer
import system.GameSystem

class Wolf private (override val id: String, override val mobSpawnPoint: MobSpawnPoint) extends Mob(id, mobSpawnPoint) {

  override val baseSpeed: Float = 10f

  override val mass = 300f
  override val scale = 1.65f
  override protected val onGettingHitSound: Sound = Assets.dogWhineSound

  actionTimer = EsTimer(true)

  dropTable.put("ringmailGreaves", 0.1f)
  dropTable.put("leatherArmor", 0.05f)
  dropTable.put("hideGloves", 0.1f)
  dropTable.put("leatherHelmet", 0.1f)
  dropTable.put("healingPowder", 0.5f)

  loadSprites(Assets.wolfSpriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 0)
  private var dashAbility: DashAbility = _

  override def hitbox: Rectangle = new Rectangle(17, 15, 30, 46)

  maxHealthPoints = 150f
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

    if (hasDestination)
      if (dashAbility.canPerform && GameSystem.distance(aggroedCreature.get.body, body) < dashDistance) {
        dashAbility.setDashVector(new Vector2(destinationX - posX, destinationY - posY).nor())
        dashAbility.perform()
      }
  }
}

object Wolf {
  def apply(id: String, mobSpawnPoint: MobSpawnPoint) =
    new Wolf(id, mobSpawnPoint)
}
