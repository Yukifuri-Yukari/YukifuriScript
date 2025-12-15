package yukifuri.script.compiler.lexer

import yukifuri.script.compiler.exception.Diagnostic
import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.lexer.token.Token
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.lexer.util.CharStream
import yukifuri.script.compiler.util.Const

class Lexer(
    private val cs: CharStream,
    private val diagnostics: Diagnostics
) {
    val tokens = mutableListOf<Token>()
    val stream = TokenStream(listOf())

    private val builder = StringBuilder()

    /* Util Functions */
    fun next() = cs.next()
    fun peek() = cs.peek()
    fun current() = cs.peek()
    fun eof() = cs.eof()
    fun next(n: Int = 1) = cs.next(n)
    fun peek(n: Int = 1) = cs.peek(n)
    fun make(type: TokenType): Token {
        return Token(type, builder.toString(), cs.row(), cs.col()).also {
            clear()
        }
    }
    fun emit(tk: Token) {
        tokens.add(tk)
    }
    private fun collect(n: Int = 1) {
        builder.append(next(n))
    }
    private fun clear() {
        builder.clear()
    }

    private fun addDiagnostic(message: String) {
        diagnostics.add(
            Diagnostic.of(cs.row(), cs.col(), tokens.count {
                    it.type == TokenType.EOL
                }, message
            )
        )
    }

    /* Parsing Functions */
    private fun skipWhiteSpace() {
    }

    private fun skipComment() {
    }

    private fun parseIdentifierAndKeyword() {
    }

    private fun parseString() {
    }

    private fun parseNumbers() {
    }

    private fun parseSimpleTokens() {
    }

    fun parse() {
        while (!eof()) {
            skipWhiteSpace()
            skipComment()
            val start = cs.peek()
            when (start) {
                // Identifier & Keyword 标识符 & 关键字
                in Const.chars -> parseIdentifierAndKeyword()
                // Numbers (IntegerLiteral, NumberLiteral) 数字
                in Const.numbers -> parseNumbers()
                // tring 字符串
                '\"' -> parseString()
                // Simple Tokens 简单符号
                else -> parseSimpleTokens()
            }
        }
        emit(make(TokenType.EOF))
    }
}
