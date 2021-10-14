package com.github.farytea.virusgame.engine.core

abstract class Player(val id: String) {
    abstract val color: Int
    val cross = Cell.Cross(id)
    val fort = Cell.Fort(id)
    abstract fun notifyTurn(engine: GameEngine)
}