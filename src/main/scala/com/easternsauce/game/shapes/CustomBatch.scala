package com.easternsauce.game.shapes

import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class CustomBatch extends SpriteBatch {
  def drawRect(rect: Rectangle, color: Color) = {
    val pixmap: Pixmap = new Pixmap(rect.getWidth.toInt, rect.getHeight.toInt, Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.fillRectangle(0, 0, rect.width.toInt, rect.height.toInt)

    val texture = new Texture(pixmap)

    draw(texture, rect.x.toInt, rect.y.toInt)
  }

}
