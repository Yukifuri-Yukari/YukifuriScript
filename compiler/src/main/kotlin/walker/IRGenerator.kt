package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.VariableAssign
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.expr.VariableGet
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.ast.visitor.Visitor

class IRGenerator(
    val file: YFile
) : Visitor {
    var locals = 0
    val builder = StringBuilder()
    val code = mutableListOf<String>()
    val global = mutableListOf<String>()
    var inGlobal = true

    fun exec() {
        file.module.forEach {
            it.accept(this)
            if (builder.isNotEmpty()) {
                global.add(builder.toString())
                builder.clear()
            }
        }
    }

    override fun functionDecl(decl: YFunction) {
        code.add("func ${decl.name}(${decl.args.joinToString(", ") { "${it.first}: ${it.second}" }}) {")
        for (stmt in decl.body) {
            stmt.accept(this)
            code.add("    $builder")
            builder.clear()
        }
        code.add("}")
    }

    override fun functionCall(call: FunctionCall) {
        builder.append("${call.name}(")
        for ((i, expr) in call.args.withIndex()) {
            expr.accept(this)
            if (i < call.args.lastIndex)
                builder.append(", ")
        }
        builder.append(")")
    }

    override fun literal(literal: Literal<*>, type: Class<*>) {
        builder.append(
            if (literal.get()::class.java == String::class.java)
                "\"${literal.get()}\""
            else literal.get()
        )
    }

    override fun binaryExpr(expr: BinaryExpr) {
        expr.l.accept(this)
        builder.append(" ${expr.operator} ")
        expr.r.accept(this)
    }

    override fun getVariable(get: VariableGet) {
        builder.append(get.name)
    }

    override fun declareVariable(decl: VariableDecl) {
        builder.append("variable ${
            if (decl.mutable) "mutable" else "const"
        } ${decl.name}: ${decl.type} = ")
        decl.value.accept(this)
    }

    override fun assignVariable(assign: VariableAssign) {
    }

    fun use(func: (IRGenerator) -> Unit) {
        func(this)
    }
}