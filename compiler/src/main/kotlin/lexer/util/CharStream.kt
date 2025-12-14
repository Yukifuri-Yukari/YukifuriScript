package yukifuri.script.compiler.lexer.util

interface CharStream{
    fun next(): Char
    fun next(n: Int = 1): String
    fun peek(): Char
    fun peek(n: Int = 1): String
    fun eof(): Boolean
    fun compare(str: String): Boolean
    fun row(): Int
    fun col(): Int
    fun currentRow(): Int
    fun currentCol(): Int
    fun updatePosition()
}
