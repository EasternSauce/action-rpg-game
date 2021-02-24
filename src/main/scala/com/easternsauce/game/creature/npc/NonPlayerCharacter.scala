package com.easternsauce.game.creature.npc

import java.util
import java.util.{List, Random}

import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.assets.{Assets, SpriteSheet}
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.WalkDirection
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.item.Item
import com.easternsauce.game.item.util.ItemType
import com.easternsauce.game.utils.Timer
import system.GameSystem

import scala.collection.mutable.ListBuffer

class NonPlayerCharacter(id: String, trader: Boolean, spriteSheet: SpriteSheet, val dialogueStartId: String) extends Creature(id) {
  private var actionTimer: Timer = Timer(isStarted = true)

  private var traderInventory: ListBuffer[Item] = ListBuffer()

  var random: scala.util.Random = GameSystem.random

  override protected val onGettingHitSound: Sound = Assets.painSound


  loadSprites(spriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 1)

  creatureType = "nonPlayerCharacter"

  dropTable.put("lifeRing", 0.05f)
  dropTable.put("poisonDagger", 0.08f)
  dropTable.put("healingPowder", 0.3f)
  dropTable.put("ironSword", 0.1f)
  dropTable.put("woodenSword", 0.1f)

  if (trader) {
    for ((key, value) <- dropTable) {
      for (i <- 0 until 12) {
        if (GameSystem.random.nextFloat < value) {
          val item = new Item(ItemType.getItemType(key), null)
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

      actionTimer.resetStart()
    }
  }

  override def onDeath(): Unit = {
    for (ability <- abilityList) {
      ability.stopAbility()
    }
    currentAttack.stopAbility()
  }

  def triggerDialogue(): Unit = {
    if (!GameSystem.dialogueWindow.activated) {
      GameSystem.dialogueWindow.dialogueNPC = this
      GameSystem.inventoryWindow.setTraderInventory(traderInventory)
    }
  }
}
