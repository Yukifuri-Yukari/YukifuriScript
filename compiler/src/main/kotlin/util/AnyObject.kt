package yukifuri.script.compiler.util

import kotlin.collections.get

class AnyObject(
    private val obj: Any?
) {
    companion object {
        fun <T> create(any: T) = AnyObject(any)
    }

    fun isNull() = obj == null

    fun isObject() = obj is Map<*, *>

    fun isArray() = obj is List<*>

    fun isString() = obj is String

    fun isNumber() = obj is Number

    fun isBoolean() = obj is Boolean

    fun asInt(default: Int? = null): Int {
        return when (obj) {
            is Int -> obj
            is Number -> obj.toInt()
            else -> default ?: throw Exception("Invalid type: $obj")
        }
    }

    fun asDouble(default: Double? = null): Double {
        return when (obj) {
            is Double -> obj
            is Number -> obj.toDouble()
            else -> default ?: throw Exception("Invalid type: $obj")
        }
    }

    fun asString(default: String? = null): String {
        return when (obj) {
            is String -> obj
            else -> default ?: throw Exception("Invalid type: $obj")
        }
    }

    fun asBoolean(default: Boolean? = null): Boolean {
        return when (obj) {
            is Boolean -> obj
            else -> default ?: throw Exception("Invalid type: $obj")
        }
    }

    fun get() = obj

    fun asList(): List<*> {
        return when (obj) {
            is List<*> -> obj
            else -> throw Exception("Invalid type: $obj")
        }
    }

    operator fun get(key: String): AnyObject {
        return when (obj) {
            is Map<*, *> -> create(obj[key])
            else -> throw Exception("Invalid type: $obj")
        }
    }
}