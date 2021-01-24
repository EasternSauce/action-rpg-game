package com.easternsauce.game.utils

import com.badlogic.gdx.Gdx

import scala.collection.mutable.ListBuffer


class Timer(var isStarted: Boolean = false){
  var time: Float = 0

  Timer.timerList += this

  private def update(delta: Float): Unit = {
    if (isStarted) time = time + delta
  }

  def start(): Unit = isStarted = true

  def stop(): Unit = isStarted = false

  def resetStart(): Unit = {
    time = 0
    isStarted = true
  }

}


object Timer {
  def apply(): Timer = new Timer()
  def apply(isStarted: Boolean): Timer = new Timer(isStarted)

  private val timerList: ListBuffer[Timer] = ListBuffer()

  def updateTimers(): Unit = timerList.foreach(_.update(Gdx.graphics.getDeltaTime))
}