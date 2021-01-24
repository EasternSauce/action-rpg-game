package com.easternsauce.game.creature.traits

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.area.Area
import com.easternsauce.game.effect.Effect
import com.easternsauce.game.item.Item
import com.easternsauce.game.shapes.Rectangle

import scala.collection.mutable

trait CreatureProperties {

  protected var healthRegen = 0.3f
  protected var staminaRegen = 10f

  protected var staminaOveruseTime = 1300


  protected var poisonTickTime = 1500

  protected var poisonTime = 20000
  protected var knockbackPower = 0f

  protected var healing = false

  protected var healingTickTime = 300

  protected var healingTime = 8000
  protected var healingPower = 0f

  protected var knockback: Boolean = false

  protected var knockbackVector: Vector2 = _

  protected var knockbackSpeed: Float = 0f

  protected var scale: Float = 1f

  protected var knocbackable = false

  protected var dropTable: mutable.Map[String, Float] = mutable.Map()

  protected val onGettingHitSound: Sound = null

  protected var baseSpeed = 0f

  protected var creatureType: String = "regularCreature"

  protected var effectMap: mutable.Map[String, Effect] = mutable.Map()

  val rect: Rectangle = new Rectangle(0, 0, 64, 64)
  val hitboxBounds: Rectangle = new Rectangle(2, 2, 60, 60)

  val isPlayer = false
  val isMob = false

  var passedGateRecently = false

  var area: Area = _

  var attackVector: Vector2 = new Vector2(0f, 0f)
  var facingVector: Vector2 = new Vector2(0f, 0f)

  var maxHealthPoints = 100f
  var healthPoints: Float = maxHealthPoints

  var maxStaminaPoints = 100f
  var staminaPoints: Float = maxStaminaPoints

  var isAttacking = false

  var toBeRemoved = false

  var pendingArea: Area = _

  var pendingX = 0f
  var pendingY = 0f

  var unarmedDamage = 15f

  var startingPosX: Float = 0f
  var startingPosY: Float = 0f

  var staminaOveruse = false

  var name: String = _

  var isBoss: Boolean = false

  var sprinting = false

  var equipmentItems: mutable.Map[Int, Item] = mutable.Map()

  def alive: Boolean = healthPoints > 0f

}
