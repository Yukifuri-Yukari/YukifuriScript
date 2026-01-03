// language: kotlin
package yukifuri.script.compiler.parser

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.exception.Diagnostic
import yukifuri.script.compiler.exception.Diagnostic.Level
import yukifuri.script.compiler.exception.Diagnostics
import yukifuri.script.compiler.exception.throwCE
import yukifuri.script.compiler.exception.throwEOF
import yukifuri.script.compiler.lexer.token.TokenStream
import yukifuri.script.compiler.lexer.token.TokenType
import yukifuri.script.compiler.util.EnvironmentTable

class Parser(
    val ts: TokenStream,
    val diagnostics: Diagnostics
) {

    fun next() = ts.next()
    fun peek() = ts.peek()
    fun addDiagnostic(message: String, level: Level = Level.Error) {
        diagnostics.add(Diagnostic.of(peek().row, peek().column, message, level))
    }
    fun addAndError(message: String) {
        addDiagnostic(message)
        throwCE(message)
    }

    val topLevel = TopLevelParser(this)
    val stmt = StatementParser(this)
    val expr = ExpressionParser(this)

    private val builder = Module.ModuleBuilder()

    private var table = EnvironmentTable()

    private lateinit var file: YFile

    fun table() = table
    fun file() = file

    fun module(): Module {
        val builder = Module.ModuleBuilder()
        if (peek().type != TokenType.LBrace) {
            addDiagnostic("Expected {, actually ${peek().text}")
            throwCE("Expected {, actually ${peek().text}")
        }
        next()
        while (peek().type != TokenType.RBrace) {
            if (peek().type == TokenType.EOF) throwEOF("Unexpected EOF")
            builder.add(stmt.parse())
        }
        next()
        return builder.build()
    }

    fun parse() {
        builder.add(topLevel.parse())
        file = YFile(builder.build(), table)
    }
}