package yukifuri.script.compiler.util

class Tuple(
    val obj: List<Any>
) {
    constructor() : this(listOf())
    constructor(vararg obj: Any) : this(listOf(*obj))

    operator fun get(i: Int) = obj[i]

    @Suppress("Unchecked_cast")
    fun <T> value(i: Int) = obj[i] as T

    override fun toString() = "(${obj.joinToString(", ")})"
}
