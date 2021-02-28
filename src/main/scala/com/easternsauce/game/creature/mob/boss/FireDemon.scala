package com.easternsauce.game.creature.mob.boss

import com.easternsauce.game.creature.mob.Mob
import com.easternsauce.game.spawn.MobSpawnPoint

class FireDemon(override val id: String, override val mobSpawnPoint: MobSpawnPoint, val weaponType: String) extends Mob(id, mobSpawnPoint) {

}
