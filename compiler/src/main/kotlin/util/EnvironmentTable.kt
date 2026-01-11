package yukifuri.script.compiler.util

import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.walker.obj.Object

class EnvironmentTable {
    private val functions = mutableMapOf<String, YFunction>()
    private val variables = mutableMapOf<String, VariableDecl>()

    fun function(func: YFunction) {
        functions[func.name] = func
    }

    fun function(name: String): YFunction? {
        return functions[name]
    }

    fun variable(value: VariableDecl) {
        variables[value.name] = value
    }

    fun variable(name: String): VariableDecl? {
        return variables[name]
    }

    override fun toString(): String {
        return "EnvironmentTable(functions=${functions.map { it.value.signature() }}, variables=$variables)"
    }
}
