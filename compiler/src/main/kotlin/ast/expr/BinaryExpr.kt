package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Operator
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.visitor.Visitor

class BinaryExpr(
    val operator: Operator,
    val l: Expression,
    val r: Expression
) : Expression() {
    override fun toString(): String {
        return "BinaryExpr(l=$l, operator=${operator.name}, r=$r)"
    }

    override fun accept(visitor: Visitor) {
        l.accept(visitor)
        val left = visitor.getReturn() as Int
        r.accept(visitor)
        val right = visitor.getReturn() as Int
        // Todo: Operation for other types
        visitor.setReturn(when (operator) {
            Operator.Add -> left + right
            Operator.Sub -> left - right
            Operator.Mul -> left * right
            Operator.Div -> left / right
            else -> TODO()
        })
    }
}