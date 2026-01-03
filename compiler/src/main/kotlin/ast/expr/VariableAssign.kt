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
        if (visitor.context()[name] == null) {
            throw Exception("No such variable: $name")
        }
        if (!visitor.context()[name]!!.second) {
            throw Exception("Cannot assign to constant: $name")
        }
        value.accept(visitor)
        val expr = visitor.getReturn()
        when (operator) {
            Operator.Assign -> visitor.context()[name] = expr to true
            else -> TODO()
        }
    }

    override fun toString(): String {
        return "VariableAssign(operator=$operator, name='$name', value=$value)"
    }
}
