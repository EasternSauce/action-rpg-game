package com.easternsauce.game.item.util

import com.badlogic.gdx.graphics.Texture

class ItemType(val id: String, val name: String, val description: String, val texture: Texture,
               val equipmentType: String, val worth: Int, val maxDamage: Float,
               val maxArmor: Float, val stackable: Boolean, val consumable: Boolean) {

}


object ItemType {
  def loadItemTypes() = {

    //TODO
  }

  def getItemType(itemTypeId: String): ItemType = {
    // TODO
    null
  }
}