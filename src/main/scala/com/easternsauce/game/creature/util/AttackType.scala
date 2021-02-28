package com.easternsauce.game.creature.util

sealed class AttackType(val minimumDistance : Float, val walkUpDistance : Float,
                        val holdDistance : Float, val attackDistance : Float)
case object Unarmed extends AttackType(100f, 400f, 175f, 130f)
case object Sword extends AttackType(100f, 400f, 175f, 130f)
case object Bow extends AttackType(300f, 400f, 300f, 300f)
case object Trident extends AttackType(180f, 400f, 220f, 200f)
