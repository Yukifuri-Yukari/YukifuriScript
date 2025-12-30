package yukifuri.script.compiler.ast.visitor

import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.literal.FloatLiteral
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.literal.StringLiteral
import java.util.Stack

interface Visitor {
    fun visitStmt(stmt: Statement) {
        when (stmt) {
            is FunctionCall -> visitFunctionCall(stmt)
            else -> throw Exception("Unknown stmt type: ${stmt::class.simpleName}")
        }
    }

    fun visitFunctionCall(call: FunctionCall)

    fun visitLiteral(lit: StringLiteral)
    fun visitLiteral(lit: IntegerLiteral)
    fun visitLiteral(lit: FloatLiteral)

    fun context(): Map<String, Any?>
    fun functionStack(): Stack<String>
}
