package yukifuri.script.compiler.visitor.bcgen.entry.symbol

class SymbolTable {
    val global = SymbolScope()
    var root = global

    fun enter() {
        val scope = SymbolScope(root)
        root = scope
    }

    fun exit() {
        root.parent?.children?.add(root)
        root = root.parent ?: global
    }

    fun add(symbol: Symbol) {
        root.add(symbol)
    }

    override fun toString(): String {
        return "SymbolTable(symbols=$global)"
    }

    class SymbolScope(
        val parent: SymbolScope? = null,
    ) {
        val children: MutableList<SymbolScope> = mutableListOf()
        val symbols = mutableMapOf<String, Symbol>()

        fun add(symbol: Symbol) {
            symbols[symbol.name] = symbol
        }

        fun findInSelf(name: String): Symbol? {
            return symbols[name]
        }

        fun findInParent(name: String): Symbol? {
            return parent?.find(name)
        }

        fun find(name: String): Symbol? {
            return findInSelf(name) ?: findInParent(name)
        }

        override fun toString(): String {
            return "SymbolScope(symbols=$symbols, children=$children)"
        }
    }
}