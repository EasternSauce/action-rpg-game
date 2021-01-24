package com.easternsauce.game.creature.util

import system.GameSystem

object WalkDirection extends Enumeration {
  type WalkDirection = Value
  val Left, Right, Up, Down = Value

  def randomDir(): WalkDirection = {
    GameSystem.random.nextInt(4) match {
      case 0 => Left
      case 1 => Right
      case 2 => Up
      case 3 => Down
    }
  }
}
