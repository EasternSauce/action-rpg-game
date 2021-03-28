package com.easternsauce.game.projectile

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.{TiledMap, TiledMapTileLayer}
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.easternsauce.game.area.Area
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.shapes.CustomVector2
import system.GameSystem

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Arrow(var x: Float, var y: Float, val area: Area, var speedVector: CustomVector2, var arrowList: ListBuffer[Arrow], val tiledMap: TiledMap, val creatures: mutable.Map[String, Creature], val shooter: Creature) {

  private val arrowSpeed: Float = 700f

  private val arrowTexture: Texture = Assets.arrowTexture


  private var hitboxBounds: Rectangle = new Rectangle(19, 19, 2, 2);

  speedVector = speedVector.normal

  private val arrowImage: Image = new Image(arrowTexture)

  var markedForDeletion: Boolean = false

  arrowImage.setOriginX(arrowTexture.getWidth / 2)
  arrowImage.setOriginY(arrowTexture.getHeight / 2)
  arrowImage.rotateBy(speedVector.angleDeg())

  def render(batch: SpriteBatch): Unit = {

    arrowImage.draw(batch, 1.0f)
  }

  def update(): Unit = {
    x = x + speedVector.x * Gdx.graphics.getDeltaTime * arrowSpeed
    y = y + speedVector.y * Gdx.graphics.getDeltaTime * arrowSpeed

    arrowImage.setX(x)
    arrowImage.setY(y)

    val margin = 50
    if (!((x >= 0 - margin && x < GameSystem.getTiledMapRealWidth(tiledMap) + margin) && (y >= 0 - margin && y < GameSystem.getTiledMapRealHeight(tiledMap) + margin))) markedForDeletion = true


    // TODO: box2d collision

    //if (isCollidingWithEnvironment(tiledMap, x, y)) speedVector = new CustomVector2(0f, 0f)

    //if (isCollidingWithCreatures(creatures, x, y)) markedForDeletion = true
  }

  def isCollidingWithEnvironment(tiledMap: TiledMap, newPosX: Float, newPosY: Float): Boolean = {
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
            val rect2 = new Rectangle(newPosX + hitboxBounds.x, newPosY + hitboxBounds.y,
              hitboxBounds.width, hitboxBounds.height)

            rect1.overlaps(rect2)
          }
        }

      }

    }

    collided
  }

  def isCollidingWithCreatures(creatures: mutable.Map[String, Creature], newPosX: Float, newPosY: Float): Boolean = {
    for (creature <- creatures.values) {
      if (creature != shooter) {
        // TODO: box2d collisions
//        val creatureRect = creature.rect
//        val arrowRect = new Rectangle(newPosX + hitboxBounds.getX, newPosY + hitboxBounds.getY, hitboxBounds.getWidth, hitboxBounds.getHeight)
//        if (!(shooter.isInstanceOf[Mob] && creature.isInstanceOf[Mob])) { // mob can't hurt a mob?
//          if (creatureRect.intersects(arrowRect)) {
//            if (speedVector == new Vector2f(0f, 0f) || creature.healthPoints <= 0.0f) return false
//            if (!creature.isImmune) {
//              creature.takeDamage(shooter.equipmentItems(0).damage, true, 0.4f, shooter.rect.center.x, shooter.rect.center.y)
//              shooter.onAttack()
//            }
//            return true
//          }
//        }
      }
    }
    false
  }
}
