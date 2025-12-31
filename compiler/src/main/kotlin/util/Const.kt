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
        "function", "val", "var", "class", "return"
    )

    val operators = setOf(
        '+', '-', '*', '/', '%', '<', '>', '=', '!', '&', '|'
    )

    val mulOperators = setOf(
        "<<", ">>", "&&", "||", "++", "--"
    )

    object OpMapping {
        operator fun get(key: String): Operator? {
            for (operator in Operator.entries) {
                if (operator.op == key) {
                    return operator
                }
            }
            return null
        }
    }
}
