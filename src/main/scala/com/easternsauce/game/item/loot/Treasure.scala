package com.easternsauce.game.item.loot

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.easternsauce.game.area.Area
import com.easternsauce.game.assets.Assets
import space.earlygrey.shapedrawer.ShapeDrawer

class Treasure(override val area: Area, x: Float, y: Float) extends LootPile(area, x, y) {

  val treasureImage: Image = new Image(Assets.treasureTexture)

  treasureImage.setX(x)
  treasureImage.setY(y)
  treasureImage.setScale(2.0f)

  override def render(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    if (visible) {

      treasureImage.draw(batch, 1.0f)
    }
  }
}
