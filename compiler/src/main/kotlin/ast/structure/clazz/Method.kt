package yukifuri.script.compiler.ast.structure.clazz

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.function.YFunction

class Method(
    name: String,
    args: List<Pair<String, String>>,
    returnType: String = "Nothing",
    body: Module
) : YFunction(name, args, returnType, body)