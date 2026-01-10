package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Operator
import yukifuri.script.compiler.ast.visitor.Visitor

class UnaryExpr(
    val operator: Operator,
    val expr: Expression
) : Expression() {
    override fun accept(visitor: Visitor) {
        visitor.unaryExpr(this)
    }
}
