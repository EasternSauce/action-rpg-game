package com.easternsauce.game.item

import com.easternsauce.game.item.loot.LootPile
import com.easternsauce.game.item.util.ItemType
import system.GameSystem

class Item(val itemType: ItemType, val lootPileBackref: LootPile = null, var damage: Float = null.asInstanceOf[Float], var armor: Float = null.asInstanceOf[Float], var quantity: Int = 1) {

  if (damage == null && armor == null) {
    if (itemType.maxDamage != null.asInstanceOf[Float]) {
      this.damage = Math.ceil(itemType.maxDamage * (0.5f + 0.5f * GameSystem.random.nextFloat())).toFloat
    }
    if (itemType.maxArmor != null.asInstanceOf[Float]) {
      this.armor = Math.ceil(itemType.maxArmor * (0.5f + 0.5f * GameSystem.random.nextFloat())).toFloat
    }

    quantity = 1
  }




}
