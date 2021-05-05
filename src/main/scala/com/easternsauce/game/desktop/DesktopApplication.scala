package com.easternsauce.game.desktop

import com.badlogic.gdx.backends.lwjgl3.{Lwjgl3Application, Lwjgl3ApplicationConfiguration}
import system.GdxGame

object DesktopApplication {
  def main(arg: Array[String]): Unit = {
    val config = new Lwjgl3ApplicationConfiguration
    config.setWindowedMode(1280, 720)
    new Lwjgl3Application(new GdxGame, config)
  }
}
