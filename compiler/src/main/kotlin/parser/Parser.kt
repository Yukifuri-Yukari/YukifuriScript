// language: kotlin
package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.UnaryExpr
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.expr.VariableGet
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.FloatLiteral
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.literal.StringLiteral
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.exception.Diagnostic
import yukifuri.script.compiler.exception.Diagnostic.Level
import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.exception.throwCE
import yukifuri.script.compiler.exception.throwEOF
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.util.Const.OpMapping
import yukifuri.script.compiler.util.EnvironmentTable
import yukifuri.script.compiler.util.toInt

class Parser(
    private val ts: TokenStream,
    private val diagnostics: Diagnostics
) {

    private fun next() = ts.next()
    private fun peek() = ts.peek()
    private fun addDiagnostic(message: String, level: Diagnostic.Level = Level.Error) {
        diagnostics.add(Diagnostic.of(peek().row, peek().column, message, level))
    }

    inner class TopLevelStatementParser {
        fun parse(): Module.ModuleBuilder {
            val builder = Module.ModuleBuilder()
            while (peek().type != TokenType.EOF) {
                builder.add(parseOnce())
            }
            return builder
        }

        private fun parseOnce(): Statement {
            return when {
                peek() == TokenType.Keyword to "function" -> functionDeclaration()
                peek().type == TokenType.Keyword -> when (peek().text) {
                    in setOf("var", "val") -> inModule.parse()
                    else -> TODO()
                }
                else -> {
                    println(peek())
                    TODO()
                }
            }
        }

        private fun functionDeclaration(): Statement {
            next() // function
            val name = next().text
            val params = mutableListOf<Pair<String, String>>()
            var returnType = "Nothing"

            if (peek().type != TokenType.LParen) {
                addDiagnostic("Expected (, actually ${peek().text}")
                throwCE("Expected (, actually ${peek().text}")
            }
            next() // (
            while (peek().type != TokenType.RParen) {
                if (peek().type == TokenType.Comma) next()
                val paramName = next().text
                if (peek().type != TokenType.Colon) {
                    addDiagnostic("Expected type specifier, actually ${peek().text}")
                    throwCE("Expected type specifier, actually ${peek().text}")
                }
                next()
                val type = next().text
                params.add(paramName to type)
            }
            next() // )
            if (peek().type == TokenType.Colon) {
                next() // :
                returnType = next().text
            }
            if (peek().type != TokenType.LBrace) {
                addDiagnostic("Expected {, actually ${peek().text}")
                throwCE("Expected {, actually ${peek().text}")
            }

            val body = module()

            return YFunction(name, params, returnType, body).also {
                table.function(it)
            }
        }
    }

    inner class ExpressionParser {
        fun parse(): Expression {
            return when (peek().type) {
                in setOf(
                    TokenType.Integer, TokenType.StringLiteral,
                    TokenType.Decimal, TokenType.LParen, TokenType.Identifier
                ) -> binary()
                else -> {
                    println(peek())
                    TODO()
                }
            }
        }

        private fun primary(): Expression? {
            return when (peek().type) {
                TokenType.Integer -> IntegerLiteral(toInt(next().text))
                TokenType.Decimal -> FloatLiteral(next().text.toFloat())
                TokenType.StringLiteral -> StringLiteral(next().text)
                TokenType.Identifier -> VariableGet(next().text)
                TokenType.LParen -> {
                    next()
                    val expr = expression.parse()
                    val closing = next()
                    if (closing.type != TokenType.RParen) {
                        addDiagnostic("Expected ), actually ${closing.text}")
                        throwCE("Expected ), actually ${closing.text}")
                    }
                    expr
                }
                else -> null
            }
        }

        private fun binary(priority: Int = -1): Expression {
            var l = primary()
            if (l == null) {
                addDiagnostic("Expected primary expression, actually ${peek().text}")
                throwCE("Expected primary expression, actually ${peek().text}")
            }
            // 如果下一个 token 不是运算符，直接返回左侧表达式
            if (peek().type != TokenType.Operator) return l!!

            var r = peek()
            while (r.type == TokenType.Operator) {
                // 只有在确认为运算符后才去查优先级，避免对非运算符调用 map 导致 NPE
                val rOp = OpMapping[r.text]
                val rPriority = rOp?.priority
                if (rPriority == null) {
                    addDiagnostic("Unknown operator ${r.text}")
                    throwCE("Unknown operator ${r.text}")
                }
                rPriority!!
                if (rPriority <= priority) break

                next() // consume operator
                val rhs = binary(rPriority)
                val bin = BinaryExpr(rOp, l!!, rhs)
                l = bin
                r = peek()
            }
            return l!!
        }
    }


    inner class InModuleStatementParser {
        fun parse(): Statement {
            return when {
                peek().type == TokenType.Identifier -> {
                    var result = functionCall()
                    if (result != null) return result
                    result = unary()
                    result
                }
                peek().text in setOf("var", "val") -> variable()
                else -> {
                    println(peek())
                    TODO()
                }
            }
        }

        private fun unary(): Statement {
            var oper = if (peek().type == TokenType.Operator) next() else null
            val id = next().text
            oper = oper ?: next()
            next() // ;
            val op = OpMapping[oper.text]!!
            return UnaryExpr(op, VariableGet(id)).asStatement()
        }

        private fun variable(): Statement {
            val mutable = next().text == "var"
            val name = next().text
            next()
            val expr = expression.parse()
            val semi = next()
            if (semi.type != TokenType.Semicolon) {
                addDiagnostic("Expected ;, actually ${semi.text}")
                throwCE("Expected ;, actually ${semi.text}")
            }
            return VariableDecl(name, expr, mutable)
        }

        private fun functionCall(): Statement? {
            val name = next().text
            if (peek().type != TokenType.LParen) {
                ts.trace()
                return null
            }
            next() // (
            val args = mutableListOf<Expression>()

            while (peek().type != TokenType.RParen) {
                if (peek().type == TokenType.Comma) next()
                args.add(expression.parse())
            }
            next() // )
            val semi = next()
            if (semi.type != TokenType.Semicolon) {
                addDiagnostic("Expected ;, actually ${semi.text}")
                throwCE("Expected ;, actually ${semi.text}")
            }
            return FunctionCall(name, args)
        }
    }

    val topLevel = this.TopLevelStatementParser()
    val inModule = this.InModuleStatementParser()
    val expression = this.ExpressionParser()

    private val builder = Module.ModuleBuilder()

    private var table = EnvironmentTable()

    private lateinit var file: YFile

    fun getTable() = table
    fun getFile() = file

    private fun module(): Module {
        val builder = Module.ModuleBuilder()
        if (peek().type != TokenType.LBrace) {
            addDiagnostic("Expected {, actually ${peek().text}")
            throwCE("Expected {, actually ${peek().text}")
        }
        next()
        while (peek().type != TokenType.RBrace) {
            if (peek().type == TokenType.EOF) throwEOF("Unexpected EOF")
            builder.add(inModule.parse())
        }
        next()
        return builder.build()
    }

    fun parse() {
        builder.add(topLevel.parse())

        file = YFile(builder.build(), table)
    }
}
