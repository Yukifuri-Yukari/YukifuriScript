package yukifuri.script.compiler.lexer.util

import yukifuri.script.compiler.exception.throwEOF

class CharStreamImpl(
    private val input: String
) : CharStream {
    private var ptr = 0

    override fun next(): Char {
        if (eof()) throwEOF()
        return input[ptr++]
    }

    override fun next(n: Int): String {
        if (eof()) throwEOF()
        return input.substring(ptr, ptr + n).also { ptr += n }
    }

    override fun peek(): Char {
        if (eof()) throwEOF()
        return input[ptr]
    }

    override fun peek(n: Int): String {
        if (eof()) throwEOF()
        return input.substring(ptr, ptr + n)
    }

    override fun eof(): Boolean {
        return ptr >= input.length
    }

    override fun compare(str: String): Boolean {
        return peek(str.length) == str
    }
}
