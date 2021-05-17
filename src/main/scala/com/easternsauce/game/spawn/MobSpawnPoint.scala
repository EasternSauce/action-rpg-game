package com.easternsauce.game.spawn

import com.easternsauce.game.area.Area
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.mob.boss.FireDemon
import com.easternsauce.game.creature.mob.{Ghost, Goblin, Skeleton, Wolf}
import system.GameSystem

class MobSpawnPoint private (val posX: Int, val posY: Int, val area: Area, val creatureType: String) {

  var blockade: Blockade = _
  private var isToBeRespawned: Boolean = false
  private var spawnedCreature: Creature = _

  markForRespawn()

  def update(): Unit = {
    if (isToBeRespawned) {

      if (creatureType == "skeletonSword") {
        spawnedCreature = Skeleton("skellie" + Math.abs(GameSystem.random.nextInt), this, "woodenSword")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "skeletonCrossbow") {
        spawnedCreature = Skeleton("skellie" + Math.abs(GameSystem.random.nextInt), this, "crossbow")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "wolf") {
        spawnedCreature = Wolf("wolfie" + Math.abs(GameSystem.random.nextInt), this)
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "ghost") {
        spawnedCreature = Ghost("ghost" + Math.abs(GameSystem.random.nextInt), this, "woodenSword")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "goblin") {
        spawnedCreature = Goblin("goblin" + Math.abs(GameSystem.random.nextInt), this, "poisonDagger")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "fireDemon") {
        spawnedCreature = FireDemon("firedemon" + Math.abs(GameSystem.random.nextInt), this, "demonTrident")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      spawnedCreature.startingPosX = posX
      spawnedCreature.startingPosY = posY
      spawnedCreature.moveToArea(area, posX, posY)
      spawnedCreature.onInit()

      isToBeRespawned = false
    }
  }

  def markForRespawn(): Unit = {
    isToBeRespawned = true
  }

}

object MobSpawnPoint {
  def apply(posX: Int, posY: Int, area: Area, creatureType: String) = new MobSpawnPoint(posX, posY, area, creatureType)
}
