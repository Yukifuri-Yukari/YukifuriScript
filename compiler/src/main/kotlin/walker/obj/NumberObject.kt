package yukifuri.script.compiler.walker.obj

abstract class NumberObject : Object() {
    abstract fun toInt(): Int
    open fun toDouble(): Double = toInt().toDouble()
    open fun toLong(): Long = toInt().toLong()

    open fun toBoolean(): Boolean = toDouble() != 0.0

    abstract fun add(other: NumberObject): NumberObject
    abstract fun sub(other: NumberObject): NumberObject
    abstract fun mul(other: NumberObject): NumberObject
    abstract fun div(other: NumberObject): NumberObject

    abstract fun compareLt(r: NumberObject): BooleanObject
    abstract fun compareEq(r: NumberObject): BooleanObject
}