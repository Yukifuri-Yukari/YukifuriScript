package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.walker.obj.FloatNumber
import yukifuri.script.compiler.walker.obj.Object

class FloatLiteral(value: Double) : Literal<Double>(value) {
    override fun toString(): String {
        return "FloatLiteral(value=$value)"
    }

    override fun accept(visitor: Visitor) {
        visitor.literal(this, Double::class.java)
    }

    override fun toObject(): Object {
        return FloatNumber(value)
    }
}
