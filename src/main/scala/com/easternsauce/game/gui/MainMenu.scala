package com.easternsauce.game.gui

import java.io.{BufferedWriter, IOException}
import java.nio.file.{Files, Paths}

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.{Gdx, Input}
import system.{GameState, GameSystem}

import scala.collection.mutable.ListBuffer

class MainMenu {
  private var currentSelected: Int = 0

  private var optionList: ListBuffer[String] = ListBuffer()

  private var startMenu: Boolean = true

  private var prompt: Boolean = false
  private var promptOption: String = ""
  private var promptText: String = ""
  private var savedOptionList: ListBuffer[String] = ListBuffer()

  import java.io.File

  private def saveFileExists: Boolean = {
    val file = new File("saves/savegame.sav")
    file.exists && file.length != 0
  }

  if (saveFileExists) {
    optionList += "Continue"
  }

  optionList += "New game"
  optionList += "Exit"

  def render(batch: SpriteBatch): Unit = {
    if (!prompt) for (i <- 0 until Math.min(4, optionList.size)) {
      GameSystem.font.setColor(Color.WHITE)
      GameSystem.font.draw(batch, (if (currentSelected == i) ">"
      else "") + optionList(i), 100, GameSystem.originalHeight - (100 + 30 * i))
    }
    else {
      GameSystem.font.setColor(Color.WHITE)
      GameSystem.font.draw(batch, promptText, 100, GameSystem.originalHeight - 100)
      for (i <- 0 until Math.min(4, optionList.size)) {
        GameSystem.font.setColor(Color.WHITE)
        GameSystem.font.draw(batch, (if (currentSelected == i) ">"
        else "") + optionList(i), 100, GameSystem.originalHeight - (130 + 30 * i))
      }
    }
  }


  def update(): Unit = {
    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

      if (optionList(currentSelected) == "Continue") {
        GameSystem.state = GameState.Gameplay
        if (startMenu) {
          startMenu = false
          optionList = ListBuffer()
          optionList += "Continue"
          optionList += "New game"
          optionList += "Save game"
          optionList += "Exit"
          GameSystem.loadGame()
        }
      }
      else if (optionList(currentSelected) == "New game") {
        if (!prompt) {
          prompt = true
          promptOption = "New game"
          savedOptionList = optionList
          optionList = ListBuffer()
          optionList += "No"
          optionList += "Yes"
          promptText = "Start new game?"
          currentSelected = 0
        }
      }
      else if (optionList(currentSelected) == "Save game") {
        GameSystem.saveGame()
      }
      else if (optionList(currentSelected) == "Exit") {
        if (!prompt) {
          prompt = true
          promptOption = "Exit"
          savedOptionList = optionList
          optionList = ListBuffer()
          optionList += "No"
          optionList += "Yes"
          promptText = "Quit without saving?"
          currentSelected = 0
        }
      }
      else if (optionList(currentSelected).equals("Yes") || optionList(currentSelected).equals("No")) {
        val option: String = optionList(currentSelected)
        if (option.equals("Yes")) {
          if (promptOption.equals("Exit")) {
            prompt = false
            System.exit(0)
          }
          else if (promptOption == "New game") {

            try {
              var writer: BufferedWriter = Files.newBufferedWriter(Paths.get("saves/savegame.sav"))
              writer.write("")
              writer.flush()
              writer = Files.newBufferedWriter(Paths.get("saves/inventory.sav"))
              writer.write("")
              writer.flush()
              writer = Files.newBufferedWriter(Paths.get("saves/respawn_points.sav"))
              writer.write("")
              writer.flush()
              writer = Files.newBufferedWriter(Paths.get("saves/treasure_collected.sav"))
              writer.write("")
              writer.flush()
            } catch {
              case e: IOException =>
                e.printStackTrace()
            }
            GameSystem.loadGame()
            GameSystem.state = GameState.Gameplay
            if (startMenu) {

              startMenu = false
              optionList = ListBuffer()
              optionList += "Continue"
              optionList += "New game"
              optionList += "Save game"
              optionList += "Exit"
            }
            else optionList = savedOptionList
            prompt = false
            currentSelected = 0
          }
        }
        else {
          optionList = savedOptionList
          prompt = false
          currentSelected = 0
        }
      }
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.W)) if (currentSelected > 0) currentSelected -= 1
    if (Gdx.input.isKeyJustPressed(Input.Keys.S)) if (currentSelected < optionList.size - 1) currentSelected += 1
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) if (!GameSystem.escRecently) if (!GameSystem.inventoryWindow.inventoryOpen && !GameSystem.lootOptionWindow.activated) {

      if (!startMenu) GameSystem.state = GameState.Gameplay
      GameSystem.escRecently = true
    }
  }
}
