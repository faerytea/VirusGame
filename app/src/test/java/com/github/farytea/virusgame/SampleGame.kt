package com.github.farytea.virusgame

import com.github.farytea.virusgame.engine.android.LocalPlayer
import com.github.farytea.virusgame.engine.core.PrintRender
import com.github.farytea.virusgame.engine.android.SimplestBot
import com.github.farytea.virusgame.engine.core.Board
import com.github.farytea.virusgame.engine.core.Cell
import com.github.farytea.virusgame.engine.core.LocalEngine
import com.github.farytea.virusgame.engine.core.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Test
import java.util.concurrent.Executors

class SampleGame {
    @Test
    fun sample() {
        Dispatchers.setMain(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
        runBlocking(Dispatchers.Main) {
            val players = mutableListOf<Player>(
                LocalPlayer.Bot("X", 1, SimplestBot),
                LocalPlayer.Bot("O", 2, SimplestBot),
            )
            val board = Board.empty(8, 8)
            board[0, 0] = Cell.EntryPoint(players[0].id)
            board[7, 7] = Cell.EntryPoint(players[1].id)
            val engine = LocalEngine(board, players)
            engine.render = PrintRender(System.out)
            players[0].notifyTurn(engine)
            LocalPlayer.Bot.awaitAll()
        }
    }
}