package com.easternsauce.game.area

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.npc.NonPlayerCharacter
import com.easternsauce.game.creature.player.PlayerCharacter
import com.easternsauce.game.shapes.Rectangle

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

  def updateGatesLogic(areaGate: AreaGate, currentAreaHolder: CurrentAreaHolder): Unit = {
    for (creature <- creatures.values) {
      if (creature.isInstanceOf[PlayerCharacter]) if (!creature.passedGateRecently) {
        var gateRect: Rectangle = null
        var destinationArea: Area = null
        var oldArea: Area = null
        var destinationRect: Rectangle = null
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
          currentAreaHolder.currentArea = destinationArea
          oldArea.onLeave()
          destinationArea.onEntry()
        }
      }
    }
  }

  def addCreature(creature: Creature): Unit = {
    creatures.put(creature.id, creature)
  }

  def renderCreatures(spriteBatch: SpriteBatch): Unit = {
    if (renderPriorityQueue != null) while (renderPriorityQueue.nonEmpty) {
      val creature = renderPriorityQueue.dequeue()
      creature.render(spriteBatch)
    }
    for (creature <- creatures.values) {
      creature.renderAbilities(spriteBatch)
    }
  }

  def updateRenderPriorityQueue(): Unit = {

    renderPriorityQueue = new mutable.PriorityQueue[Creature]()

    renderPriorityQueue.addAll(creatures.values)
  }
}

object CreaturesManager {
  def apply(area: Area): CreaturesManager = new CreaturesManager(area)
}
