package com.easternsauce.game.spawn

import com.easternsauce.game.area.Area
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.mob.boss.FireDemon
import com.easternsauce.game.creature.mob.{Ghost, Goblin, Skeleton, Wolf}
import system.GameSystem

class MobSpawnPoint(val posX: Int, val posY: Int, val area: Area, val creatureType: String) {

  var blockade: Blockade = _
  private var isToBeRespawned: Boolean = false
  private var spawnedCreature: Creature = _

  markForRespawn()

  def update(): Unit = {
    if (isToBeRespawned) {

      if (creatureType == "skeletonSword") {
        spawnedCreature = new Skeleton("skellie" + Math.abs(GameSystem.random.nextInt), this, "woodenSword")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "skeletonCrossbow") {
        spawnedCreature = new Skeleton("skellie" + Math.abs(GameSystem.random.nextInt), this, "crossbow")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "wolf") {
        spawnedCreature = new Wolf("wolfie" + Math.abs(GameSystem.random.nextInt), this)
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "ghost") {
        spawnedCreature = new Ghost("ghost" + Math.abs(GameSystem.random.nextInt), this, "woodenSword")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "goblin") {
        spawnedCreature = new Goblin("goblin" + Math.abs(GameSystem.random.nextInt), this, "poisonDagger")
        area.moveInCreature(spawnedCreature, posX, posY)
      }
      if (creatureType == "fireDemon") {
        spawnedCreature = new FireDemon("firedemon" + Math.abs(GameSystem.random.nextInt), this, "demonTrident")
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
    //if (spawnedCreature != null) spawnedCreature.kill()
    isToBeRespawned = true
  }

}
