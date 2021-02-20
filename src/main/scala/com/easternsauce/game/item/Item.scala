package com.easternsauce.game.item

import com.easternsauce.game.item.loot.LootPile
import com.easternsauce.game.item.util.ItemType
import system.GameSystem

class Item(val itemType: ItemType, var lootPileBackref: LootPile = null, var damage: Float = null.asInstanceOf[Float], var armor: Float = null.asInstanceOf[Float], var quantity: Int = 1) {

  val name: String = if (itemType == null.asInstanceOf[ItemType]) null else itemType.name

  val description: String = if (itemType == null.asInstanceOf[ItemType]) null else itemType.description


  if (damage == null.asInstanceOf[Float] && armor == null.asInstanceOf[Float]) {
    if (itemType.maxDamage != null.asInstanceOf[Float]) {
      this.damage = Math.ceil(itemType.maxDamage * (0.5f + 0.5f * GameSystem.random.nextFloat())).toFloat
    }
    if (itemType.maxArmor != null.asInstanceOf[Float]) {
      this.armor = Math.ceil(itemType.maxArmor * (0.5f + 0.5f * GameSystem.random.nextFloat())).toFloat
    }

    quantity = 1
  }

  def removeFromLoot(): Unit = {
    lootPileBackref.itemList -= this
    lootPileBackref = null.asInstanceOf[LootPile]
  }


  def getItemInformation(trader: Boolean): String =
    if (trader)
      (if (this.damage != null) "Damage: " + damage.intValue + "\n" else "") +
        (if (this.armor != null) "Armor: " + armor.intValue + "\n" else "") +
        this.description +
        "\n" + "Worth " + this.itemType.worth.toInt + " Gold" + "\n"
    else
      (if (this.damage != null) "Damage: " + damage.intValue + "\n" else "") +
        (if (this.armor != null) "Armor: " + armor.intValue + "\n" else "") +
        this.description +
        "\n" + "Worth " + (this.itemType.worth * 0.3).toInt + " Gold" + "\n"
}
