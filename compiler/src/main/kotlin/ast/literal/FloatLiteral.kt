package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor

class FloatLiteral(value: Float) : Literal<Float>(value) {

    override fun toString(): String {
        return "FloatLiteral(value=$value)"
    }
}
