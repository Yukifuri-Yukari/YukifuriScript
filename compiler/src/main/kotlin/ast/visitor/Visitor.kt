package yukifuri.script.compiler.ast.visitor

import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.VariableAssign
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.expr.VariableGet
import yukifuri.script.compiler.ast.flow.ConditionalFor
import yukifuri.script.compiler.ast.flow.ConditionalJump
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.Literal

interface Visitor {
    fun functionDecl(decl: YFunction)
    fun functionCall(call: FunctionCall)
    fun functionReturn(ret: Return)

    fun literal(literal: Literal<*>, type: Class<*>)
    fun binaryExpr(expr: BinaryExpr)

    fun getVariable(get: VariableGet)
    fun declareVariable(decl: VariableDecl)
    fun assignVariable(assign: VariableAssign)

    fun condFor(loop: ConditionalFor)

    fun condJump(jump: ConditionalJump)
}
