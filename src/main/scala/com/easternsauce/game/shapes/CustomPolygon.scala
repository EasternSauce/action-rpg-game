package com.easternsauce.game.shapes

import com.badlogic.gdx.math.Rectangle

import scala.collection.mutable

class CustomPolygon extends com.badlogic.gdx.math.Polygon {

  def this(vertices: Array[Float]) {
    this()

    setVertices(vertices)
  }

  def this(rect: Rectangle) {
    this()

    val vertices: mutable.ArrayBuffer[Float] = mutable.ArrayBuffer()

    vertices ++= List(rect.x, rect.y)
    vertices ++= List(rect.x, rect.y + rect.height)
    vertices ++= List(rect.x + rect.width, rect.y + rect.height)
    vertices ++= List(rect.x + rect.width, rect.y)

    setVertices(vertices.toArray)
  }

}
