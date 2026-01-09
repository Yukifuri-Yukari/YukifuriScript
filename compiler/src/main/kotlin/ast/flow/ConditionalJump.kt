package yukifuri.script.compiler.ast.flow

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class ConditionalJump(
    val cond: Expression,
    val ifBlock: Module,
    val elseBlock: Module? = null
) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.condJump(this)
    }

    override fun toString(): String {
        return "ConditionalJump(cond=$cond, ifBlock=$ifBlock, elseBlock=$elseBlock)"
    }
}
