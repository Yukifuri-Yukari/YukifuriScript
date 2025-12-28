package yukifuri.script.compiler.ast.visitor

import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.function.FunctionCall

interface Visitor {
    fun visitStmt(stmt: Statement) {
        when (stmt) {
            is FunctionCall -> visitFunctionCall(stmt)
            else -> throw Exception("Unknown stmt type: ${stmt::class.simpleName}")
        }
    }

    fun visitFunctionCall(call: FunctionCall)

    fun context(): Map<String, Any?>
}
