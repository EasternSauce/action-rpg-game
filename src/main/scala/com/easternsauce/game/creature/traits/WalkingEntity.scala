package com.easternsauce.game.creature.traits

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.easternsauce.game.assets.Assets
import com.easternsauce.game.creature.util.WalkDirection.WalkDirection
import com.easternsauce.game.utils.{IntPair, Timer}
import system.GameSystem

trait WalkingEntity extends CreatureProperties with AnimatedWalking with CollisionDetector {
  protected var movingDir: IntPair = IntPair(0, 0)
  protected var isMoving = false
  protected var wasMoving = false
  protected var totalDirections = 0
  protected var movementIncrement: Float = 0
  protected var movementVector: Vector2 = new Vector2(0f, 0f)
  protected var runningStoppedTimer: Timer = Timer()

  val speed: Float = 400.0f

  def move(dir: WalkDirection): Unit = {
    import com.easternsauce.game.creature.util.WalkDirection._
    dir match {
      case Left =>
        movingDir.x = -1
        isMoving = true
        totalDirections = totalDirections + 1
        lastMovingDir = Left
      case Right =>
        movingDir.x = 1
        isMoving = true
        totalDirections = totalDirections + 1
        lastMovingDir = Right
      case Up =>
        movingDir.y = 1
        isMoving = true
        totalDirections = totalDirections + 1
        lastMovingDir = Up
      case Down =>
        movingDir.y = -1
        isMoving = true
        totalDirections = totalDirections + 1
        lastMovingDir = Down
    }
  }

  def onUpdateStart(): Unit = {
    isMoving = false

    totalDirections = 0

    movingDir.x = 0
    movingDir.y = 0

    var adjustedSpeed = this.speed

    if (isAttacking) adjustedSpeed = adjustedSpeed / 3
    else if (sprinting && staminaPoints > 0) adjustedSpeed = adjustedSpeed * 2

    movementIncrement = adjustedSpeed * Gdx.graphics.getDeltaTime

  }

  def processMovement(): Unit = {

    if (totalDirections > 1) {
      movementIncrement = movementIncrement / Math.sqrt(2).floatValue
    }
    val newPosX: Float = rect.getX + movementIncrement * movingDir.x
    val newPosY: Float = rect.getY + movementIncrement * movingDir.y

    if (!isCollidingX(Assets.grassyMap, newPosX, newPosY)
      && newPosX + hitboxBounds.x >= 0 && newPosX <
      GameSystem.getTiledMapRealWidth(Assets.grassyMap) - (hitboxBounds.x + hitboxBounds.width)) {
      move(movementIncrement * movingDir.x, 0)
      movementVector.x = movementIncrement * movingDir.x
    }
    else movementVector.x = 0

    if (!isCollidingY(Assets.grassyMap, newPosX, newPosY)
      && newPosY + hitboxBounds.y >= 0 && newPosY <
      GameSystem.getTiledMapRealHeight(Assets.grassyMap) - (hitboxBounds.y + hitboxBounds.height)) {
      move(0, movementIncrement * movingDir.y)
      movementVector.y = movementIncrement * movingDir.y
    }
    else movementVector.y = 0

    if (isMoving && !wasMoving) {
      if (!isRunningAnimationActive) {

        isRunningAnimationActive = true
        walkAnimationTimer.resetStart()
      }
    }

    if (!isMoving && wasMoving) {
      runningStoppedTimer.resetStart()
    }

    if (!isMoving && isRunningAnimationActive && runningStoppedTimer.time > 0.25f) {
      isRunningAnimationActive = false
      runningStoppedTimer.stop()
      walkAnimationTimer.stop()
    }

    wasMoving = isMoving
  }

  private def move(dx: Float, dy: Float): Unit = {
    rect.x = rect.x + dx
    rect.y = rect.y + dy
  }

  def controlMovement(): Unit = {

  }
}
