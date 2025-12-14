package yukifuri.script.compiler.lexer.token

import yukifuri.script.compiler.exception.throwEOF

class TokenStream(
    private val tokens: List<Token>
) {
    private var ptr = 0

    fun next(): Token {
        if (eof()) throwEOF()
        return tokens[ptr++]
    }

    fun peek(): Token {
        if (eof()) throwEOF()
        return tokens[ptr]
    }

    fun eof(): Boolean {
        return ptr >= tokens.size
    }
}
