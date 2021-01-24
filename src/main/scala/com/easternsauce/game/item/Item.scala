package com.easternsauce.game.item

import com.easternsauce.game.item.loot.LootPile
import com.easternsauce.game.item.util.ItemType
import system.GameSystem

class Item {
  private var itemType: ItemType = _
  private var lootPileBackref : LootPile = _

  private var damage = 0f
  private var armor = 0f

  private var quantity = 1

  def this(itemType: ItemType, lootPileBackref: LootPile) {
    this()

    this.itemType = itemType

    this.lootPileBackref = lootPileBackref

    if (itemType.maxDamage != null.asInstanceOf[Float]) {
      this.damage = Math.ceil(itemType.maxDamage * (0.5f + 0.5f * GameSystem.random.nextFloat())).toFloat
    }
    if (itemType.maxArmor != null.asInstanceOf[Float]) {
      this.armor = Math.ceil(itemType.maxArmor * (0.5f + 0.5f * GameSystem.random.nextFloat())).toFloat
    }

    quantity = 1
  }


}
