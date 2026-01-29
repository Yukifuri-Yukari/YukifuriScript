package yukifuri.script.compiler.visitor.bcgen.entry.symbol

import yukifuri.script.compiler.ast.function.YFunction

enum class SymbolType {
    Variable,
    Function,
    Class, // Reserved
}

abstract class Symbol {
    abstract val name: String
    protected abstract val symbolType: SymbolType
    var used = false

    fun assert(type: SymbolType) {
        if (this.symbolType != type) {
            throw IllegalArgumentException("Symbol $name is not a $type")
        }
    }
}

class VariableSym(
    override val name: String,
    val mutable: Boolean,
    val type: String
): Symbol() {
    override val symbolType = SymbolType.Variable

    override fun toString(): String {
        return "VariableSym(name=\"$name\", mutable=$mutable, symType=$symbolType, type=\"$type\", used=$used)"
    }
}

class FunctionSym(
    override val name: String,
    val retType: String,
    val function: YFunction,
): Symbol() {
    override val symbolType = SymbolType.Function

    override fun toString(): String {
        return "FunctionSym(name=\"$name\", symType=$symbolType, type=\"$retType\", used=$used)"
    }
}
