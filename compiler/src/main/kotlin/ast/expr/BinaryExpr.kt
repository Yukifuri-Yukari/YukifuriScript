package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.visitor.Visitor

class BinaryExpr(
    val l: Expression,
    val operator: String,
    val r: Expression
) : Expression() {
    override fun toString(): String {
        return "BinaryExpr(l=$l, operator='$operator', r=$r)"
    }

    override fun accept(visitor: Visitor) {
        l.accept(visitor)
        r.accept(visitor)
    }
}