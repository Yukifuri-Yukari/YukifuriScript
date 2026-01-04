package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Operator
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class VariableAssign(
    val operator: Operator,
    val name: String,
    val value: Expression,
) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.assignVariable(this)
    }

    override fun toString(): String {
        return "VariableAssign(operator=$operator, name='$name', value=$value)"
    }
}
