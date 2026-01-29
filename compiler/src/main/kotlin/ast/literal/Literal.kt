package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.visitor.walker.obj.Object

abstract class Literal<T>(protected val value: T) : Statement() {
    fun get() = value

    abstract fun toObject(): Object
}
