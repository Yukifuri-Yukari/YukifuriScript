package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.visitor.walker.obj.BooleanObject
import yukifuri.script.compiler.visitor.walker.obj.Object

class BooleanLiteral(value: Boolean) : Literal<Boolean>(value) {
    override fun toObject(): Object {
        return BooleanObject(value)
    }

    override fun accept(visitor: Visitor) {
        visitor.literal(this)
    }

    override fun toString(): String {
        return "BooleanLiteral($value)"
    }
}
