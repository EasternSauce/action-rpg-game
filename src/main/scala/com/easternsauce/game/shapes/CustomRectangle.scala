package com.easternsauce.game.shapes

import com.badlogic.gdx.math.Intersector

class CustomRectangle(x: Float, y: Float, var w: Float, var h: Float) extends com.badlogic.gdx.math.Rectangle(x, y, w, h) {

  def center: CustomVector2 = {
    val vector = CustomVector2()
    val v = getCenter(vector)
    CustomVector2(v.x, v.y)
  }

  def intersects(other: CustomRectangle): Boolean = {
    Intersector.intersectRectangles(this, other, new CustomRectangle(0,0,1,1))
  }

}
