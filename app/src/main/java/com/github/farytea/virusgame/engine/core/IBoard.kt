package com.github.farytea.virusgame.engine.core

interface IBoard {
    operator fun get(coord: Coord): Cell = get(coord.h, coord.v)
    operator fun get(h: Int, v: Int): Cell = get(Coord(h, v))
    val cols: Int
    val rows: Int
    fun isOutside(h: Int, v: Int): Boolean
    fun checkBorders(h: Int, v: Int) {
        if (isOutside(h, v))
            throw IllegalArgumentException("got $h x $v for $rows x $cols board")
    }
    operator fun set(h: Int, v: Int, c: Cell): Unit = set(Coord(h, v), c)
    operator fun set(coord: Coord, c: Cell): Unit = set(coord.h, coord.v, c)
    fun step(h: Int, v: Int, player: Player): Unit = step(Coord(h, v), player)
    fun step(coord: Coord, player: Player): Unit = step(coord.h, coord.v, player)

    companion object {
        fun transform(player: Player, original: Cell): Cell? = when (original) {
            is Cell.Cross -> if (player.id == original.owner) null else player.fort
            Cell.Empty -> player.cross
            is Cell.EntryPoint -> player.cross
            is Cell.Fort -> null
        }

        inline fun IBoard.forEachNeighbour(h: Int, v: Int, action: (ch: Int, cv: Int, Cell) -> Unit) {
            if (!isOutside(h-1, v-1)) action(h-1, v-1, get(h-1, v-1))
            if (!isOutside(h-1, v)) action(h-1, v, get(h-1, v))
            if (!isOutside(h-1, v+1)) action(h-1, v+1, get(h-1, v+1))
            if (!isOutside(h, v-1)) action(h, v-1, get(h, v-1))
            if (!isOutside(h, v+1)) action(h, v+1, get(h, v+1))
            if (!isOutside(h+1, v-1)) action(h+1, v-1, get(h+1, v-1))
            if (!isOutside(h+1, v)) action(h+1, v, get(h+1, v))
            if (!isOutside(h+1, v+1)) action(h+1, v+1, get(h+1, v+1))
        }
    }
}