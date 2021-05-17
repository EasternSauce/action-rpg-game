package com.easternsauce.game.dialogue

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.{Gdx, Input}
import com.easternsauce.game.creature.npc.NonPlayerCharacter
import com.easternsauce.game.dialogue.DialogueAction.DialogueAction
import system.GameSystem

import scala.collection.mutable.ListBuffer
import scala.io.Source

class DialogueWindow {
  var activated: Boolean = false
  var dialogueNPC: NonPlayerCharacter = _
  private var dialogueList: ListBuffer[Dialogue] = ListBuffer()
  private var currentDialogue: Dialogue = _
  private var currentDialogueChoices: ListBuffer[Dialogue] = ListBuffer()
  private var currentSelected: Int = 0

  loadDialogueFromFile("assets/dialogues/dialogues.txt")

  def loadDialogueFromFile(filePath: String): Unit = {

    val fileContents = Source.fromFile(filePath)
    try {
      for (line <- fileContents.getLines) {
        val s = line.split(";")

        var action: DialogueAction = null
        var actionArgument: String = null
        val id = s(0)
        val text = s(1)
        var actionCode: String = null
        if (s.length > 2) {
          actionCode = s(2)
          if (actionCode.startsWith("g")) {
            action = DialogueAction.Goto
            actionArgument = actionCode.substring(1)
          } else if (actionCode.startsWith("t")) action = DialogueAction.Trade
          else if (actionCode.startsWith("c")) {
            action = DialogueAction.Choice
            actionArgument = actionCode.substring(1)
          } else if (actionCode.startsWith("e")) action = DialogueAction.Goodbye
        }
        val dialogue = Dialogue(
          id,
          if (text.startsWith(">")) text.substring(1)
          else text,
          action,
          actionArgument
        )
        dialogueList += dialogue

      }
    } finally fileContents.close()
  }

  def render(hudBatch: SpriteBatch): Unit = {
    GameSystem.font.setColor(Color.WHITE)
    if (activated) {
      GameSystem.font.draw(
        hudBatch,
        currentDialogue.text,
        10,
        GameSystem.originalHeight - (GameSystem.originalHeight * GameSystem.ScreenProportion + 10)
      )
      if (currentDialogueChoices != null)
        for (i <- currentDialogueChoices.indices) {
          val text = currentDialogueChoices(i).text

          GameSystem.font.draw(
            hudBatch,
            (if (currentSelected == i) ">"
             else "") + text,
            10,
            GameSystem.originalHeight - (GameSystem.originalHeight * GameSystem.ScreenProportion + 10 + 30 * (i + 1))
          )
        }
    }
  }

  def update(): Unit = {
    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (activated) if (currentDialogueChoices != null) {
        val dialogue = currentDialogueChoices(currentSelected)
        if (dialogue.action == DialogueAction.Goto) {
          currentDialogue = findDialogueById(dialogue.actionArgument)

          setDialogueChoices()
        } else if (dialogue.action == DialogueAction.Goodbye) activated = false
        else if (dialogue.action == DialogueAction.Trade)
          GameSystem.inventoryWindow.openTradeWindow()
      } else if (currentDialogue.action == DialogueAction.Goto) {
        currentDialogue = findDialogueById(currentDialogue.actionArgument)
        setDialogueChoices()
      }
      if (dialogueNPC != null) if (!activated) {
        activated = true
        currentDialogue = findDialogueById(dialogueNPC.dialogueStartId)

        setDialogueChoices()
      }
    }
    if (currentDialogueChoices != null && !GameSystem.inventoryWindow.trading) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.W))
        if (currentSelected > 0) currentSelected -= 1
      if (Gdx.input.isKeyJustPressed(Input.Keys.S))
        if (currentSelected < currentDialogueChoices.size - 1)
          currentSelected += 1
    }
    dialogueNPC = null
  }

  private def findDialogueById(dialogueId: String): Dialogue = {
    for (dialogue <- dialogueList) {
      if (dialogue.id == dialogueId) return dialogue
    }
    null
  }

  def setDialogueChoices(): Unit = {
    currentSelected = 0
    if (currentDialogue.action == DialogueAction.Choice) {
      currentDialogueChoices = ListBuffer()
      val dialogueIndex = dialogueList.indexOf(currentDialogue)
      for (
        i <-
          dialogueIndex + 1 until dialogueIndex + 1 + currentDialogue.actionArgument.toInt
      ) {
        currentDialogueChoices += dialogueList(i)
      }
    } else currentDialogueChoices = null
  }

}
