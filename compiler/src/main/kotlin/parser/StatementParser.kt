package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.lexer.token.TokenType

class StatementParser(
    self: Parser
) : SubParser(self) {

    fun parse(): Statement {
        return when {
            peek().type == TokenType.Identifier -> startWithIdentifier()
            else -> TODO()
        }
    }

    private fun startWithIdentifier(): Statement {
        var result = functionCall()
        if (result != null) return result
        TODO()
    }

    fun functionCall(): Statement? {
        next()
        if (peek().type != TokenType.LParen) {
            self.ts.trace()
            return null
        }
        self.ts.trace()
        return expr.functionCall().also {
            next() // ;
        }
    }
}
