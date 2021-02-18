package com.easternsauce.game.area

import java.io.PrintWriter

import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.npc.NonPlayerCharacter
import com.easternsauce.game.creature.player.PlayerCharacter
import com.easternsauce.game.item.Item
import com.easternsauce.game.shapes.{CustomBatch, CustomRectangle}
import system.GameSystem

import scala.collection.mutable

class CreaturesManager(private val area: Area) {



  var creatures: mutable.Map[String, Creature] = mutable.Map()

  private var renderPriorityQueue: mutable.PriorityQueue[Creature] = _

  def onAreaChange(): Unit = {
    for (creature <- creatures.values) {
      if (!(creature.isInstanceOf[PlayerCharacter] || creature.isInstanceOf[NonPlayerCharacter])) if (!creature.alive) creature.toBeRemoved = true
    }

    creatures.filterInPlace((_, creature) => !creature.toBeRemoved)
  }

  def updateGatesLogic(areaGate: AreaGate): Unit = {
    for (creature <- creatures.values) {
      if (creature.isInstanceOf[PlayerCharacter]) if (!creature.passedGateRecently) {
        var gateRect: CustomRectangle = null
        var destinationArea: Area = null
        var oldArea: Area = null
        var destinationRect: CustomRectangle = null
        if (area == areaGate.areaFrom) {
          gateRect = areaGate.fromRect
          oldArea = areaGate.areaFrom
          destinationArea = areaGate.areaTo
          destinationRect = areaGate.toRect
        }
        if (area == areaGate.areaTo) {
          gateRect = areaGate.toRect
          oldArea = areaGate.areaTo
          destinationArea = areaGate.areaFrom
          destinationRect = areaGate.fromRect
        }
        if (creature.rect.intersects(gateRect)) {
          creature.passedGateRecently = true
          creature.moveToArea(destinationArea, destinationRect.getX, destinationRect.getY)
          GameSystem.currentArea = Some(destinationArea)
          oldArea.onLeave()
          destinationArea.onEntry()
        }
      }
    }
  }

  def addCreature(creature: Creature): Unit = {
    creatures.put(creature.id, creature)
  }

  def renderCreatures(spriteBatch: CustomBatch): Unit = {
    if (renderPriorityQueue != null) while (renderPriorityQueue.nonEmpty) {
      val creature = renderPriorityQueue.dequeue()
      creature.render(spriteBatch)
    }
    for (creature <- creatures.values) {
      creature.renderAbilities(spriteBatch)
    }

    for (creature <- creatures.values) {
      creature.renderHealthBar(spriteBatch)
    }
  }

  def updateRenderPriorityQueue(): Unit = {

    renderPriorityQueue = new mutable.PriorityQueue[Creature]()

    renderPriorityQueue.addAll(creatures.values)
  }

  def getCreatureById(id: String): Creature = {
    creatures.get(id) match {
      case Some(creature) => creature
      case _ => throw new RuntimeException("creature doesn't exist: " + id)
    }
  }

  def saveToFile(writer: PrintWriter): Unit = {
    for (creature <- creatures.values) {
      if (creature.isPlayer) { // TODO: or npc
        writer.write("creature " + creature.id + "\n")
        writer.write("area " + creature.area.id + "\n")
        writer.write("pos " + creature.rect.x + " " + creature.rect.y + "\n")
        writer.write("health " + creature.healthPoints + "\n")
        val equipmentItems: mutable.Map[Int, Item] = creature.equipmentItems
        for ((key, value) <- equipmentItems) {
          if (value != null) {
            val damage: String =
              if (value.damage == null.asInstanceOf[Float]) "0"
              else "" + value.damage.intValue
            val armor: String =
              if (value.armor == null.asInstanceOf[Float]) "0"
              else "" + value.armor.intValue
            writer.write("equipment_item " + key + " " + value.itemType.id +
              " " + damage + " " + armor + "\n")
          }
        }
      }
    }
  }
}

object CreaturesManager {
  def apply(area: Area): CreaturesManager = new CreaturesManager(area)
}
