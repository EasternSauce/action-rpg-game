package com.easternsauce.game.creature.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys._
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.Creature
import com.easternsauce.game.creature.util.WalkDirection.{Down, Left, Right, Up}
import com.easternsauce.game.shapes.Rectangle
import system.GameSystem

class PlayerCharacter(id: String) extends Creature(id) {
  override val rect = new Rectangle(0,5000,64,64)
  override val hitboxBounds = new Rectangle(18, 0, 28, 64)
  override val isPlayer = true

  override protected val onGettingHitSound: Sound = Assets.painSound

  loadSprites(Assets.male1, Map(Left -> 2, Right -> 3, Up -> 4, Down -> 1), 1)

  override def controlMovement(): Unit = {
    super.controlMovement()

    import com.easternsauce.game.creature.util.WalkDirection._
    if (GameSystem.dirKeysMap(A)) move(Left)
    if (GameSystem.dirKeysMap(D)) move(Right)
    if (GameSystem.dirKeysMap(W)) move(Up)
    if (GameSystem.dirKeysMap(S)) move(Down)


  }

  override def performActions(): Unit = {

  }

  override def setFacingDirection(): Unit = {
    val mouseX = Gdx.input.getX
    val mouseY = Gdx.input.getY

    val centerX = Gdx.graphics.getWidth / 2f
    val centerY = Gdx.graphics.getHeight / 2f

    facingVector = new Vector2(mouseX - centerX, mouseY - centerY)
  }

  override def onDeath(): Unit = {
    super.onDeath()
  }
}
