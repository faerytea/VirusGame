package com.github.farytea.virusgame.engine.android

import androidx.annotation.ColorInt
import com.github.farytea.virusgame.engine.core.GameEngine
import com.github.farytea.virusgame.engine.core.Player
import kotlinx.coroutines.*

sealed class LocalPlayer(id: String, @get:ColorInt override val color: Int): Player(id) {
    class Bot(id: String, color: Int, private val strategy: BotStrategy): LocalPlayer(id, color) {
        companion object {
            val scope = CoroutineScope(Dispatchers.Default)
            suspend fun awaitAll() {
                scope.coroutineContext.job.join()
            }
        }

        override fun notifyTurn(engine: GameEngine) {
            scope.launch {
                val (m1, m2, m3) = strategy.move(engine, this@Bot)
                withContext(Dispatchers.Main) {
                    engine.commit(this@Bot, m1, m2, m3)
                }
            }
        }

        override fun gameOver(victory: Boolean) {
            strategy.gameOver(victory)
            scope.cancel()
        }
    }
    class Human
}