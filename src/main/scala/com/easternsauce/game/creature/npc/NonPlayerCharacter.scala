package com.easternsauce.game.creature.npc

import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.WalkDirection
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.util.ItemType
import com.easternsauce.game.utils.EsTimer
import com.easternsauce.game.wrappers.EsSpriteSheet
import system.GameSystem

import scala.collection.mutable.ListBuffer

class NonPlayerCharacter private (id: String, trader: Boolean, spriteSheet: EsSpriteSheet, val dialogueStartId: String)
    extends Creature(id) {
  override protected val onGettingHitSound: Sound = Assets.painSound
  override val isNPC = true
  val random: scala.util.Random = GameSystem.random
  private val actionTimer: EsTimer = EsTimer(isStarted = true)
  private var traderInventory: ListBuffer[Item] = ListBuffer()

  loadSprites(spriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 1)

  creatureType = "nonPlayerCharacter"

  dropTable.put("lifeRing", 0.05f)
  dropTable.put("poisonDagger", 0.08f)
  dropTable.put("healingPowder", 0.3f)
  dropTable.put("ironSword", 0.1f)
  dropTable.put("woodenSword", 0.1f)

  if (trader) {
    for ((key, value) <- dropTable) {
      for (_ <- 0 until 12) {
        if (GameSystem.random.nextFloat < value) {
          val item = Item(ItemType.getItemType(key), null)
          traderInventory += item
        }
      }
    }
  }

  override def performActions(): Unit = {
    if (actionTimer.time > 4000) {
      lastMovingDir = random.nextInt % 4 match {
        case 0 => WalkDirection.Down
        case 1 => WalkDirection.Up
        case 2 => WalkDirection.Left
        case 3 => WalkDirection.Right
      }

      actionTimer.restart()
    }
  }

  override def onDeath(): Unit = {
    isRunningAnimationActive = false

    for (ability <- abilityList) {
      ability.forceStop()
    }
    currentAttack.forceStop()
  }

  def triggerDialogue(): Unit = {
    if (!GameSystem.dialogueWindow.activated) {
      GameSystem.dialogueWindow.dialogueNPC = this
      GameSystem.inventoryWindow.setTraderInventory(traderInventory)
    }
  }
}

object NonPlayerCharacter {
  def apply(id: String, trader: Boolean, spriteSheet: EsSpriteSheet, dialogueStartId: String) =
    new NonPlayerCharacter(id, trader, spriteSheet, dialogueStartId)
}
