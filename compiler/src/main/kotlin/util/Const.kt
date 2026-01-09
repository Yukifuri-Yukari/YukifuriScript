package yukifuri.script.compiler.util

import yukifuri.script.compiler.ast.base.Operator

object Const {
    val chars = (('a' .. 'z') + ('A' .. 'Z') + '_').toSet()

    val numbers = (('0' .. '9') + '.').toSet()
    val scientificNotation = withUnderscore(numbers + 'e' + 'E' + '+' + '-')
    val validNumbers = withUnderscore(numbers)
    val hexNumbers = validNumbers + ('a' .. 'f') + ('A' .. 'F')
    val octNumbers = withUnderscore(('0' .. '7').toSet())
    val binNumbers = withUnderscore(('0' .. '1').toSet())

    val charWithNumber = chars + numbers

    val whitespaces = setOf(' ', '\t', '\r', '\n')

    fun withUnderscore(set: Set<Char>) = set + '_'

    val keywords = setOf(
        "function", "val", "var", "class", "return", "if", "else"
    )

    val operators = setOf(
        '+', '-', '*', '/', '%', '<', '>', '=', '!', '&', '|'
    )

    val mulOperators = run {
        OpMapping.map.values.filter {
            it.op.length > 1
        }.map { it.op }.sortedByDescending {
            it.length
        }
    }

    object OpMapping {
        val map = run {
            val map = mutableMapOf<String, Operator>()
            for (operator in Operator.entries) {
                map[operator.op] = operator
            }
            return@run map
        }

        operator fun get(key: String): Operator? {
            return map[key]
        }
    }
}
