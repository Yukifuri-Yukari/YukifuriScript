package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor

class IntegerLiteral(value: Int) : Literal<Int>(value) {
    override fun toString(): String {
        return "IntegerLiteral(value=$value)"
    }

    override fun accept(visitor: Visitor) {
        visitor.literal(this, Int::class.java)
    }
}
