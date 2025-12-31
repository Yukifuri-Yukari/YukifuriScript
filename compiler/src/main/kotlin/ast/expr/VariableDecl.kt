package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class VariableDecl(
    val name: String,
    val value: Expression,
    val mutable: Boolean,
) : Statement() {
    override fun accept(visitor: Visitor) {
        value.accept(visitor)
        visitor.context()[name] = visitor.getReturn()
    }

    override fun toString(): String {
        return "VariableDecl(name=$name, value=$value, mutable=$mutable)"
    }
}
