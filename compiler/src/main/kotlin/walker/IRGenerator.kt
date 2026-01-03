package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.VariableAssign
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.expr.VariableGet
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.visitor.Visitor
import java.util.Stack

class IRGenerator : Visitor {
    val stack = Stack<Any>()
    val code = mutableListOf<String>()

    override fun functionDecl(decl: YFunction) {
        TODO("Not yet implemented")
    }

    override fun functionCall(call: FunctionCall) {
        TODO("Not yet implemented")
    }

    override fun literal(literal: Literal<*>, type: Class<*>) {
        stack.push(literal.get())
    }

    override fun binaryExpr(expr: BinaryExpr) {
        val r = stack.pop()
        val l = stack.pop()

    }

    override fun getVariable(get: VariableGet) {
        TODO("Not yet implemented")
    }

    override fun declareVariable(decl: VariableDecl) {
        TODO("Not yet implemented")
    }

    override fun assignVariable(assign: VariableAssign) {
        TODO("Not yet implemented")
    }
}