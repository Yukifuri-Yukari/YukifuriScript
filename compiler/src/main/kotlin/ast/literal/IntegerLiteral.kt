package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor

class IntegerLiteral(value: Int) : Literal<Int>(value) {

    override fun accept(visitor: Visitor) {
        visitor.visitLiteral(this)
    }

    override fun toString(): String {
        return "IntegerLiteral(value=$value)"
    }
}
