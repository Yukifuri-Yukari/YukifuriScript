package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.VariableAssign
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.expr.VariableGet
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.Return
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

    fun exec() {
        file.module.forEach {
            it.accept(this)
            if (builder.isNotEmpty()) {
                code.add(builder.toString())
                builder.clear()
            }
        }
    }

    override fun functionDecl(decl: YFunction) {
        code.add("fun ${decl.name}(${decl.args.joinToString(", ") { "${it.first}: ${it.second}" }}): ${decl.returnType} {")
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

    override fun functionReturn(ret: Return) {
        builder.append("return ")
        ret.expr.accept(this)
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
        builder.append("va${
            if (decl.mutable) "r" else "l"
        } ${decl.name}: ${decl.type} = ")
        decl.value.accept(this)
    }

    override fun assignVariable(assign: VariableAssign) {
        code.add("${assign.name} ${assign.operator.op} ")
        assign.value.accept(this)
        code[code.size - 1] += builder.toString()
        builder.clear()
    }

    fun use(func: (IRGenerator) -> Unit) {
        func(this)
    }
}