package yukifuri.script.compiler.ast.base

import yukifuri.script.compiler.ast.visitor.Visitor

open class Module(
    private val statements: MutableList<Statement>
) {
    class ModuleBuilder {
        private val statements = mutableListOf<Statement>()

        fun add(statement: Statement): ModuleBuilder {
            statements.add(statement)
            return this
        }

        fun add(moduleBuilder: ModuleBuilder): ModuleBuilder {
            statements.addAll(moduleBuilder.statements)
            return this
        }

        fun add(collection: Collection<Statement>): ModuleBuilder {
            statements.addAll(collection)
            return this
        }

        fun build(): Module {
            return Module(statements)
        }
    }

    override fun toString(): String {
        return "Module(statements=$statements)"
    }

    open fun accept(visitor: Visitor) {
        for (statement in statements) {
            statement.accept(visitor)
        }
    }

    operator fun iterator(): Iterator<Statement> {
        return statements.iterator()
    }

    fun extend(vararg stmt: Statement) {
        statements.addAll(stmt)
    }

    fun list(): List<Statement> {
        return statements
    }

    companion object {
        fun from(list: List<Statement>) = Module(list.toMutableList())
    }
}
