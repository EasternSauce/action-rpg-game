package com.easternsauce.game.utils

import com.badlogic.gdx.Gdx

import scala.collection.mutable.ListBuffer


class EsTimer(var isStarted: Boolean = false){
  var time: Float = 0

  EsTimer.timerList += this

  private def update(delta: Float): Unit = {
    if (isStarted) time = time + delta
  }

  def start(): Unit = isStarted = true

  def stop(): Unit = isStarted = false

  def restart(): Unit = {
    time = 0
    isStarted = true
  }

}


object EsTimer {
  def apply(): EsTimer = new EsTimer()
  def apply(isStarted: Boolean): EsTimer = new EsTimer(isStarted)

  private val timerList: ListBuffer[EsTimer] = ListBuffer()

  def updateTimers(): Unit = timerList.foreach(_.update(Gdx.graphics.getDeltaTime))
}