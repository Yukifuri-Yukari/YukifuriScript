package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.VariableGet
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.literal.FloatLiteral
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.literal.StringLiteral
import yukifuri.script.compiler.exception.Diagnostic
import yukifuri.script.compiler.exception.throwCE
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.util.Const
import yukifuri.script.compiler.util.toInt

class ExpressionParser(
    self: Parser
) : SubParser(self) {
    fun parse(): Expression {
        return when {
            peek().type in setOf(
                TokenType.LParen, TokenType.Identifier, TokenType.Integer,
                TokenType.StringLiteral, TokenType.Decimal
            ) -> primaryOrBinary()
            else -> TODO()
        }
    }

    fun primaryOrBinary(): Expression {
        if (peek().type == TokenType.LParen) {
            next()
            return primaryOrBinary().also { next() /* ) */ }
        }
        next()
        val peekType = peek().type
        self.ts.trace()
        if (peekType == TokenType.Operator) {
            return binary()
        }
        return primary()!!
    }

    fun primary(): Expression? {
        /* Must not start with LParen ( ( )! */
        return when (peek().type) {
            TokenType.Identifier -> {
                val name = next().text
                if (peek().type == TokenType.LParen) {
                    self.ts.trace()
                    functionCall()
                } else VariableGet(name)
            }
            TokenType.Integer -> IntegerLiteral(toInt(next().text))
            TokenType.Decimal -> FloatLiteral(next().text.toFloat())
            TokenType.StringLiteral -> StringLiteral(next().text)
            else -> null
        }
    }

    fun functionCall(): Statement {
        val name = next().text
        if (peek().type != TokenType.LParen) {
            addDiagnostic("Expected (, actually ${peek().text}")
            throwCE("Expected (, actually ${peek().text}")
        }
        next() // (
        val args = mutableListOf<Expression>()
        while (peek().type != TokenType.RParen) {
            if (peek().type == TokenType.Comma)
                next() // ,
            val expr = parse()
            args.add(expr)
        }
        next() // )
        return FunctionCall(name, args)
    }

    fun binary(priority: Int = -1): Expression {
        val left = primary()
        if (left == null) {
            addDiagnostic("Expected primary expression, actually ${peek().text}")
            throwCE("Expected primary expression, actually ${peek().text}")
        }
        var l = left!!
        // 如果下一个 token 不是运算符，直接返回左侧表达式
        if (peek().type != TokenType.Operator) return l

        var r = peek()
        while (r.type == TokenType.Operator) {
            // 只有在确认为运算符后才去查优先级，避免对非运算符调用 map 导致 NPE
            val rOp = Const.OpMapping[r.text]
            val rPriority = rOp?.priority
            if (rPriority == null) {
                addDiagnostic("Unknown operator ${r.text}")
                throwCE("Unknown operator ${r.text}")
            }
            rPriority!!
            if (rPriority <= priority) break

            next() // consume operator
            val rhs = binary(rPriority)
            val bin = BinaryExpr(rOp, l, rhs)
            l = bin
            r = peek()
        }
        return l
    }
}