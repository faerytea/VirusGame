package com.github.farytea.virusgame.engine.core

class WrongStepException(player: Player, h: Int, v: Int) :
    Exception("Player ${player.id} cannot step into $h x $v") {
    constructor(player: Player, coord: Coord): this(player, coord.h, coord.v)
}