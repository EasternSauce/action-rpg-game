package com.easternsauce.game.area

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.item.Item
import space.earlygrey.shapedrawer.ShapeDrawer

import java.io.PrintWriter
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class CreaturesManager(private val area: Area) {



  var creatures: mutable.Map[String, Creature] = mutable.Map()

  private var renderPriorityQueue: mutable.PriorityQueue[Creature] = _

  def onAreaEntry(): Unit = {
    creatures.values.filter(creature => !creature.isPlayer && !creature.isNPC).foreach(creature => {
      creature.area.world.destroyBody(creature.body)
    })
    creatures.filterInPlace((_, creature) => creature.isPlayer || creature.isNPC)
  }

  def addCreature(creature: Creature): Unit = {
    creatures.put(creature.id, creature)
  }

  def renderCreatures(batch: SpriteBatch, shapeDrawer: ShapeDrawer): Unit = {
    if (renderPriorityQueue != null) while (renderPriorityQueue.nonEmpty) {
      val creature = renderPriorityQueue.dequeue()
      creature.render(shapeDrawer, batch)
    }
    for (creature <- creatures.values) {
      creature.renderAbilities(shapeDrawer, batch)
    }

    for (creature <- creatures.values) {
      if (creature.alive && !creature.atFullLife) creature.renderHealthBar(shapeDrawer)
    }
  }

  def updateRenderPriorityQueue(): Unit = {

    renderPriorityQueue = new mutable.PriorityQueue[Creature]()

    renderPriorityQueue.addAll(creatures.values)
  }

  def getCreatureById(id: String): Option[Creature] = {
    creatures.get(id)
  }

  def saveToFile(writer: PrintWriter): Unit = {
    for (creature <- creatures.values) {
    }
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
