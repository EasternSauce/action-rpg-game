package com.easternsauce.game.spawn

import scala.collection.mutable.ListBuffer
import scala.io.Source

class SpawnLocationsContainer private (enemyFileLocation: String) {
  private val fileContents = Source.fromFile(enemyFileLocation)
  var spawnLocationList: ListBuffer[SpawnLocation] = ListBuffer()
  try {
    for (line <- fileContents.getLines) {

      if (!line.isEmpty) {
        val s = line.split(" ")

        val spawnType = s(0)
        val enemyType = s(1)
        val posX = s(2).toInt
        val posY = s(3).toInt

        if (s.length > 4) {
          val blockadePosX = s(4).toInt
          val blockadePosY = s(5).toInt
          spawnLocationList += SpawnLocation(spawnType, enemyType, posX, posY, blockadePosX, blockadePosY)
        } else
          spawnLocationList += SpawnLocation(spawnType, enemyType, posX, posY)

      }

    }
  } finally fileContents.close()
}

object SpawnLocationsContainer {
  def apply(enemyFileLocation: String) = new SpawnLocationsContainer(enemyFileLocation)
}
