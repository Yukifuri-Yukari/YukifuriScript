package yukifuri.script.compiler.ast.function

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.visitor.Visitor

class YFunction(
    val name: String,
    val args: List<Pair<String, String>>,
    val returnType: String = "Nothing",
    val body: Module,
) : Statement() {
    override fun toString() = "Function(name=$name, args=$args, return=$returnType, body=$body)"

    fun data() = Data(name, args, returnType)

    override fun accept(visitor: Visitor) {
        for (s in body) {
            s.accept(visitor)
        }
    }

    data class Data(
        val name: String,
        val args: List<Pair<String, String>>,
        val returnType: String
    ) {
        override fun toString(): String {
            return "Data(name=$name, args=$args, returnType=$returnType)"
        }
    }
}
