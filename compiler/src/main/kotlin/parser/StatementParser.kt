package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.VariableAssign
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.flow.ConditionalFor
import yukifuri.script.compiler.ast.flow.ConditionalJump
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.ast.literal.FloatLiteral
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.literal.StringLiteral
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.util.Const

class StatementParser(
    self: Parser
) : SubParser(self) {

    private fun skip(type: TokenType = TokenType.Semicolon) {
        if (next().type != type)
            addAndError("Expected ${type.name}, actually ${peek().text}")
    }

    fun parse(): Statement {
        return when {
            peek().type == TokenType.Identifier -> startWithIdentifier()
            peek().type == TokenType.Keyword && peek().text in setOf("val", "var")
                -> variableDeclaration()
            peek() == TokenType.Keyword to "return" -> functionReturn()
            peek() == TokenType.Keyword to "for" -> forLoop()
            peek() == TokenType.Keyword to "if" -> condIf()
            peek().type in setOf(
                TokenType.StringLiteral, TokenType.Integer, TokenType.Decimal
            ) -> literal()
            else -> {
                println(peek())
                TODO()
            }
        }
    }

    fun literal(): Statement {
        return when (peek().type) {
            TokenType.StringLiteral -> StringLiteral(peek().text)
            TokenType.Integer -> IntegerLiteral(peek().text.toInt())
            TokenType.Decimal -> FloatLiteral(peek().text.toDouble())
            else -> throw IllegalArgumentException()
        }.also { next() }
    }

    fun condIf(mustHave2Branch: Boolean = false): Statement {
        next() // if
        if (next().type != TokenType.LParen) {
            addAndError("Expected (, actually ${peek().text}")
        }
        val cond = expr.parse()
        if (next().type != TokenType.RParen) {
            addAndError("Expected ), actually ${peek().text}")
        }
        val module = if (peek().type == TokenType.LBrace) {
            self.module()
        } else {
            Module.from(listOf(parse()))
        }

        if (peek() != TokenType.Keyword to "else") {
            if (mustHave2Branch) {
                throw Exception("Expected else, actually ${peek().text}")
            }
            return ConditionalJump(cond, module)
        }
        next() // else
        val elseModule = if (peek().type == TokenType.LBrace) {
            self.module()
        } else {
            Module.from(listOf(parse()))
        }
        return ConditionalJump(cond, module, elseModule)
    }

    fun forLoop(): Statement {
        next() // for
        if (next().type != TokenType.LParen) {
            addAndError("Expected (, actually ${peek().text}")
        }
        val start = parse()
        val cond = expr.parse()
        skip()
        val step = parse()
        val module = if (peek().type == TokenType.LBrace) {
            self.module()
        } else {
            Module.from(listOf(parse()))
        }
        return ConditionalFor(start, cond, step, module)
    }

    fun functionReturn(): Statement {
        next() // return
        val expr = expr.parse()
        return Return(expr).also { skip() }
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
        return VariableDecl(name, type, value, mutable).also { skip() }
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
        return VariableAssign(op, name, expr.parse()).also { next() }
    }

    fun functionCall(): Statement? {
        next()
        if (peek().type != TokenType.LParen) {
            self.ts.trace()
            return null
        }
        self.ts.trace()
        return expr.functionCall().also { skip() }
    }
}
