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
        visitor.binaryExpr(this)
    }
}