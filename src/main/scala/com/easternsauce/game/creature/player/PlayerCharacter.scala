package com.easternsauce.game.creature.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys._
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.{Rectangle, Vector2}
import com.easternsauce.game.ability.DashAbility
import com.easternsauce.game.ability.attack.{BowAttack, SwordAttack, TridentAttack, UnarmedAttack}
import com.easternsauce.game.area.Area
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.npc.NonPlayerCharacter
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.spawn.PlayerRespawnPoint
import com.easternsauce.game.utils.SimpleTimer
import system.GameSystem

import scala.collection.mutable.ListBuffer

class PlayerCharacter(id: String) extends Creature(id) {

  override val hitboxBounds = new Rectangle(18, 0, 28, 64)
  override val isPlayer = true

  override protected val onGettingHitSound: Sound = Assets.painSound

  private var respawnTimer: SimpleTimer = SimpleTimer()

  var dashAbility: DashAbility = _
  var respawning: Boolean = false

  var currentRespawnPoint: PlayerRespawnPoint = _

  def inMenus: Boolean = GameSystem.inventoryWindow.inventoryOpen || GameSystem.dialogueWindow.activated

  loadSprites(Assets.male1SpriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 1)

  override def onInit(): Unit = {
    super.onInit()

    currentRespawnPoint = area.respawnList.head
    respawning = false

  }

  override def controlMovement(): Unit = {

    if (!inMenus) {
      super.controlMovement()

      import com.easternsauce.game.creature.util.WalkDirection._
      if (GameSystem.dirKeysMap(A)) moveInDirection(Left)
      if (GameSystem.dirKeysMap(D)) moveInDirection(Right)
      if (GameSystem.dirKeysMap(W)) moveInDirection(Up)
      if (GameSystem.dirKeysMap(S)) moveInDirection(Down)
    }


  }

  override def performActions(): Unit = {

  }

  override def setFacingDirection(): Unit = {
    val mouseX = Gdx.input.getX
    val mouseY = Gdx.input.getY

    val centerX = Gdx.graphics.getWidth / 2f
    val centerY = (1f - GameSystem.ScreenProportion) * Gdx.graphics.getHeight + GameSystem.ScreenProportion * Gdx.graphics.getHeight / 2f

    facingVector = new Vector2(mouseX - centerX, (Gdx.graphics.getHeight - mouseY) - centerY).nor() // we need to reverse y due to mouse coordinates being in different system
  }

  override def onDeath(): Unit = {
    super.onDeath()
    respawnTimer.restart()
    respawning = true
    sprinting = false

    for (ability <- abilityList) {
      ability.forceStop()
    }

    currentAttack.forceStop()

    GameSystem.hud.bossHealthBar.hide()

    isRunningAnimationActive = false
    toSetBodyNonInteractive = true

    // TODO: add music manager
    Assets.abandonedPlainsMusic.stop()
    Assets.fireDemonMusic.stop()
  }

  override protected def defineCustomAbilities(): Unit = {
    dashAbility = DashAbility(this)

    dashAbility.onPerformAction = () => {
      Assets.flybySound.play(0.05f)
    }

    abilityList += dashAbility
    //        swordAttackAbility.setAimed(true);
    //        unarmedAttackAbility.setAimed(true);
    //        tridentAttackAbility.setAimed(true);
  }

  override def processMovement(): Unit = {
    if (isMoving && !wasMoving) {
      Assets.runningSound.loop(0.1f)
    }


    if (wasMoving && !isMoving) {
      Assets.runningSound.stop()
    }

    super.processMovement()

  }

  def interact(): Unit = {

    if (GameSystem.lootSystem.getVisibleItemsCount == 0) {
      for (creature <- area.creatures.values) {
        if (creature != this) {
          if (GameSystem.distance(creature.body, body) < 70f && creature.isNPC && creature.healthPoints > 0) {
            creature.asInstanceOf[NonPlayerCharacter].triggerDialogue()
          }
        }
      }

      for (playerRespawnPoint <- area.respawnList) {
        if (GameSystem.distance(playerRespawnPoint.body, body) < 70f) {
          currentRespawnPoint = playerRespawnPoint

          if (currentRespawnPoint.respawnSetTimer.time >= currentRespawnPoint.respawnSetTime) {
              currentRespawnPoint.onRespawnSet()

            if (healthPoints < maxHealthPoints / 2) healthPoints = maxHealthPoints / 2

            assert(GameSystem.currentArea.nonEmpty)
            GameSystem.currentArea.get.softReset()
          }
        }
      }
    }
  }

  override def update(): Unit = {
    super.update()

//    println("pos= " +  posX.toInt + ", " + posY.toInt)

    if (respawning && respawnTimer.time > 3f) {
      respawning = false
      pendingArea = currentRespawnPoint.area
      pendingX = currentRespawnPoint.rect.x
      pendingY = currentRespawnPoint.rect.y
      healthPoints = maxHealthPoints
      staminaPoints = maxStaminaPoints
      isAttacking = false
      staminaOveruse = false
      effectMap("staminaRegenStopped").stop()
      GameSystem.currentArea = Option(currentRespawnPoint.area)
      GameSystem.resetArea()
      GameSystem.stopBossBattleMusic()
    }
  }

  def respawnArea: Area = currentRespawnPoint.area
}
