package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.walker.obj.Integer
import yukifuri.script.compiler.walker.obj.Object

class IntegerLiteral(value: Int) : Literal<Int>(value) {
    override fun toString(): String {
        return "IntegerLiteral(value=$value)"
    }

    override fun accept(visitor: Visitor) {
        visitor.literal(this, Int::class.java)
    }

    override fun toObject(): Object {
        return Integer(value)
    }
}
