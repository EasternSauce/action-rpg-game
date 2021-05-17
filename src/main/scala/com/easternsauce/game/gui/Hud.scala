package com.easternsauce.game.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.easternsauce.game.creature.npc.NonPlayerCharacter
import space.earlygrey.shapedrawer.ShapeDrawer
import system.GameSystem

class Hud {
  private val w = GameSystem.originalWidth
  private val h = GameSystem.originalHeight
  private val proportion = 1 - GameSystem.ScreenProportion
  private val pc = GameSystem.playerCharacter
  var bossHealthBar = new BossHealthBar
  private var bottomRect = new Rectangle(0, 0, w, h * proportion)
  private var maxHealthRect = new Rectangle(10, h * proportion + 40, 100, 10)
  private var healthRect = new Rectangle(
    10,
    h * proportion + 40,
    100 * pc.healthPoints / pc.maxHealthPoints,
    10
  )
  private var maxStaminaRect = new Rectangle(10, h * proportion + 25, 100, 10)
  private var staminaRect = new Rectangle(
    10,
    h * proportion + 25,
    100 * pc.staminaPoints / pc.maxStaminaPoints,
    10
  )

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

            if (
              GameSystem.distance(
                GameSystem.playerCharacter.body,
                creature.body
              ) < 70f && creature
                .isInstanceOf[NonPlayerCharacter] && creature.healthPoints > 0
            ) {
              triggerMessage = "> Talk"
            }
          }
        }

        for (playerRespawnPoint <- GameSystem.currentArea.get.respawnList) {
          if (
            GameSystem.distance(
              GameSystem.playerCharacter.body,
              playerRespawnPoint.body
            ) < 70f
          ) triggerMessage = "> Set respawn"
        }
        GameSystem.font.draw(
          hudBatch,
          triggerMessage,
          10,
          GameSystem.originalHeight - (GameSystem.originalHeight * GameSystem.ScreenProportion + 10)
        )
      }
      bossHealthBar.render(shapeDrawer, hudBatch)
    }
  }

  def update(): Unit = {
    maxHealthRect = new Rectangle(10, bottomRect.height + 40, 100, 10)
    healthRect = new Rectangle(
      10,
      bottomRect.height + 40,
      100 * pc.healthPoints / pc.maxHealthPoints,
      10
    )
    maxStaminaRect = new Rectangle(10, bottomRect.height + 25, 100, 10)
    staminaRect = new Rectangle(
      10,
      bottomRect.height + 25,
      100 * pc.staminaPoints / pc.maxStaminaPoints,
      10
    )

    bossHealthBar.update()
  }

}
