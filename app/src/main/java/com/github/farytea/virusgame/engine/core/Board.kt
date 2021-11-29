package com.github.farytea.virusgame.engine.core

import java.io.EOFException
import java.nio.ByteBuffer

class Board(
    data: Array<Cell>,
    rows: Int,
    cols: Int,
): IBoard {
    init {
        if (cols <= 0 || rows <= 0)
            throw IllegalArgumentException("Zero board is not a valid board")
    }

    private val array = Array2D(data, rows, cols)
    override val rows: Int
        get() = array.rows
    override val cols: Int
        get() = array.cols
    val data
        get() = array.data

    override operator fun get(h: Int, v: Int): Cell = array[h, v]

    override operator fun set(h: Int, v: Int, c: Cell) {
        array[h, v] = c
    }

    override fun isOutside(h: Int, v: Int) = h !in 0 until rows || v !in 0 until cols

    fun ixToCoord(ix: Int) = Coord(ix / cols, ix % cols)

    companion object {
        fun empty(rows: Int, cols: Int) = Board(Array(cols * rows) { Cell.Empty }, rows, cols)

        fun Board.save(players: List<Player>, turn: Int): ByteBuffer {
            val backMap = HashMap<String, Int>().apply {
                players.forEachIndexed { index, player ->
                    put(player.id, (index + turn) % players.size)
                }
            }
            with(ByteBuffer.allocate(data.size * 6 + 16)) {
                val version = data.maxOf { it.since }
                putInt(version.major)
                putInt(version.minor)

                putInt(rows)
                putInt(cols)

                for (c in data) {
                    putChar(c.marker)
                    if (c is Cell.Owned) putInt(backMap[c.owner]!!)
                }

                flip()
                return this
            }
        }

        @Suppress("UsePropertyAccessSyntax")
        fun restoreFrom(save: ByteBuffer, players: List<Player>): Board {
            with(save) {
                val major = getInt()
                val minor = getInt()
                val saveVersion = Version(major, minor, 0, 0)
                if (!Version.CURRENT.canUseResFor(saveVersion))
                    throw Version.IncompatibleException(Version.CURRENT, saveVersion)
                val rows = getInt()
                val cols = getInt()

                val data = Array(rows * cols) {
                    when (val marker = getChar()) {
                        '_' -> Cell.Empty
                        'X', 'F', 'E' -> getInt().let {
                            val owner = players[it].id
                            when (marker) {
                                'X' -> Cell.Cross(owner)
                                'F' -> Cell.Fort(owner)
                                'E' -> Cell.EntryPoint(owner)
                                else -> throw AssertionError()
                            }
                        }
                        else -> throw UnknownCellException(marker)
                    }
                }

                if (position() != limit()) throw EOFException()

                return Board(data, rows, cols)
            }
        }

        fun ByteBuffer.toByteArray() = ByteArray(remaining()).apply { get(this) }

        class UnknownCellException(marker: Char): Exception("Marker $marker is not known")
    }
}