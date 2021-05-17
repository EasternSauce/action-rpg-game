package system

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input, InputProcessor}
import system.GameSystem._

class GdxGame extends ApplicationAdapter with InputProcessor {

  private var spriteBatch: SpriteBatch = _
  private var hudBatch: SpriteBatch = _

  override def create() {
    spriteBatch = new SpriteBatch()
    hudBatch = new SpriteBatch()

    GameSystem.create(spriteBatch, hudBatch)

    Gdx.input.setInputProcessor(this)
  }

  override def render() {
    GameSystem.update()
    GameSystem.render()
  }

  override def dispose(): Unit = {
    spriteBatch.dispose()
    hudBatch.dispose()
  }

  override def keyDown(keycode: Int): Boolean = {
    import Input.Keys._
    keycode match {
      case W | A | S | D => dirKeysMap(keycode) = true
      case SHIFT_LEFT    => playerCharacter.sprinting = true
      case SPACE =>
        if (
          playerCharacter.dashAbility.canPerform && !playerCharacter.inMenus
        ) {
          playerCharacter.dashAbility.setDashVector(
            playerCharacter.movementVector.cpy().nor()
          )
          playerCharacter.dashAbility.perform()

        }
      case _ =>
    }
    false
  }

  override def keyUp(keycode: Int): Boolean = {
    import Input.Keys._
    keycode match {
      case W | A | S | D => dirKeysMap(keycode) = false
      case SHIFT_LEFT    => playerCharacter.sprinting = false
      case _             =>
    }
    false
  }

  override def keyTyped(character: Char): Boolean = false

  override def touchDown(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = false

  override def touchUp(
      screenX: Int,
      screenY: Int,
      pointer: Int,
      button: Int
  ): Boolean = false

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean =
    false

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = false

  override def scrolled(amountX: Float, amountY: Float): Boolean = false

  override def resize(width: Int, height: Int) {
    super.resize(width, height)

    camera.viewportWidth = width
    camera.viewportHeight = height
  }
}
