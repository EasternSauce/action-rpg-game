package com.easternsauce.game.dialogue

import com.easternsauce.game.dialogue.DialogueAction.DialogueAction

case class Dialogue(
    id: String,
    text: String,
    action: DialogueAction,
    actionArgument: String
)
