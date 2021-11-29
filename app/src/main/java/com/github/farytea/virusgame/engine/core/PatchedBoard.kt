package com.github.farytea.virusgame.engine.core

import android.os.Build
import com.github.farytea.virusgame.engine.core.IBoard.Companion.transform

class PatchedBoard(
    val base: Board,
    val patch: LinkedHashMap<Coord, Cell> = LinkedHashMap()
): IBoard {
    override val cols: Int
        get() = base.cols
    override val rows: Int
        get() = base.rows

    override fun isOutside(h: Int, v: Int): Boolean = base.isOutside(h, v)

    override operator fun get(coord: Coord): Cell = patch[coord] ?: base[coord]
    override operator fun set(coord: Coord, c: Cell) {
        checkBorders(coord.h, coord.v)
        patch[coord] = c
    }
    override fun step(coord: Coord, player: Player) {
        set(coord, transform(player, get(coord)) ?: throw WrongStepException(player, coord))
    }

    fun revertPatch(amount: Int = 1) {
        val it = patch.entries.iterator()
        for (i in 1..amount) {
            if (!it.hasNext()) break
            it.next()
            it.remove()
        }
    }

    fun applyPatch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            patch.forEach(base::set)
        } else {
            patch.forEach { (coord, cell) ->
                base[coord] = cell
            }
        }
    }
}