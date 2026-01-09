package yukifuri.script.compiler.util

data class Pair3<A, B, C>(
    val first: A,
    val second: B,
    val third: C
) {
    override fun toString(): String {
        return "($first, $second, $third)"
    }

    operator fun get(i: Int): Any? = when (i) {
        0 -> first
        1 -> second
        2 -> third
        else -> throw Exception("index $i out of range")
    }
}

