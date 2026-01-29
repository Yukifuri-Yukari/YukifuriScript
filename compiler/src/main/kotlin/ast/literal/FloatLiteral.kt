package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.visitor.walker.obj.FloatNumber
import yukifuri.script.compiler.visitor.walker.obj.Object

class FloatLiteral(value: Double) : Literal<Double>(value) {
    override fun toString(): String {
        return "FloatLiteral(value=$value)"
    }

    override fun accept(visitor: Visitor) {
        visitor.literal(this)
    }

    override fun toObject(): Object {
        return FloatNumber(value)
    }
}
