package com.easternsauce.game.spawn

class SpawnLocation(val spawnType: String, val creatureType: String, val posX: Int, val posY: Int, val blockadePosX: Int = null.asInstanceOf[Int], val blockadePosY: Int = null.asInstanceOf[Int]) {
  var hasBlockade: Boolean = false

  if (blockadePosX != null.asInstanceOf[Int] && blockadePosY != null.asInstanceOf[Int]) hasBlockade = true

}
