package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.visitor.Visitor

abstract class Literal<T>(protected val value: T) : Expression() {
    fun get() = value
}
