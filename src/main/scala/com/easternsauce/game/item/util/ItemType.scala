package com.easternsauce.game.item.util

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.{AttackType, Bow, Sword, Trident}

import scala.collection.mutable

class ItemType(val id: String, val name: String, val description: String, val textureRegion: TextureRegion,
               val equipmentType: String, val worth: Int, val maxDamage: Float,
               val maxArmor: Float, val stackable: Boolean, val consumable: Boolean) {

  val weaponSpeed = 1.0f
  var poisonChance = 0.0f

  var attackType: AttackType = null.asInstanceOf[AttackType]

}


object ItemType {

  private val itemTypes: mutable.Map[String, ItemType] = mutable.Map()


  def loadItemTypes(): Unit = {
    val itemType1 = new ItemType("leatherArmor", "Leather Armor", "-", Assets.getItemIcon(8, 7), "body", 150, null.asInstanceOf[Float], 13f, false, false)
    val itemType2 = new ItemType("ringmailGreaves", "Ringmail Greaves", "-", Assets.getItemIcon(3, 8), "boots", 50, null.asInstanceOf[Float], 7f, false, false)
    val itemType3 = new ItemType("hideGloves", "Hide Gloves", "-", Assets.getItemIcon(0, 8), "gloves", 70, null.asInstanceOf[Float], 5f, false, false)
    val itemType4 = new ItemType("crossbow", "Crossbow", "-", Assets.getItemIcon(4, 6), "weapon", 500, 45f, null.asInstanceOf[Float], false, false)
    itemType4.attackType = Bow

    val itemType5 = new ItemType("ironSword", "Iron Sword", "-", Assets.getItemIcon(2, 5), "weapon", 100, 60f, null.asInstanceOf[Float], false, false)
    itemType5.attackType = Sword

    val itemType6 = new ItemType("woodenSword", "Wooden Sword", "-", Assets.getItemIcon(0, 5), "weapon", 70, 45f, null.asInstanceOf[Float], false, false)
    itemType6.attackType = Sword

    val itemType7 = new ItemType("leatherHelmet", "Leather Helmet", "-", Assets.getItemIcon(2, 7), "helmet", 80, null.asInstanceOf[Float], 9f, false, false)
    val itemType8 = new ItemType("lifeRing", "Life Ring", "Increases life when worn", Assets.getItemIcon(5, 8), "ring", 1000, null.asInstanceOf[Float], null.asInstanceOf[Float], false, false)
    val itemType9 = new ItemType("poisonDagger", "Poison Dagger", "-", Assets.getItemIcon(6, 5), "weapon", 500, 40f, null.asInstanceOf[Float], false, false)
    itemType9.poisonChance = 0.5f
    itemType9.attackType = Sword

    val itemType10 = new ItemType("healingPowder", "Healing Powder", "Quickly regenerates health", Assets.getItemIcon(5, 20), null, 45, null.asInstanceOf[Float], null.asInstanceOf[Float], true, true)
    val itemType11 = new ItemType("trident", "Trident", "-", Assets.getItemIcon(8, 5), "weapon", 900, 85f, null.asInstanceOf[Float], false, false)
    itemType11.attackType = Trident

    val itemType12 = new ItemType("steelArmor", "Steel Armor", "-", Assets.getItemIcon(4, 7), "body", 200, null.asInstanceOf[Float], 20f, false, false)
    val itemType13 = new ItemType("steelGreaves", "Steel Greaves", "-", Assets.getItemIcon(3, 8), "boots", 150, null.asInstanceOf[Float], 13f, false, false)
    val itemType14 = new ItemType("steelGloves", "Steel Gloves", "-", Assets.getItemIcon(1, 8), "gloves", 130, null.asInstanceOf[Float], 10f, false, false)
    val itemType15 = new ItemType("steelHelmet", "Steel Helmet", "-", Assets.getItemIcon(1, 7), "helmet", 170, null.asInstanceOf[Float], 15f, false, false)

    val itemType16 = new ItemType("demonTrident", "Demon Trident", "-", Assets.getItemIcon(8, 5), "weapon", 900, 100f, null.asInstanceOf[Float], false, false)
    itemType16.attackType = Trident

    val itemType17 = new ItemType("thiefRing", "Thief Ring", "Gain life on hit", Assets.getItemIcon(5, 8), "ring", 1400, null.asInstanceOf[Float], null.asInstanceOf[Float], false, false)


    itemTypes.put(itemType1.id, itemType1)
    itemTypes.put(itemType2.id, itemType2)
    itemTypes.put(itemType3.id, itemType3)
    itemTypes.put(itemType4.id, itemType4)
    itemTypes.put(itemType5.id, itemType5)
    itemTypes.put(itemType6.id, itemType6)
    itemTypes.put(itemType7.id, itemType7)
    itemTypes.put(itemType8.id, itemType8)
    itemTypes.put(itemType9.id, itemType9)
    itemTypes.put(itemType10.id, itemType10)
    itemTypes.put(itemType11.id, itemType11)
    itemTypes.put(itemType12.id, itemType12)
    itemTypes.put(itemType13.id, itemType13)
    itemTypes.put(itemType14.id, itemType14)
    itemTypes.put(itemType15.id, itemType15)
    itemTypes.put(itemType16.id, itemType16)
    itemTypes.put(itemType17.id, itemType17)

  }

  def getItemType(itemTypeId: String): ItemType = {
    if (itemTypes.contains(itemTypeId)) {
      itemTypes(itemTypeId)
    }
    else {
      throw new RuntimeException("item type doesn't exist: " + itemTypeId)
    }
  }
}