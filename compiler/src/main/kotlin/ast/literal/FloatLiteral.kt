package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor

class FloatLiteral(value: Float) : Literal<Float>(value) {
    override fun accept(visitor: Visitor) {
        visitor.visitLiteral(this)
    }

    override fun toString(): String {
        return "FloatLiteral(value=$value)"
    }
}
