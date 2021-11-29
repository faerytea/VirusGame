package com.github.farytea.virusgame.ui.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.farytea.virusgame.engine.core.Array2D
import com.github.farytea.virusgame.engine.core.Array2D.Companion.Array2D
import com.github.farytea.virusgame.engine.core.Cell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

private val gridColor = SolidColor(Color(0.5f, 0.5f, 0.5f))
private val gridStroke = Stroke(width = 0.5f)

@Composable
fun BoardView(
    boardVM: Array2D<out StateFlow<DisplayViewModel.CellDisplay>>,
    playerToColorMapping: Map<String, Color>,
    modifier: Modifier = Modifier,
    onClick: (Int, Int) -> Unit,
) {
    Column(
        modifier = modifier.aspectRatio( (boardVM.cols.toFloat()) / (boardVM.rows.toFloat()) )
    ) {
        for (h in 0 until boardVM.rows) {
            Row(
                modifier = modifier.aspectRatio(boardVM.cols.toFloat()),
                horizontalArrangement = Arrangement.Start
            ) {
                for (v in 0 until boardVM.cols) {
                    val cell by boardVM[h, v].collectAsState()
                    val cColor = (cell.cell as? Cell.Owned)?.let { playerToColorMapping[it.owner] }
                    CellView(
                        modifier = modifier
//                            .aspectRatio(1f, true)
                            .weight(1f / boardVM.cols)
                            .fillMaxHeight(1f)
                        ,
                        cellDisplay = cell,
                        color = cColor
                    ) {
                        onClick(h, v)
                    }
                }
            }
        }
    }
}

@Composable
fun CellView(
    modifier: Modifier = Modifier,
    cellDisplay: DisplayViewModel.CellDisplay,
    color: Color? = Color.Black,
    onClick: () -> Unit
) {
    val cell: Cell = cellDisplay.cell
    val opacity: Float = when (cellDisplay) {
        is DisplayViewModel.CellDisplay.Real -> {
            1f
        }
        is DisplayViewModel.CellDisplay.Shadow -> {
            0.2f
        }
    }
    val drawingF = when (cell) {
        Cell.Empty -> { _: DrawScope, _: SolidColor -> }
        is Cell.EntryPoint -> DrawScope::entryPoint
        is Cell.Cross -> DrawScope::cross
        is Cell.Fort -> DrawScope::fort
    }
    val resColor = SolidColor(
        when (color) {
            null -> Color.Transparent
            else -> color.copy(alpha = opacity)
        }
    )
    Canvas(
        modifier = modifier.clickable(onClick = onClick),
        onDraw = {
            clipRect {
                drawRect(gridColor, style = gridStroke)
                drawingF(this, resColor)
            }
        },
    )
}

fun DrawScope.cross(color: SolidColor) {
    drawCross(gridColor, 4.dp.toPx())
    drawCross(color, 2.dp.toPx())
}

fun DrawScope.drawCross(color: SolidColor, strokeWidth: Float) {
    drawLine(
        brush = color,
        start = Offset.Zero,
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
    drawLine(
        brush = color,
        start = Offset(size.width, 0f),
        end = Offset(0f, size.height),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
}

fun DrawScope.fort(color: SolidColor) {
    drawRect(brush = color, blendMode = BlendMode.Src)
    drawCircle(brush = gridColor, radius = 1.dp.toPx())
}

fun DrawScope.entryPoint(color: SolidColor) {
    drawCircle(brush = color, radius = 1.dp.toPx())
}

@Preview
@Composable
fun CrossPreview() {
    CellView(
        cellDisplay = DisplayViewModel.CellDisplay.Real(Cell.Cross("")),
        modifier = Modifier.size(40.dp)
    ) {

    }
}

@Preview
@Composable
fun BoardPreview() {
    BoardView(
        boardVM = Array2D(
            10,
            5,
        ) { h, v ->
            val owner = Random.nextInt(3).toString()
//            val owner = ((h+v) % 3).toString()
//            val cell = when ((h+v) % 4) {
            val cell = when (Random.nextInt(4)) {
                1 -> Cell.Cross(owner)
                2 -> Cell.Fort(owner)
                3 -> Cell.EntryPoint(owner)
                else -> Cell.Empty
            }
            val d = when {
                Random.nextBoolean() && cell is Cell.Owned && cell !is Cell.EntryPoint -> DisplayViewModel.CellDisplay.Shadow(cell)
                else -> DisplayViewModel.CellDisplay.Real(cell)
            }
            MutableStateFlow(d)
        },
        playerToColorMapping = mapOf(
            "0" to Color.White,
            "1" to Color.Magenta,
            "2" to Color.Black,
        ),
        onClick = { _, _ -> },
        modifier = Modifier.width(200.dp)
    )
}
