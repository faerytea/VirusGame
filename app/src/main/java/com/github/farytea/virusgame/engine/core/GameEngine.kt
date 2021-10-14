package com.github.farytea.virusgame.engine.core

interface GameEngine {
    val currentPlayer: Player
    fun validate(player: Player = currentPlayer, vararg moves: Coord): Boolean
    fun commit(player: Player = currentPlayer, m1: Coord, m2: Coord, m3: Coord)
    fun findPossibleMoves(player: Player): Sequence<Array<Coord>>
}