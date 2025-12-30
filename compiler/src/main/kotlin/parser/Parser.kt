package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.FloatLiteral
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.literal.StringLiteral
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.exception.throwCE
import yukifuri.script.compiler.exception.throwEOF
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.util.Const
import yukifuri.script.compiler.util.EnvironmentTable

class Parser(
    private val ts: TokenStream,
    private val diagnostics: Diagnostics
) {

    private fun next() = ts.next()
    private fun peek() = ts.peek()

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
                else -> TODO()
            }
        }

        private fun functionDeclaration(): Statement {
            next() // function
            val name = next().text
            val params = mutableListOf<Pair<String, String>>()
            var returnType = "Nothing"

            if (peek().type != TokenType.LParen) throwCE("Expected (, actually ${peek().text}")
            next() // (
            while (peek().type != TokenType.RParen) {
                if (peek().type == TokenType.Comma) next()
                val paramName = next().text
                if (peek().type != TokenType.Colon) throwCE("Expected type specifier, actually ${peek().text}")
                next()
                val type = next().text
                params.add(paramName to type)
            }
            next() // )
            if (peek().type == TokenType.Colon) {
                next() // :
                returnType = next().text
            }
            if (peek().type != TokenType.LBrace) throwCE("Expected {, actually ${peek().text}")

            val body = module()

            return YFunction(name, params, returnType, body).also {
                table.function(it)
            }
        }
    }

    inner class ExpressionParser {
        fun parse(): Expression {
            return when {
                primary() != null -> primary()!!
                peek().type in arrayOf(TokenType.Integer, TokenType.Decimal, TokenType.LParen) -> binary()
                else -> TODO()
            }
        }

        private fun primary(): Expression? {
            val n = peek()
            if (n.type == TokenType.Integer)
                return IntegerLiteral(n.text.toInt())
            if (n.type == TokenType.StringLiteral)
                return StringLiteral(n.text)
            if (n.type == TokenType.Decimal)
                return FloatLiteral(n.text.toFloat())
            return null
        }

        private fun binary(priority: Int = 0): Expression {
            val l = primary()
            if (l == null) throwCE("Expected primary expression, actually ${peek().text}")
            var op = peek()
            var opPriority = Const.prioritiesOfOperators[op.text]!!
            while (op.type == TokenType.Operator && opPriority > priority) {
                next()
                val r = binary(opPriority)
                val expr =
            }
        }
    }

    inner class InModuleStatementParser {
        fun parse(): Statement {
            return when {
                peek().type == TokenType.Identifier -> functionCall()
                else -> {
                    println(peek())
                    TODO()
                }
            }
        }

        private fun functionCall(): Statement {
            val name = next().text
            if (peek().type != TokenType.LParen) throwCE("Expected (, actually ${peek().text}")
            next() // (
            val args = mutableListOf<Expression>()

            while (peek().type != TokenType.RParen) {
                if (peek().type == TokenType.Comma) next()
                args.add(expression.parse())
            }
            next() // )
            next() // ;
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
        if (peek().type != TokenType.LBrace) throwCE("Expected {, actually ${peek().text}")
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
