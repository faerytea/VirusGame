package com.github.farytea.virusgame

import com.github.farytea.virusgame.engine.android.LocalPlayer
import com.github.farytea.virusgame.engine.android.LogRender
import com.github.farytea.virusgame.engine.android.SimplestBot
import com.github.farytea.virusgame.engine.core.Board
import com.github.farytea.virusgame.engine.core.LocalEngine
import com.github.farytea.virusgame.engine.core.Player
import org.junit.Test

class SampleGame {
    @Test
    fun sample() {
        val players = mutableListOf<Player>(
            LocalPlayer.Bot("X", 1, SimplestBot),
            LocalPlayer.Bot("O", 2, SimplestBot),
        )
        val board = Board.empty(8, 8)
        val engine = LocalEngine(board, players)
        engine.render = LogRender
        players[0].notifyTurn(engine)
    }
}