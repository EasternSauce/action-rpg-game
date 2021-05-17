package com.easternsauce.game.spawn

class SpawnLocation private (
  val spawnType: String,
  val creatureType: String,
  val posX: Int,
  val posY: Int,
  val blockadePosX: Int,
  val blockadePosY: Int
) {
  var hasBlockade: Boolean = false

  if (
    blockadePosX != null.asInstanceOf[Int] && blockadePosY != null
      .asInstanceOf[Int]
  ) {
    hasBlockade = true
  }

}

object SpawnLocation {
  def apply(
    spawnType: String,
    creatureType: String,
    posX: Int,
    posY: Int,
    blockadePosX: Int = null.asInstanceOf[Int],
    blockadePosY: Int = null.asInstanceOf[Int]
  ) = new SpawnLocation(spawnType, creatureType, posX, posY, blockadePosX, blockadePosY)
}
