package com.github.farytea.virusgame.engine.core

sealed class Cell {
    abstract val marker: Char
    abstract val since: Version

    sealed class Owned(val owner: String) : Cell()

    class Cross(owner: String) : Owned(owner) {
        override val marker
            get() = 'X'
        override val since: Version
            get() = INITIAL_CELLS_RELEASE
    }

    class Fort(owner: String) : Owned(owner) {
        override val marker
            get() = 'F'
        override val since: Version
            get() = INITIAL_CELLS_RELEASE

        var connected: Boolean? = null
    }

    class EntryPoint(owner: String) : Owned(owner) {
        override val marker
            get() = 'E'
        override val since: Version
            get() = INITIAL_CELLS_RELEASE
    }

    object Empty : Cell() {
        override val marker
            get() = '_'
        override val since: Version
            get() = INITIAL_CELLS_RELEASE
    }

    companion object {
        private val INITIAL_CELLS_RELEASE = Version(1, 0, 0, 0)
    }
}