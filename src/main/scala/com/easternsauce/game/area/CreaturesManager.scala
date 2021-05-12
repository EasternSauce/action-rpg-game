package com.easternsauce.game.area

import java.io.PrintWriter

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.item.Item
import space.earlygrey.shapedrawer.ShapeDrawer

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class CreaturesManager(private val area: Area) {



  var creatures: mutable.Map[String, Creature] = mutable.Map()

  private var renderAlivePriorityQueue: mutable.PriorityQueue[Creature] = _
  private var renderDeadPriorityQueue: mutable.PriorityQueue[Creature] = _


  def onAreaEntry(): Unit = {
    creatures.values.filter(creature => !creature.isPlayer && !creature.isNPC).foreach(creature => {
      creature.area.world.destroyBody(creature.body)
    })
    creatures.filterInPlace((_, creature) => creature.isPlayer || creature.isNPC)
  }

  def addCreature(creature: Creature): Unit = {
    creatures.put(creature.id, creature)
  }

  def renderAliveCreatures(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    if (renderAlivePriorityQueue != null) while (renderAlivePriorityQueue.nonEmpty) {
      val creature = renderAlivePriorityQueue.dequeue()
      creature.render(shapeDrawer, batch)
    }
  }

  def renderDeadCreatures(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    if (renderDeadPriorityQueue != null) while (renderDeadPriorityQueue.nonEmpty) {
      val creature = renderDeadPriorityQueue.dequeue()
      creature.render(shapeDrawer, batch)
    }

  }
  def renderAbilities(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    for (creature <- creatures.values) {
      creature.renderAbilities(shapeDrawer, batch)
    }
  }

  def renderHealthBars(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    for (creature <- creatures.values) {
      if (creature.isAlive && !creature.atFullLife) creature.renderHealthBar(shapeDrawer)
    }
  }

  def updateRenderPriorityQueue(): Unit = {

    renderAlivePriorityQueue = new mutable.PriorityQueue[Creature]()
    renderDeadPriorityQueue = new mutable.PriorityQueue[Creature]()

    renderAlivePriorityQueue.addAll(creatures.values.filter(_.isAlive))
    renderDeadPriorityQueue.addAll(creatures.values.filter(!_.isAlive))
  }

  def getCreatureById(id: String): Option[Creature] = {
    creatures.get(id)
  }

  def saveToFile(writer: PrintWriter): Unit = {
    for (creature <- creatures.values) {
      if (creature.isPlayer || creature.isNPC) {
        writer.write("creature " + creature.id + "\n")
        writer.write("area " + creature.area.id + "\n")
        writer.write("pos " + creature.posX + " " + creature.posY + "\n")
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

  def processAreaChanges(creaturesToMove: ListBuffer[Creature]): Unit = {
    for (creature <- creatures.values) {
      if (creature.pendingArea != null) creaturesToMove += creature
    }

    area.updateSpawns()
  }

  def initializeCreatures(): Unit = {
    for (creature <- creatures.values) {
      creature.onInit()
    }
  }

  def clearRespawnableCreatures(): Unit = {
    creatures.values.filter(creature => creature.isPlayer || creature.isNPC)
  }
}

object CreaturesManager {
  def apply(area: Area): CreaturesManager = new CreaturesManager(area)
}
