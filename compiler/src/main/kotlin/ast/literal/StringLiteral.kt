package yukifuri.script.compiler.ast.literal

import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.walker.obj.Object
import yukifuri.script.compiler.walker.obj.StringObject

class StringLiteral(value: String) : Literal<String>(value) {
    override fun toString(): String {
        return "StringLiteral($value)"
    }

    override fun accept(visitor: Visitor) {
        visitor.literal(this, String::class.java)
    }

    override fun toObject(): Object {
        return StringObject(value)
    }
}
