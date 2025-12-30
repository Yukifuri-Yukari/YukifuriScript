package yukifuri.script.compiler.util

object Const {
    val chars = ('a' .. 'z') + ('A' .. 'Z') + '_'

    val numbers = ('0' .. '9').toSet()
    val validNumbers = withUnderscore(numbers)
    val hexNumbers = validNumbers + ('a' .. 'f') + ('A' .. 'F')
    val octNumbers = withUnderscore(('0' .. '7').toSet())
    val binNumbers = withUnderscore(('0' .. '1').toSet())

    val charWithNumber = chars + numbers

    val whitespaces = listOf(' ', '\t', '\r', '\n')

    fun withUnderscore(set: Set<Char>) = set + '_'

    fun <T> with(list: List<T>, vararg elements: T) = list + elements

    val keywords = listOf(
        "function"
    )

    val operators = listOf(
        '+', '-', '*', '/', '%', '<', '>', '=', '!', '&', '|'
    )

    // the operators that can double itself and still be a valid operator, like << >> == || &&
    val doubleOperators = listOf(
        '<', '>', '=', '&', '|'
    )

    // 运算符优先级
    val prioritiesOfOperators = mapOf(
        "+" to 0, "-" to 0,
        "*" to 1, "/" to 1, "%" to 1,
        "<" to 2, ">" to 2, "=" to 2,
        "&" to 3, "|" to 3,
        "<<" to 4, ">>" to 4,
        "==" to 5, "!=" to 5,
        "!" to 6, "&&" to 6,
        "||" to 7,
    )
}
