package com.easternsauce.game.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.easternsauce.game.creature.Creature
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class BossHealthBar {
  var boss: Creature = _
  private var visible = false
  private var maxHealthRect: Rectangle = _
  private var healthRect: Rectangle = _

  def render(shapeDrawer: ShapeDrawer, spriteBatch: SpriteBatch): Unit = {
    if (visible && boss != null) {
      GameSystem.font.setColor(Color.WHITE)
      GameSystem.font.draw(spriteBatch, boss.name, GameSystem.originalWidth / 2f - 60, GameSystem.originalHeight - 10)
      shapeDrawer.setColor(Color.ORANGE)
      shapeDrawer.filledRectangle(maxHealthRect)
      shapeDrawer.setColor(Color.RED)
      shapeDrawer.filledRectangle(healthRect)
    }
  }

  def onBossBattleStart(boss: Creature): Unit = {
    this.boss = boss
    visible = true
  }

  def hide(): Unit = {
    visible = false
  }

  def update(): Unit = {
    if (visible && boss != null) {
      maxHealthRect = new Rectangle(GameSystem.originalWidth / 2f - 250, GameSystem.originalHeight - 35, 500, 10)
      healthRect = new Rectangle(
        GameSystem.originalWidth / 2f - 250,
        GameSystem.originalHeight - 35,
        500 * boss.healthPoints / boss.maxHealthPoints,
        10
      )
    }
  }
}
