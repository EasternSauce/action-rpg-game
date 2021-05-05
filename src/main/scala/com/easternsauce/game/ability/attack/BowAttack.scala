package com.easternsauce.game.ability.attack

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.projectile.Arrow
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class BowAttack(override val abilityCreature: Creature) extends Attack(abilityCreature) {
  override def init(): Unit = {
    cooldownTime = 1.5f
    activeTime = 0.3f
    channelTime = 0.5f
  }

  override def onChannellingStart(): Unit = {
    super.onChannellingStart()

    abilityCreature.isAttacking = true

    Assets.bowPullSound.play(0.1f)
  }

  override def onActiveStart(): Unit = {
    super.onActiveStart()

    Assets.bowReleaseSound.play(0.1f)

    abilityCreature.attackVector = abilityCreature.facingVector.cpy()

    val arrowList: ListBuffer[Arrow] = abilityCreature.area.arrowList
    val tiles: TiledMap = abilityCreature.area.tiledMap
    val areaCreatures: mutable.Map[String, Creature] = abilityCreature.area.creatures

    if (!abilityCreature.facingVector.equals(new Vector2(0.0f, 0.0f))) {
      val arrowStartX = abilityCreature.posX
      val arrowStartY = abilityCreature.posY
      val arrow: Arrow = new Arrow(arrowStartX, arrowStartY, abilityCreature.area, abilityCreature.facingVector, arrowList, tiles, areaCreatures, this.abilityCreature)
      arrowList += arrow
    }

    abilityCreature.takeStaminaDamage(20f)
  }
}

object BowAttack {
  def apply(creature: Creature): BowAttack = {
    val attack = new BowAttack(creature)
    attack.init()
    attack
  }
}
