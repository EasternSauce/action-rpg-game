package com.easternsauce.game.creature.traits

import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.easternsauce.game.shapes.Rectangle
import system.GameSystem

trait CollisionDetector extends CreatureProperties {

  def hitbox: Rectangle = new Rectangle(rect.x + hitboxBounds.x, rect.y + hitboxBounds.y,
    hitboxBounds.width, hitboxBounds.height)

  def isCollidingX(tiledMap: TiledMap, newPosX: Float, newPosY: Float): Boolean = {
    val layer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]

    var collided = false

    for {x <- Seq.range(0, layer.getWidth)
         y <- Seq.range(0, layer.getHeight)} {
      if (!collided) {
        val cell: TiledMapTileLayer.Cell = layer.getCell(x, y)

        val traversable: Boolean = cell.getTile.getProperties.get("traversable").asInstanceOf[Boolean]

        if (!traversable) {
          collided = {
            val rect1 = new Rectangle(x * GameSystem.TiledMapCellSize, y * GameSystem.TiledMapCellSize,
              GameSystem.TiledMapCellSize, GameSystem.TiledMapCellSize)
            val rect2 = new Rectangle(newPosX + hitboxBounds.x, hitbox.y,
              hitbox.width, hitbox.height)

            rect1.overlaps(rect2)
          }
        }

      }

    }

    collided
  }

  def isCollidingY(tiledMap: TiledMap, newPosX: Float, newPosY: Float): Boolean = {
    val layer = tiledMap.getLayers.get(0).asInstanceOf[TiledMapTileLayer]

    var collided = false

    // TODO: check collisions for nearby cells only
    for {x <- Seq.range(0, layer.getWidth)
         y <- Seq.range(0, layer.getHeight)} {
      if (!collided) {
        val cell: TiledMapTileLayer.Cell = layer.getCell(x, y)

        val traversable: Boolean = cell.getTile.getProperties.get("traversable").asInstanceOf[Boolean]

        if (!traversable) {
          collided = {
            val rect1 = new Rectangle(x * GameSystem.TiledMapCellSize, y * GameSystem.TiledMapCellSize,
              GameSystem.TiledMapCellSize, GameSystem.TiledMapCellSize)
            val rect2 = new Rectangle(hitbox.x, newPosY + hitboxBounds.y,
              hitbox.width, hitbox.height)

            rect1.overlaps(rect2)
          }
        }

      }
    }

    collided
  }
}
