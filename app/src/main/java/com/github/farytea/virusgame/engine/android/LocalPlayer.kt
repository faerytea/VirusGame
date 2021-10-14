package com.github.farytea.virusgame.engine.android

import androidx.annotation.ColorInt
import com.github.farytea.virusgame.engine.core.GameEngine
import com.github.farytea.virusgame.engine.core.Player

sealed class LocalPlayer(id: String, @get:ColorInt override val color: Int): Player(id) {
    class Bot(id: String, color: Int, private val strategy: BotStrategy): LocalPlayer(id, color) {
        override fun notifyTurn(engine: GameEngine) {
            val (m1, m2, m3) = strategy.move(engine, this)
            engine.commit(this, m1, m2, m3)
        }
    }
    class Human
}