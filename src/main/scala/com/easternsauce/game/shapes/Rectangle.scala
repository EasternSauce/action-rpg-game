package com.easternsauce.game.shapes

import com.badlogic.gdx.math.{Intersector, Vector2}

class Rectangle(x: Float, y: Float, width: Float, height: Float) extends com.badlogic.gdx.math.Rectangle(x, y, width, height) {

  def center: Vector2 = {
    val vector = new Vector2()
    getCenter(vector)
  }

  def intersects(other: Rectangle): Boolean = {
    Intersector.intersectRectangles(this, other, new Rectangle(0,0,1,1))
  }

}
