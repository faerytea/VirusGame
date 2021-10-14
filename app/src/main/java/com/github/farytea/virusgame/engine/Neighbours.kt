package com.github.farytea.virusgame.engine

import com.github.farytea.virusgame.engine.core.Cell
import com.github.farytea.virusgame.engine.core.Coord
import com.github.farytea.virusgame.engine.core.IBoard

class Neighbours(
    private val board: IBoard,
    private val h: Int,
    private val v: Int,
): Iterable<Cell> {
    override fun iterator(): Iterator<Cell> = It()

    private inner class It: Iterator<Cell> {
        private var next = 0
        private var nextC: Cell? = null

        init {
            findNext()
        }

        override fun hasNext(): Boolean = nextC != null

        override fun next(): Cell {
            val res = nextC ?: throw NoSuchElementException()
            ++next
            if (next < 8)
                findNext()
            else
                nextC = null
            return res
        }

        private tailrec fun findNext() {
            val c: Coord = coord(next)
            if (board.isOutside(c.h, c.v)) {
                ++next
                if (next < 8)
                    findNext()
                else
                    nextC = null
            } else {
                nextC = board[c]
            }
        }

        private fun coord(n: Int): Coord = when (n) {
            0 -> Coord(h - 1, v - 1)
            1 -> Coord(h - 1, v)
            2 -> Coord(h - 1, v + 1)
            3 -> Coord(h, v - 1)
            4 -> Coord(h, v + 1)
            5 -> Coord(h + 1, v - 1)
            6 -> Coord(h + 1, v)
            7 -> Coord(h + 1, v + 1)
            else -> throw IllegalArgumentException("$n")
        }
    }
}