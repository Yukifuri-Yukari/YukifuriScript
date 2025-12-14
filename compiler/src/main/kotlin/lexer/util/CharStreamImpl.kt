package yukifuri.script.compiler.lexer.util

import yukifuri.script.compiler.exception.throwEOF

class CharStreamImpl(
    private val input: String
) : CharStream {
    private var ptr = 0

    private var row = 0
    private var col = 0

    private var currentRow = 0
    private var currentCol = 0

    override fun next(): Char {
        if (eof()) throwEOF()

        if (input[ptr] == '\n') {
            currentRow++
            currentCol = 0
        } else {
            currentCol++
        }

        return input[ptr++]
    }

    override fun next(n: Int): String {
        if (eof()) throwEOF()
        val builder = StringBuilder()
        for (ignored in 0 until n) {
            builder.append(next())
        }
        return builder.toString()
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

    override fun row() = row

    override fun col() = col

    override fun currentRow() = currentRow

    override fun currentCol() = currentCol

    override fun updatePosition() {
        row = currentRow
        col = currentCol
    }
}
