package yukifuri.script.compiler.walker.obj

class BooleanObject(
    val value: Boolean
) : NumberObject() {
    override fun toInt(): Int {
        return if (value) 1 else 0
    }

    override fun add(other: NumberObject): NumberObject {
        return when (other) {
            is BooleanObject -> BooleanObject(value xor other.value)
            is Integer -> Integer(toInt() + other.value)
            is FloatNumber -> FloatNumber(toDouble() + other.value)
            else -> TODO()
        }
    }

    override fun sub(other: NumberObject): NumberObject {
        return when (other) {
            is BooleanObject -> BooleanObject(value xor other.value)
            is Integer -> Integer(toInt() - other.value)
            is FloatNumber -> FloatNumber(toDouble() - other.value)
            else -> TODO()
        }
    }

    override fun mul(other: NumberObject): NumberObject {
        return when (other) {
            is BooleanObject -> BooleanObject(value xor other.value)
            is Integer -> Integer(toInt() * other.value)
            is FloatNumber -> FloatNumber(toDouble() * other.value)
            else -> TODO()
        }
    }

    override fun div(other: NumberObject): NumberObject {
        return when (other) {
            is BooleanObject -> BooleanObject(value xor other.value)
            is Integer -> Integer(toInt() / other.value)
            is FloatNumber -> FloatNumber(toDouble() / other.value)
            else -> TODO()
        }
    }

    override fun compareLt(r: NumberObject): BooleanObject {
        return when (r) {
            is BooleanObject -> BooleanObject(value xor r.value)
            is Integer -> BooleanObject(toInt() < r.value)
            is FloatNumber -> BooleanObject(toDouble() < r.value)
            else -> TODO()
        }
    }
}
