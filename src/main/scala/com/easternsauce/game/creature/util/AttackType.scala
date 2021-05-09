package com.easternsauce.game.creature.util

sealed class AttackType( val walkUpDistance : Float,
                        val holdDistance : Float, val attackDistance : Float)
case object Unarmed extends AttackType(400f, 175f, 130f)
case object Sword extends AttackType(400f, 175f, 130f)
case object Bow extends AttackType(600f, 300f, 600f)
case object Trident extends AttackType(400f, 220f, 200f)
