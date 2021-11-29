package com.github.farytea.virusgame.engine.core

import com.github.farytea.virusgame.engine.core.IBoard.Companion.forEachNeighbour

class LocalEngine(val board: Board, val players: MutableList<Player>) : GameEngine {
    private var playerNo = 0
    override val currentPlayer: Player
        get() = players[playerNo]

    var render: BoardRender? = null
        set(value) {
            field = value
            value?.render(board, players)
        }

    override fun validate(player: Player, vararg moves: Coord): Boolean =
        validateInternal(player, *moves) != null

    override fun commit(player: Player, m1: Coord, m2: Coord, m3: Coord) {
        validateInternal(player, m1, m2, m3)?.applyPatch()
            ?: throw IllegalArgumentException("One of $m1 $m2 $m3 isn't legal")
        val lastPlayer = currentPlayer
        val lastPlayerNo = playerNo
        clearConnectionStatus()
        render?.notifyCommit(board, players, playerNo, arrayOf(m1, m2, m3))
        playerNo = nextPlayerNo(playerNo)

        var cpn = playerNo
        while (playerNo != lastPlayerNo) {
            val cp = players[cpn]
            if (findPossibleMoves(cp).firstOrNull() == null) {
                render?.let {
                    if (players.size > 1) {
                        it.notifyLosing(cp)
                        cp.gameOver(false)
                    }
                }
                players.removeAt(cpn)
                if (cpn < playerNo) --playerNo
                if (cpn >= players.size) cpn = 0
            } else {
                break
            }
        }

        if (players.isEmpty()) players += lastPlayer
        else if (players.size >= 2) currentPlayer.notifyTurn(this)

        if (players.size == 1 && players[0] == lastPlayer) {
            render?.notifyVictory(lastPlayer)
            lastPlayer.gameOver(true)
        }
    }

    private fun nextPlayerNo(current: Int): Int = (current + 1).let { if (it == players.size) 0 else it }

    override fun findPossibleMoves(player: Player): Sequence<Array<Coord>> =
        sequence { // get alive cells
            for (h in 0 until board.rows) {
                for (v in 0 until board.cols) {
                    val coord = Coord(h, v)
                    val cell = board[coord]
                    when {
                        cell is Cell.Cross
                                && cell.owner == player.id -> yield(coord)
                        cell is Cell.Fort
                                && cell.owner == player.id
                                && isConnected(board, h, v, player) -> yield(coord)
                    }
                }
            }
        }.flatMap { // get points of growth
            sequence {
                board.forEachNeighbour(it.h, it.v) { ch, cv, c ->
                    when (c) {
                        is Cell.Cross -> if (c.owner != player.id) yield(Coord(ch, cv))
                        Cell.Empty -> yield(Coord(ch, cv))
                        is Cell.EntryPoint -> yield(Coord(ch, cv))
                        is Cell.Fort -> { /* ignore */ }
                    }
                }
            }
        }.plus(run { // add starting point
            val found = board.data.indexOfFirst { it is Cell.EntryPoint && it.owner == player.id }
            if (found == -1) emptyList()
            else listOf(board.ixToCoord(found))
        }).flatMap { // find moves
            val board = PatchedBoard(board)
            sequence {
                suspend fun SequenceScope<Array<Coord>>.move(coord: Coord, vararg made: Coord) {
                    board.step(coord, player)
                    if (made.size == 2) {
                        val foundMove = arrayOf(*made, coord)
                        render?.notifyVerifier(board, players, players.indexOf(player), foundMove)
                        this.yield(foundMove)
                    }
                    board.forEachNeighbour(coord.h, coord.v) { ch, cv, cell ->
                        when (cell) {
                            is Cell.Cross -> if (cell.owner != player.id) move(Coord(ch, cv), *made, coord)
                            Cell.Empty, is Cell.EntryPoint -> move(Coord(ch, cv), *made, coord)
                            is Cell.Fort -> { /* ignore */ }
                        }
                    }
                    board.patch.remove(coord)
                }
                move(it)
            }
        }

    private fun validateInternal(player: Player, vararg moves: Coord): PatchedBoard? {
        clearConnectionStatus()
        val patched = PatchedBoard(board)
        for (move in moves) {
            when (val cell = patched[move]) {
                is Cell.Cross -> if (cell.owner == player.id) return null
                is Cell.EntryPoint -> if (cell.owner == player.id) {
                    patched.step(move, player)
                    continue
                }
                is Cell.Fort -> return null
            }
            val isConnected = isConnected(patched, move.h, move.v, player)
            if (isConnected) {
                patched.step(move, player)
            } else {
                return null
            }
        }
        return patched
    }

    private fun isConnected(
        board: IBoard,
        h: Int,
        v: Int,
        player: Player
    ): Boolean {
        val visited = HashSet<Coord>()
        return isConnected(board, h, v, player, visited).also {
            for (c in visited) {
                val cell = board[c]
                if (cell is Cell.Fort) cell.connected = it
            }
        }
    }

    private fun isConnected(
        board: IBoard,
        h: Int,
        v: Int,
        player: Player,
        visited: MutableSet<Coord>
    ): Boolean {
        val coord = Coord(h, v)
        if (coord in visited) return false
        visited += coord
        board.forEachNeighbour(h, v) { ch, cv, cell ->
            when (cell) {
                is Cell.Cross -> if (cell.owner == player.id) return true
                is Cell.Fort -> if (cell.owner == player.id) when (cell.connected) {
                    true -> return true
                    null -> {
                        val isConnected = isConnected(board, ch, cv, player, visited)
                        if (isConnected) {
                            cell.connected = isConnected
                            return true
                        }
                    }
                }
                else -> { /* ignore */
                }
            }
        }
        return false
    }

    private fun clearConnectionStatus() {
        for (c in board.data) if (c is Cell.Fort) c.connected = null
    }
}