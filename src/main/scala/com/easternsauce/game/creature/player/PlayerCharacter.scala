package com.easternsauce.game.creature.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys._
import com.badlogic.gdx.audio.Sound
import com.easternsauce.game.ability.DashAbility
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.{CustomRectangle, CustomVector2}
import system.GameSystem

class PlayerCharacter(id: String) extends Creature(id) {

  override val rect = new CustomRectangle(0,5000,64,64)
  override val hitboxBounds = new CustomRectangle(18, 0, 28, 64)
  override val isPlayer = true

  var dashAbility: DashAbility = _


  override protected val onGettingHitSound: Sound = Assets.painSound

  def inMenus: Boolean = GameSystem.inventoryWindow.inventoryOpen

  loadSprites(Assets.male1SpriteSheet, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 1)

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
    val centerY = (1 - GameSystem.ScreenProportion) * Gdx.graphics.getHeight + GameSystem.ScreenProportion * Gdx.graphics.getHeight / 2f

    facingVector = CustomVector2(mouseX - centerX, (Gdx.graphics.getHeight - mouseY) - centerY) // we need to reverse y due to mouse coordinates being in different system
  }


  override def onDeath(): Unit = {
    super.onDeath()
  }

  override protected def defineCustomAbilities(): Unit = {
    dashAbility = DashAbility(this)

    dashAbility.onPerformAction = () => {
      Assets.flybySound.play(0.1f)
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
}
