package com.easternsauce.game.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.easternsauce.game.shapes.CustomRectangle
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class Hud {
  private val w = Gdx.graphics.getWidth
  private val h = Gdx.graphics.getHeight
  private val proportion = 1 - GameSystem.ScreenProportion

  private val bottomRect = new CustomRectangle(0, 0, w, h * proportion)

  private val pc = GameSystem.playerCharacter

  private var maxHealthRect = new CustomRectangle(10, h * proportion + 40, 100, 10)
  private var healthRect = new CustomRectangle(10, h * proportion + 40, 100 * pc.healthPoints / pc.maxHealthPoints, 10)
  private var maxStaminaRect = new CustomRectangle(10, h * proportion + 25, 100, 10)
  private var staminaRect = new CustomRectangle(10, h * proportion + 25, 100 * pc.healthPoints / pc.maxHealthPoints, 10)
  private var bossHealthBar = new BossHealthBar

  def render(shapeDrawer: ShapeDrawer): Unit = {
    shapeDrawer.filledRectangle(bottomRect, Color.DARK_GRAY)
    shapeDrawer.filledRectangle(maxHealthRect, Color.ORANGE)
    shapeDrawer.filledRectangle(healthRect, Color.RED)
    shapeDrawer.filledRectangle(maxStaminaRect, Color.ORANGE)
    shapeDrawer.filledRectangle(staminaRect, Color.GREEN)
  }


  def update(): Unit = {
    val h = Gdx.graphics.getHeight
    healthRect = new CustomRectangle(10, h * proportion + 40, 100 * pc.healthPoints / pc.maxHealthPoints, 10)
    staminaRect = new CustomRectangle(10, h * proportion + 25, 100 * pc.staminaPoints / pc.maxStaminaPoints, 10)

    bossHealthBar.update()
  }

}
