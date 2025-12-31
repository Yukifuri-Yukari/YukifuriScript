package yukifuri.script.compiler.ast.expr

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class Inc(val name: String) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.context()[name] = visitor.context()[name]!!.toString().toInt() + 1
    }
}