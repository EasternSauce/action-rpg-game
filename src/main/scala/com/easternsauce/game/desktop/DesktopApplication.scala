package com.easternsauce.game.desktop

import com.badlogic.gdx.backends.lwjgl3.{
  Lwjgl3Application,
  Lwjgl3ApplicationConfiguration
}
import system.GdxGame

object DesktopApplication {

  var windowWidth: Float = 1280f
  var windowHeight: Float = 720f

  def main(arg: Array[String]): Unit = {

    val config = new Lwjgl3ApplicationConfiguration
    config.setWindowedMode(windowWidth.toInt, windowHeight.toInt)
    new Lwjgl3Application(new GdxGame, config)
  }
}
