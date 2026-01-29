package yukifuri.script.compiler.visitor.walker.obj

import yukifuri.script.compiler.ast.function.YFunction

class FunctionReference(
    val func: YFunction
) : Object() {
    override fun toString(): String {
        return func.signature().toString()
    }
}
