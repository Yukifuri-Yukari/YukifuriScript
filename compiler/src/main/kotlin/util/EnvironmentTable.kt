package yukifuri.script.compiler.util

import yukifuri.script.compiler.ast.function.YFunction

class EnvironmentTable {
    private val functions = mutableMapOf<String, YFunction>()

    fun function(func: YFunction) {
        functions[func.name] = func
    }

    fun function(name: String): YFunction? {
        return functions[name]
    }

    fun functions(): Map<String, YFunction> = functions

    override fun toString(): String {
        return "EnvironmentTable(functions=${functions.map { it.value.signature() }})"
    }
}
