package yukifuri.script.compiler.util

data class Pair3<A, B, C>(
    val first: A,
    val second: B,
    val third: C
) {
    override fun toString(): String {
        return "($first, $second, $third)"
    }

    constructor(p: Pair<A, Pair<B, C>>) : this(p.first, p.second.first, p.second.second)
}

