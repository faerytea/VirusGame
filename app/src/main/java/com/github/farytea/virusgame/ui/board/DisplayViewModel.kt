package com.github.farytea.virusgame.ui.board

import androidx.lifecycle.ViewModel
import com.github.farytea.virusgame.engine.core.*
import com.github.farytea.virusgame.engine.core.Array2D.Companion.Array2D
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DisplayViewModel : BoardRender, ViewModel() {
    private val flow: MutableStateFlow<Array2D<MutableStateFlow<CellDisplay>>> = MutableStateFlow(Array2D(emptyArray(), 0, 0))
    val boardFlow: StateFlow<Array2D<out Flow<CellDisplay>>>
        get() = flow

    override fun render(board: IBoard, players: List<Player>) {
        val current = flow.value
        if (current.cols != board.cols || current.rows != board.rows) {
            // completely new board
            flow.value = if (board is Board) {
                Array2D(
                        Array(board.data.size) { MutableStateFlow(CellDisplay.Real(board.data[it])) },
                        board.rows,
                        board.cols
                )
            } else {
                Array2D(board.rows, board.cols) { h, v -> MutableStateFlow(CellDisplay.Real(board[h, v])) }
            }
        } else {
            fun CellDisplay.actual() = if (this is CellDisplay.Real) this.cell else null
            // a patch for existing board
            for (h in 0 until current.rows) {
                for (v in 0 until current.cols) {
                    val cellFlow = current[h, v]
                    val newCell = board[h, v]
                    if (cellFlow.value.actual() != newCell) {
                        cellFlow.value = CellDisplay.Real(newCell)
                    }
                }
            }
        }
    }

    override fun notifyCommit(
        board: IBoard,
        players: List<Player>,
        authorIx: Int,
        move: Array<Coord>
    ) {
        val arr = flow.value
        for ((h, v) in move) {
            arr[h, v].value = CellDisplay.Real(board[h, v])
        }
    }

    override fun notifyVerifier(
        board: IBoard,
        players: List<Player>,
        authorIx: Int,
        move: Array<Coord>
    ) {
        val arr = flow.value
        for ((h, v) in move) {
            arr[h, v].value = CellDisplay.Shadow(board[h, v])
        }
    }

    override fun notifyLosing(player: Player) {
        TODO("Not yet implemented")
    }

    override fun notifyVictory(player: Player) {
        TODO("Not yet implemented")
    }

    sealed class CellDisplay(val cell: Cell) {
        class Real(cell: Cell): CellDisplay(cell)
        class Shadow(cell: Cell): CellDisplay(cell)
    }
}