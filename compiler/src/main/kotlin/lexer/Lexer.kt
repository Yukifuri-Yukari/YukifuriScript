package yukifuri.script.compiler.lexer

import yukifuri.script.compiler.lexer.token.Token
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.lexer.util.CharStream
import yukifuri.script.compiler.util.Characters
import yukifuri.script.compiler.util.Constants

class Lexer(
    private val cs: CharStream
) {
    private val tokens = mutableListOf<Token>()
    private val stream = TokenStream(listOf())

    fun next() = cs.next()
    fun peek() = cs.peek()
    fun current() = cs.peek()
    fun eof() = cs.eof()
    fun compare(str: String) = cs.compare(str)
    fun next(n: Int = 1) = cs.next(n)
    fun peek(n: Int = 1) = cs.peek(n)

    fun make(type: TokenType, text: String): Token {
        return Token(type, text, cs.row(), cs.col())
    }

    private fun parseIdentifierAndKeyword(): Token? {
        if (!Characters.valid(current())) return null

        val builder = StringBuilder()
        while (Characters.valid(current(), Constants.charWithNumber) && !eof()) {
            builder.append(next())
        }

        val s = builder.toString()
        return make(if (s in Constants.keywords) TokenType.Keyword else TokenType.Identifier, s)
    }

    fun parse() {
        while (!eof()) {
        }
    }
}
