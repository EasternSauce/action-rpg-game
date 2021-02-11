package system

import com.badlogic.gdx.graphics.g2d._
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.{ApplicationAdapter, Gdx, Input, InputProcessor}
import com.easternsauce.game.shapes.CustomBatch
import system.GameSystem._

class GdxGame extends ApplicationAdapter with InputProcessor {
  private var spriteBatch: CustomBatch = _
  private var hudBatch: CustomBatch = _
  private var polygonBatch: PolygonSpriteBatch = _

  override def create() {
    spriteBatch = new CustomBatch()
    hudBatch = new CustomBatch()
    polygonBatch = new PolygonSpriteBatch()
    shapeRenderer = new ShapeRenderer()

    GameSystem.create()

    Gdx.input.setInputProcessor(this)
  }

  override def render() {
    GameSystem.update()
    GameSystem.render(spriteBatch, hudBatch, shapeRenderer, polygonBatch)

  }

  override def dispose(): Unit = {
    spriteBatch.dispose()
  }

  override def keyDown(keycode: Int): Boolean = {
    import Input.Keys._
    keycode match {
      case W | A | S | D => dirKeysMap(keycode) = true
      case SHIFT_LEFT => playerCharacter.sprinting = true
      case SPACE => if (playerCharacter.dashAbility.canPerform) {
        playerCharacter.dashAbility.setDashVector(playerCharacter.movementVector.normal)
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
      case SHIFT_LEFT => playerCharacter.sprinting = false
      case _ =>
    }
    false
  }

  override def keyTyped(character: Char): Boolean = false

  override def touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = false

  override def touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = false

  override def mouseMoved(screenX: Int, screenY: Int): Boolean = false

  override def scrolled(amountX: Float, amountY: Float): Boolean = false

}
