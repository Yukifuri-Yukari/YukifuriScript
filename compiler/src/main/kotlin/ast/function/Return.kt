package yukifuri.script.compiler.ast.function

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class Return(
    val expr: Expression
) : Statement() {
    override fun accept(visitor: Visitor) {
        expr.accept(visitor)
    }
}