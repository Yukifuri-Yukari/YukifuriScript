package yukifuri.script.compiler.visitor.walker.obj

class BooleanObject(
    val value: Boolean
) : NumberObject() {
    override fun toInt(): Int {
        return if (value) 1 else 0
    }

    override fun add(other: NumberObject): NumberObject {
        throw Exception("Illegal operation")
    }

    override fun sub(other: NumberObject): NumberObject {
        throw Exception("Illegal operation")
    }

    override fun mul(other: NumberObject): NumberObject {
        throw Exception("Illegal operation")
    }

    override fun div(other: NumberObject): NumberObject {
        throw Exception("Illegal operation")
    }

    override fun compareLt(r: NumberObject): BooleanObject {
        throw Exception("Illegal operation")
    }

    override fun compareEq(r: NumberObject): BooleanObject {
        return BooleanObject(value == r.toBoolean())
    }
}
