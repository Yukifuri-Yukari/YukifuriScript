package yukifuri.script.compiler.ast.function

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

open class YFunction(
    val name: String,
    val args: List<Pair<String, String>>,
    val returnType: String = "Nothing",
    val body: Module,
) : Statement() {
    override fun toString() = "Function(name=$name, args=$args, return=$returnType, body=$body)"

    fun data() = Data(name, args, returnType)

    override fun accept(visitor: Visitor) {
        visitor.functionStack().push(name)
        for (s in body) {
            s.accept(visitor)
        }
        visitor.functionStack().pop()
    }

    data class Data(
        val name: String,
        val args: List<Pair<String, String>>,
        val returnType: String
    ) {
        override fun toString(): String {
            return "Function(name=$name, args=$args, returnType=$returnType)"
        }
    }
}
