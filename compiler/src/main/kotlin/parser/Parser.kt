package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.Inc
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
import yukifuri.script.compiler.util.Const
import yukifuri.script.compiler.util.EnvironmentTable

class Parser(
    private val ts: TokenStream,
    private val diagnostics: Diagnostics
) {

    private fun next() = ts.next()
    private fun peek() = ts.peek()
    private fun addDiagnostic(message: String, level: Diagnostic.Level = Level.Error) {
        // diagnostics.add(Diagnostic.of(peek()))
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
            return when (peek().type) {
                TokenType.StringLiteral -> StringLiteral(next().text)
                TokenType.Integer -> IntegerLiteral(next().text.toInt())
                TokenType.Decimal -> FloatLiteral(next().text.toFloat())
                TokenType.Identifier -> VariableGet(next().text)
                else -> TODO()
            }
        }
    }

    inner class InModuleStatementParser {
        fun parse(): Statement {
            return when {
                peek().type == TokenType.Identifier -> {
                    var result = functionCall()
                    if (result != null) return result
                    inc()
                }
                peek().text in setOf("var", "val") -> varDecl()
                else -> {
                    println(peek())
                    TODO()
                }
            }
        }

        private fun inc(): Statement {
            val n = next().text
            next()
            return VariableDecl(n, Inc(n), true)
        }

        private fun varDecl(): Statement {
            val mutable = next().text == "var"
            val name = next().text
            next()
            val expr = expression.parse()
            if (next().type != TokenType.Semicolon) throwCE()
            return VariableDecl(name, expr, mutable)
        }

        private fun functionCall(): Statement? {
            val name = next().text
            if (peek().type != TokenType.LParen) return null
            next() // (
            val args = mutableListOf<Expression>()

            while (peek().type != TokenType.RParen) {
                if (peek().type == TokenType.Comma) next()
                args.add(expression.parse())
            }
            next() // )
            if (next().type != TokenType.Semicolon) throwCE("Expected ;, actually ${next().text}")
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
