package yukifuri.script.compiler.ast.flow

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class ConditionalFor(
    val init: Statement,
    val cond: Expression,
    val updater: Statement,
    val body: Module
) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.condFor(this)
    }

    override fun toString(): String {
        return "ConditionalFor(init=$init, cond=$cond, updater=$updater, body=$body)"
    }
}