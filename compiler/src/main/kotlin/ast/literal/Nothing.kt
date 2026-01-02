package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.visitor.Visitor

object Nothing : Expression() {
    override fun accept(visitor: Visitor) {
    }
}
