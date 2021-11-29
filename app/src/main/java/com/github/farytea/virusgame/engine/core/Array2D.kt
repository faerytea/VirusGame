package com.github.farytea.virusgame.engine.core

class Array2D<T>(
    val data: Array<T>,
    val rows: Int,
    val cols: Int,
) {
    init {
        if (cols * rows != data.size) throw IllegalArgumentException("There is no 2D array of $rows rows and $cols cols with ${data.size} items!")
    }
    
    operator fun get(h: Int, v: Int): T {
        checkBorders(h, v)
        return data[h * cols + v]
    }
    
    operator fun set(h: Int, v: Int, c: T) {
        checkBorders(h, v)
        data[h * cols + v] = c
    }
    
    private fun checkBorders(h: Int, v: Int) {
        if (h >= rows || v >= cols) 
            throw IllegalArgumentException("$h x $v is outside of border of size $rows x $cols")
    }

    companion object {
        inline fun <reified T> Array2D(rows: Int, cols: Int, src: (Int, Int) -> T): Array2D<T> {
            val r = Array2D(arrayOfNulls<T?>(rows * cols), rows, cols)
            for (h in 0 until rows)
                for (v in 0 until cols)
                    r[h, v] = src(h, v)
            @Suppress("UNCHECKED_CAST")
            return r as Array2D<T>
        }
    }
}