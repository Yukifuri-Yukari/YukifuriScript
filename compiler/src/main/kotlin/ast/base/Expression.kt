package yukifuri.script.compiler.ast.base

import yukifuri.script.compiler.ast.visitor.Visitor

abstract class Expression {
    abstract fun accept(visitor: Visitor)
}
