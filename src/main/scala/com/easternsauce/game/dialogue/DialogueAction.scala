package com.easternsauce.game.dialogue

object DialogueAction extends Enumeration {
  type DialogueAction = Value
  val Goto, Trade, Choice, Goodbye = Value
}
