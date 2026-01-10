package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.walker.obj.NumberObject
import yukifuri.script.compiler.walker.obj.Object

abstract class Literal<T>(protected val value: T) : Expression() {
    fun get() = value

    abstract fun toObject(): Object
}
