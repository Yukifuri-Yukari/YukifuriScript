package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.visitor.Visitor

class VariableGet(val name: String) : Expression() {
    override fun accept(visitor: Visitor) {
        visitor.setReturn(visitor.context()[name]?.first ?: throw Exception("No such variable: $name"))
    }

    override fun toString(): String {
        return "VariableGet(name='$name')"
    }
}
