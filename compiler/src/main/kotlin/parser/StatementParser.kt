package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.VariableAssign
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.lexer.token.Token
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.util.Const

class StatementParser(
    self: Parser
) : SubParser(self) {

    fun parse(): Statement {
        return when {
            peek().type == TokenType.Identifier -> startWithIdentifier()
            peek().type == TokenType.Keyword && peek().text in setOf("val", "var")
                -> variableDeclaration()
            peek() == TokenType.Keyword to "return" -> functionReturn()
            peek() == TokenType.Keyword to "for" -> forLoop()
            peek() == TokenType.Keyword to "if" -> condIf()
            else -> {
                TODO()
            }
        }
    }

    fun condIf(): Statement {
        next() // if
    }

    fun forLoop(): Statement {
        next() // for
        TODO()
    }

    fun functionReturn(): Statement {
        next() // return
        val expr = expr.parse()
        return Return(expr).also { next() /* ; */ }
    }

    fun variableDeclaration(): Statement {
        val mutable = next().text == "var"
        val name = next().text
        val type = if (peek().type == TokenType.Colon) {
            next()
            next().text
        } else "auto"

        if (next() != TokenType.Operator to "=") {
            addAndError("Expected =, actually ${peek().text}")
        }
        val value = expr.parse()
        return VariableDecl(name, type, value, mutable).also {
            next() // ;
        }
    }

    private fun startWithIdentifier(): Statement {
        var result = functionCall()
        if (result != null) return result
        result = variableAssign()
        if (result != null) return result
        TODO()
    }

    fun variableAssign(): Statement? {
        val name = next().text
        if (peek().type != TokenType.Operator) {
            self.ts.trace()
            return null
        }
        val op = Const.OpMapping[next().text]!!
        return VariableAssign(op, name, expr.parse()).also {
            next() // ;
        }
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
