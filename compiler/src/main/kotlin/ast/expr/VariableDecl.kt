package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class VariableDecl(
    val name: String,
    val type: String = "auto",
    val value: Expression,
    val mutable: Boolean,
) : Statement() {
    override fun accept(visitor: Visitor) {
        value.accept(visitor)
        if (visitor.context()[name] != null) {
            throw Exception("Redeclaration of variable $name")
        }
        visitor.context()[name] = visitor.getReturn() to mutable
    }

    override fun toString(): String {
        return "VariableDecl(name=$name, type=$type, value=$value, mutable=$mutable)"
    }
}
