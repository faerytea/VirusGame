package com.github.farytea.virusgame.engine.android

import android.util.Log
import com.github.farytea.virusgame.engine.core.*

object LogRender: BoardRender {
    override fun render(board: IBoard, players: List<Player>) {
        val sb = StringBuilder(board.cols * board.rows + 100)
        sb.appendLine(players.map { it.id })
        appendBoard(sb, board)
        Log.d("BOARD", sb.toString())
    }

    override fun notifyCommit(
        board: IBoard,
        players: List<Player>,
        authorIx: Int,
        move: Array<Coord>
    ) {
        val sb = StringBuilder(board.cols * board.rows + 100)
        sb.appendLine("${players[authorIx].id} move:")
        sb.appendLine(players.map { it.id })
        appendBoard(sb, board)
        Log.d("BOARD", sb.toString())
    }

    override fun notifyVerifier(
        board: IBoard,
        players: List<Player>,
        authorIx: Int,
        move: Array<Coord>
    ) {
        val sb = StringBuilder(board.cols * board.rows + 100)
        sb.appendLine("${players[authorIx].id} found move:")
        sb.appendLine(players.map { it.id })
        appendBoard(sb, board)
        Log.d("BOARD", sb.toString())
    }

    override fun notifyLosing(player: Player) {
        Log.d("BOARD", "${player.id} loses the game")
    }

    override fun notifyVictory(player: Player) {
        Log.d("BOARD", "${player.id} win")
    }

    private fun appendBoard(
        sb: StringBuilder,
        board: IBoard
    ) {
        for (h in 0 until board.rows) {
            for (v in 0 until board.cols) {
                sb.append(
                    when (val cell = board[h, v]) {
                        is Cell.EntryPoint, Cell.Empty -> '_'
                        is Cell.Cross -> cell.owner[0].lowercaseChar()
                        is Cell.Fort -> cell.owner[0].uppercaseChar()
                    }
                )
            }
            sb.appendLine()
        }
    }
}