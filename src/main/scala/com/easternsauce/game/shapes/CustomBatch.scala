package com.easternsauce.game.shapes

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}

class CustomBatch extends SpriteBatch {
  def drawRect(rect: CustomRectangle, color: Color): Unit = {
    val pixmap: Pixmap = new Pixmap(rect.getWidth.toInt, rect.getHeight.toInt, Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.fillRectangle(0, 0, rect.width.toInt, rect.height.toInt)

    val texture = new Texture(pixmap)

    draw(texture, rect.x.toInt, rect.y.toInt)

    pixmap.dispose()
  }

  def drawRectBorder(rect: CustomRectangle, color: Color): Unit = {
    val pixmap: Pixmap = new Pixmap(rect.getWidth.toInt, rect.getHeight.toInt, Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.drawRectangle(0, 0, rect.width.toInt, rect.height.toInt)

    val texture = new Texture(pixmap)

    draw(texture, rect.x.toInt, rect.y.toInt)

    pixmap.dispose()
  }

}
