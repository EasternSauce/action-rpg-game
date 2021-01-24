package com.easternsauce.game.item.util

class ItemType(val id: String, val name: String, val description: String, val image: AnyVal/*TODO: prepare image class???*/,
               val equipmentType: String, val worth: Int, val maxDamage: Float,
               val maxArmor: Float, val stackable: Boolean, val consumable: Boolean) {

}


object ItemType {
  def getItemType(itemTypeId: String): ItemType = {
    // TODO
    null
  }
}