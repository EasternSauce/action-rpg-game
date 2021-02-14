package com.easternsauce.game.item.loot

import com.easternsauce.game.area.Area
import com.easternsauce.game.item.Item
import com.easternsauce.game.shapes.CustomRectangle

import scala.collection.mutable.ListBuffer

class LootPile(val area: Area, x: Float, y: Float) {


  val rect: CustomRectangle = new CustomRectangle(x,y,1,1)

  val itemList: ListBuffer[Item] = ListBuffer()

  def render(): Unit = {

  }

  def addItem(item: Item) = ???


}
