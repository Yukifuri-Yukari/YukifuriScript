package yukifuri.script.compiler.ast.structure

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.util.EnvironmentTable

class YFile(
    val module: Module,
    val table: EnvironmentTable
) {
    override fun toString(): String {
        return "File(module=$module, table=$table)"
    }

    fun iterator() = module.iterator()
}

