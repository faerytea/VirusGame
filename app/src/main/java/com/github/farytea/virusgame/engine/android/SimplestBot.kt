package com.github.farytea.virusgame.engine.android

import com.github.farytea.virusgame.engine.core.Coord
import com.github.farytea.virusgame.engine.core.GameEngine
import com.github.farytea.virusgame.engine.core.Player

object SimplestBot: BotStrategy {
    override fun move(engine: GameEngine, me: Player): Array<Coord> =
        engine.findPossibleMoves(me).first()

    override fun gameOver(victory: Boolean) = Unit
}