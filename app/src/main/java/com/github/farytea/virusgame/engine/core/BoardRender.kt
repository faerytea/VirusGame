package com.github.farytea.virusgame.engine.core

interface BoardRender {
    fun render(board: IBoard, players: List<Player>)
    fun notifyCommit(
        board: IBoard,
        players: List<Player>,
        authorIx: Int,
        move: Array<Coord>
    )
    fun notifyVerifier(
        board: IBoard,
        players: List<Player>,
        authorIx: Int,
        move: Array<Coord>
    )
    fun notifyLosing(player: Player)
    fun notifyVictory(player: Player)
}