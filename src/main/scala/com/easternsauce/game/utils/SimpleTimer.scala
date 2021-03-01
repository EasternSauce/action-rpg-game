package com.easternsauce.game.utils

import com.badlogic.gdx.Gdx

import scala.collection.mutable.ListBuffer


class SimpleTimer(var isStarted: Boolean = false){
  var time: Float = 0

  SimpleTimer.timerList += this

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


object SimpleTimer {
  def apply(): SimpleTimer = new SimpleTimer()
  def apply(isStarted: Boolean): SimpleTimer = new SimpleTimer(isStarted)

  private val timerList: ListBuffer[SimpleTimer] = ListBuffer()

  def updateTimers(): Unit = timerList.foreach(_.update(Gdx.graphics.getDeltaTime))
}