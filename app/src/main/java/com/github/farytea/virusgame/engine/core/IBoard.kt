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
            doIfInside(h-1, v-1, action)
            doIfInside(h-1, v, action)
            doIfInside(h-1, v+1, action)
            doIfInside(h, v-1, action)
//            doIfInside(h, v, action)
            doIfInside(h, v+1, action)
            doIfInside(h+1, v-1, action)
            doIfInside(h+1, v, action)
            doIfInside(h+1, v+1, action)
        }

        inline fun IBoard.doIfInside(h: Int, v: Int, action: (ch: Int, cv: Int, Cell) -> Unit) {
            if (!isOutside(h, v))
                action(h, v, get(h, v))
        }
    }
}