package yukifuri.script.compiler.visitor.walker.obj

class Integer(
    val value: Int
) : NumberObject() {
    override fun toInt() = value

    override fun toString() = value.toString()

    override fun add(other: NumberObject): NumberObject {
        return Integer(value + other.toInt())
    }

    override fun sub(other: NumberObject): NumberObject {
        return Integer(value - other.toInt())
    }

    override fun mul(other: NumberObject): NumberObject {
        return Integer(value * other.toInt())
    }

    override fun div(other: NumberObject): NumberObject {
        return Integer(value / other.toInt())
    }

    override fun compareLt(r: NumberObject): BooleanObject {
        return BooleanObject(value < r.toInt())
    }

    override fun compareEq(r: NumberObject): BooleanObject {
        return BooleanObject(value == r.toInt())
    }
}
