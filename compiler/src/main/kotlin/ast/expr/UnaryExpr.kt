package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Operator
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.exception.throwCE

class UnaryExpr(
    val operator: Operator,
    val expr: Expression
) : Expression() {
    override fun accept(visitor: Visitor) {
        expr.accept(visitor)
        val v = visitor.getReturn() as Int
        when (operator) {
            Operator.Inc -> visitor.setReturn(v + 1)
            Operator.Dec -> visitor.setReturn(v - 1)
            else -> throwCE("Invalid operator for unary expression")
        }
    }

    fun asStatement() = UnaryStmt(this)

    override fun toString(): String {
        return "UnaryExpr(operator=$operator, expr=$expr)"
    }

    class UnaryStmt(val unary: UnaryExpr) : Statement() {
        override fun accept(visitor: Visitor) {
            unary.accept(visitor)
        }

        override fun toString(): String {
            return unary.toString()
        }
    }
}
