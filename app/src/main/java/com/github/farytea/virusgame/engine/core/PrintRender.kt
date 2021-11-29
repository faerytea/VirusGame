package com.github.farytea.virusgame.engine.core

class PrintRender(private val printer: Appendable): BoardRender {
    override fun render(board: IBoard, players: List<Player>) {
        printer.appendLine(players.joinToString { it.id })
        appendBoard(board)
    }

    override fun notifyCommit(
        board: IBoard,
        players: List<Player>,
        authorIx: Int,
        move: Array<Coord>
    ) {
        printer.appendLine("${players[authorIx].id} move:")
        printer.appendLine(players.joinToString { it.id })
        appendBoard(board)
    }

    override fun notifyVerifier(
        board: IBoard,
        players: List<Player>,
        authorIx: Int,
        move: Array<Coord>
    ) {
        printer.appendLine("${players[authorIx].id} found move:")
        printer.appendLine(players.joinToString { it.id })
        appendBoard(board)
    }

    override fun notifyLosing(player: Player) {
        printer.appendLine("${player.id} loses the game")
    }

    override fun notifyVictory(player: Player) {
        printer.appendLine("${player.id} win")
    }

    private fun appendBoard(
        board: IBoard
    ) {
        for (h in 0 until board.rows) {
            for (v in 0 until board.cols) {
                printer.append(
                    when (val cell = board[h, v]) {
                        is Cell.EntryPoint, Cell.Empty -> '_'
                        is Cell.Cross -> cell.owner[0].lowercaseChar()
                        is Cell.Fort -> cell.owner[0].uppercaseChar()
                    }
                )
            }
            printer.appendLine()
        }
    }
}