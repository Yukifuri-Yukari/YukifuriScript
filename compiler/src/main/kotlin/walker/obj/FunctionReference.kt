package yukifuri.script.compiler.walker.obj

import yukifuri.script.compiler.ast.function.YFunction

class FunctionReference(
    val func: YFunction
) : Object() {
    override fun toString(): String {
        return func.signature().toString()
    }
}
