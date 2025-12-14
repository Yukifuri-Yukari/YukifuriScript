package yukifuri.script.compiler.lexer

import yukifuri.script.compiler.lexer.token.Token
import yukifuri.script.compiler.lexer.util.CharStream

class Lexer(
    private val cs: CharStream
) {
    private val tokens = mutableListOf<Token>()
}
