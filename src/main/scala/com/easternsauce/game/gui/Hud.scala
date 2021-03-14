package com.easternsauce.game.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.creature.npc.NonPlayerCharacter
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

  var bossHealthBar = new BossHealthBar

  def render(hudBatch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    shapeDrawer.filledRectangle(bottomRect, Color.DARK_GRAY)
    shapeDrawer.filledRectangle(maxHealthRect, Color.ORANGE)
    shapeDrawer.filledRectangle(healthRect, Color.RED)
    shapeDrawer.filledRectangle(maxStaminaRect, Color.ORANGE)
    shapeDrawer.filledRectangle(staminaRect, Color.GREEN)

    if (!GameSystem.dialogueWindow.activated) {
      if (GameSystem.lootSystem.getVisibleItemsCount == 0) {
        assert(GameSystem.currentArea.nonEmpty)
        GameSystem.font.setColor(Color.WHITE)
        var triggerMessage = ""
        for (creature <- GameSystem.currentArea.get.creatures.values) {
          if (creature != GameSystem.playerCharacter) {

            // TODO: box2d collision
//            if (GameSystem.playerCharacter.rect.intersects(creature.rect) && creature.isInstanceOf[NonPlayerCharacter] && creature.healthPoints > 0) triggerMessage = "> Talk"
          }
        }

        for (playerRespawnPoint <- GameSystem.currentArea.get.respawnList) {
          // TODO: box2d collision
//          if (GameSystem.playerCharacter.rect.intersects(playerRespawnPoint.rect)) triggerMessage = "> Set respawn"
        }
        GameSystem.font.draw(hudBatch, triggerMessage, 10, Gdx.graphics.getHeight - (Gdx.graphics.getHeight * GameSystem.ScreenProportion + 10))
      }
      bossHealthBar.render(shapeDrawer, hudBatch)
    }
  }


  def update(): Unit = {
    val h = Gdx.graphics.getHeight
    healthRect = new CustomRectangle(10, h * proportion + 40, 100 * pc.healthPoints / pc.maxHealthPoints, 10)
    staminaRect = new CustomRectangle(10, h * proportion + 25, 100 * pc.staminaPoints / pc.maxStaminaPoints, 10)

    bossHealthBar.update()
  }

}
