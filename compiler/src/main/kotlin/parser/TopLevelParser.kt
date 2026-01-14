package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.util.EnvironmentTable

class TopLevelParser(
    self: Parser
) : SubParser(self) {
    fun parse(): Module.ModuleBuilder {
        val builder = Module.ModuleBuilder()
        while (peek().type != TokenType.EOF) {
            builder.add(once())
        }
        return builder
    }

    fun once(): Statement {
        return when {
            peek() == TokenType.Keyword to "function" -> {
                val function = functionDeclaration()
                function
            }
            peek().type == TokenType.Keyword && peek().text in setOf("val", "var")
                -> stmt.variableDeclaration()
            else -> {
                addAndError("Unknown symbol found: ${next().text}")
                throw Exception()
            }
        }
    }

    fun functionDeclaration(): YFunction {
        next() // function
        val name = next().text
        if (peek().type != TokenType.LParen)
            addAndError("Expected (, actually ${peek().text}")
        next() // (
        val params = mutableListOf<Pair<String, String>>()

        if (peek().type == TokenType.Comma)
            addAndError("Expected identifier, actually ${peek().text}")

        while (peek().type != TokenType.RParen) {
            if (peek().type == TokenType.Comma)
                next() // ,
            if (peek().type != TokenType.Identifier)
                addAndError("Expected identifier, actually ${peek()}")
            val name = next().text
            if (peek().type != TokenType.Colon)
                addAndError("Expected :, actually ${peek().text}")
            next() // :
            val type = next().text
            params.add(name to type)
        } // Argument collection
        next() // )

        val returnType = if (peek().type == TokenType.Colon) {
            next() // :
            if (peek().type != TokenType.Identifier)
                addAndError("Expected identifier, actually ${peek().text}")
            next().text
        } else "Nothing"

        if (peek().type != TokenType.LBrace)
            addAndError("Expected {, actually ${peek().text}")

        val body = self.module()

        return YFunction(
            name, params, returnType, body
        ).also { self.table().function(it) }
    }
}