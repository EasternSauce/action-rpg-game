package com.easternsauce.game.projectile

import com.badlogic.gdx.maps.tiled.TiledMap
import com.easternsauce.game.area.Area
import com.easternsauce.game.creature.Creature
import org.lwjgl.util.vector.Vector2f

import scala.collection.mutable

class Arrow(x: Float, y: Float, area: Area, facingVector: Vector2f, arrowList: List[Arrow], tiledMap: TiledMap, creatures: Map[String, Creature], shooter: Creature) {

  def render(): Unit = {
    // TODO
  }

  def update(): Unit = {
    // TODO
  }

  def isCollidingWithEnvironment(tiledMap: TiledMap, newPosX: Float, newPosY: Float): Boolean = {
    // TODO
    false
  }

  def isCollidingWithCreatures(creatures: mutable.Map[String, Creature], newPosX: Float, newPosY: Float): Boolean = {
    // TODO
    false
  }
}
