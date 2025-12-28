package yukifuri.script.compiler.ast.function

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class FunctionCall(
    val name: String,
    val args: List<Expression>
) : Statement() {
    override fun toString(): String {
        return "FunctionCall(name='$name', args=$args)"
    }

    override fun accept(visitor: Visitor) {
        visitor.visitFunctionCall(this)
    }
}
