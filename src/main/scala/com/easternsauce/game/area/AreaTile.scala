package com.easternsauce.game.area

import com.badlogic.gdx.physics.box2d.Body

class AreaTile(
    val pos: (Int, Int, Int),
    val body: Body,
    val traversable: Boolean,
    val flyover: Boolean
) {}
