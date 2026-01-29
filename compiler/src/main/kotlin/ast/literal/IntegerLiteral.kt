package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.visitor.walker.obj.Integer
import yukifuri.script.compiler.visitor.walker.obj.Object

class IntegerLiteral(value: Int) : Literal<Int>(value) {
    override fun toString(): String {
        return "IntegerLiteral(value=$value)"
    }

    override fun accept(visitor: Visitor) {
        visitor.literal(this)
    }

    override fun toObject(): Object {
        return Integer(value)
    }
}
