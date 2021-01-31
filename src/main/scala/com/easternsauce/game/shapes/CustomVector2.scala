package com.easternsauce.game.shapes

import com.badlogic.gdx.math.Vector2

class CustomVector2(sx: Float = 0f, sy: Float = 0f) extends Vector2(sx, sy) {

  def normal: CustomVector2 = {
    val copy = this.copy
    val l: Float = len()

    if (l == 0) copy
    else {
      copy.x = x / l
      copy.y = y / l
      copy
    }

  }

  def copy: CustomVector2 = {
    new CustomVector2(x, y)
  }
}

object CustomVector2 {
  def apply(): CustomVector2 = new CustomVector2()
  def apply(sx: Float, sy: Float): CustomVector2 = new CustomVector2(sx, sy)
}
