package com.easternsauce.game.desktop

import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import system.GdxGame

object DesktopApplication {
  def main(arg: Array[String]): Unit = {
    val config = new LwjglApplicationConfiguration
    config.width = 1024
    config.height = 720
    config.fullscreen = false
    new LwjglApplication(new GdxGame, config)
  }
}
