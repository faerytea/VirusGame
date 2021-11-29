package com.github.farytea.virusgame.engine.core

sealed class Cell {
    abstract val marker: Char
    abstract val since: Version

    override fun equals(other: Any?): Boolean =
            other != null
                    && other::class.java == this::class.java
                    && (other as Cell).marker == marker

    override fun hashCode(): Int = marker.code

    sealed class Owned(val owner: String) : Cell() {
        override fun equals(other: Any?): Boolean = super.equals(other) && this.owner == (other as Owned).owner

        override fun hashCode(): Int = super.hashCode() and (owner.hashCode() shl 8)
    }

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

        override fun equals(other: Any?): Boolean = other === this
    }

    companion object {
        private val INITIAL_CELLS_RELEASE = Version(1, 0, 0, 0)
    }
}