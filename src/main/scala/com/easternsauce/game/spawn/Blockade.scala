package com.easternsauce.game.spawn

class Blockade private (mobSpawnPoint: MobSpawnPoint, blockadePosX: Int, blockadePosY: Int) {

  var active = false

  def render(): Unit = {}

}

object Blockade {
  def apply(mobSpawnPoint: MobSpawnPoint, blockadePosX: Int, blockadePosY: Int) =
    new Blockade(mobSpawnPoint, blockadePosX, blockadePosY)
}
