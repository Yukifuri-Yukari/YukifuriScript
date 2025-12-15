package yukifuri.script.compiler.util

object Const {
    val chars = ('a' .. 'z') + ('A' .. 'Z') + '_'

    val numbers = withUnderscore(('0' .. '9').toList())

    val charWithNumber = chars + numbers

    val hexNumbers = withUnderscore(('0' .. '9') + ('a' .. 'f') + ('A' .. 'F'))

    val octNumbers = withUnderscore(('0' .. '7').toList())

    val binNumbers = withUnderscore(('0' .. '1').toList())

    val whitespaces = listOf(' ', '\t', '\r', '\n')

    fun withUnderscore(list: List<Char>) = list + '_'

    fun <T> with(list: List<T>, vararg elements: T) = list + elements

    val keywords = listOf(
        "function"
    )

    val operators = listOf(
        '+', '-', '*', '/', '%', '<', '>', '=', '!', '&', '|'
    )

    // the operators that can double itself and still be a valid operator, like << >> == || &&
    val doubleLegals = listOf(
        '<', '>', '=', '!', '&', '|'
    )
}
