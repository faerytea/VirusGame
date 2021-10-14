package com.github.farytea.virusgame.engine.core

data class Version(
    /**
     * Breaks compatibility
     */
    val major: Int,

    /**
     * Breaks forward compatibility
     */
    val minor: Int,

    /**
     * Change observable effects in any manner, code still compatible
     */
    val patch: Int,

    /**
     * Bugfixes, performance improvements
     */
    val fix: Int,
) : Comparable<Version> {
    companion object {
        fun Version(version: String): Version {
            val (mj, mn, p, f) = version.split('.').map { it.toInt() }
            return Version(mj, mn, p, f)
        }

        val CURRENT = Version(1, 0, 0, 0)
    }

    fun toVersionString() = "$major.$minor.$patch.$fix"

    override fun compareTo(other: Version): Int {
        major.compareTo(other.major).let { if (it != 0) return it }
        minor.compareTo(other.minor).let { if (it != 0) return it }
        patch.compareTo(other.patch).let { if (it != 0) return it }
        return fix.compareTo(other.fix)
    }

    fun isFullyCompatible(other: Version): Boolean = major == other.major && minor == other.minor

    fun canUseResFor(other: Version): Boolean = major == other.major && minor >= other.minor

    class IncompatibleException(current: Version, target: Version) :
        Exception("Version ${target.toVersionString()} is not compatible to runtime (${current.toVersionString()})")
}