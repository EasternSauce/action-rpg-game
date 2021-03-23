package com.easternsauce.game.ability.attack

import com.badlogic.gdx.maps.tiled.TiledMap
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.projectile.Arrow
import com.easternsauce.game.shapes.CustomVector2

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class BowAttack(override val abilityCreature: Creature) extends Attack(abilityCreature) {
  override def init(): Unit = {
    cooldownTime = 1.5f
    activeTime = 0.3f
    channelTime = 0.5f
  }


  override def onActiveStart(): Unit = {
    super.onActiveStart()

    Assets.bowReleaseSound.play(0.1f)

    abilityCreature.attackVector = abilityCreature.facingVector.copy

    val arrowList: ListBuffer[Arrow] = abilityCreature.area.arrowList
    val tiles: TiledMap = abilityCreature.area.tiledMap
    val areaCreatures: mutable.Map[String, Creature] = abilityCreature.area.creatures

    if (!abilityCreature.facingVector.equals(new CustomVector2(0.0f, 0.0f))) {
      val arrowWidth = 40 //TODO: make global? or read from arrow texture?
      val arrowHeight = 40
      val arrowStartX = abilityCreature.posX + abilityCreature.spriteWidth / 2 - arrowWidth / 2
      val arrowStartY = abilityCreature.posY + abilityCreature.spriteHeight / 2 - arrowHeight / 2
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
