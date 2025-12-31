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
        "function", "val", "var"
    )

    val operators = setOf(
        '+', '-', '*', '/', '%', '<', '>', '=', '!', '&', '|'
    )

    val mulOperators = setOf(
        "<<", ">>", "&&", "||", "++", "--"
    )

    val operatorMapping = mapOf(
        "+" to Operator.Add,
        "-" to Operator.Sub,
        "*" to Operator.Mul,
        "/" to Operator.Div,
        "%" to Operator.Mod,
        "<" to Operator.Lt,
        "<=" to Operator.Lte,
        ">" to Operator.Gt,
        ">=" to Operator.Gte,
        "==" to Operator.Eq,
        "!=" to Operator.Neq,
        "&&" to Operator.And,
        "||" to Operator.Or,
        "&" to Operator.Lsh,
        "|" to Operator.Rsh,
        "!" to Operator.Not
    )
}
