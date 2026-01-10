package yukifuri.script.compiler.walker.obj

class FloatNumber(
    val value: Double
) : NumberObject() {
    override fun toInt(): Int {
        return value.toInt()
    }

    override fun toDouble(): Double {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }

    override fun add(other: NumberObject): NumberObject {
        return FloatNumber(value + other.toDouble())
    }

    override fun sub(other: NumberObject): NumberObject {
        return FloatNumber(value - other.toDouble())
    }

    override fun mul(other: NumberObject): NumberObject {
        return FloatNumber(value * other.toDouble())
    }

    override fun div(other: NumberObject): NumberObject {
        return FloatNumber(value / other.toDouble())
    }

    override fun compareLt(r: NumberObject): BooleanObject {
        return BooleanObject(value < r.toDouble())
    }
}
