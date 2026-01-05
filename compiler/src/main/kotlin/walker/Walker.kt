package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.VariableAssign
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.expr.VariableGet
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.visitor.Visitor

class Walker : Visitor {
    fun builtin(
        name: String,
        args: List<Pair<String, String>>,
        returnType: String = "Nothing",
        body: (Visitor) -> Unit
    ): Pair<String, YFunction> = name to YFunction(
        name, args, returnType, Module(
            listOf(
        object : Statement() {
            override fun accept(visitor: Visitor) {
                body(visitor)
            }
        }
    )))

    val functions = mutableMapOf<String, YFunction>()
    val builtins = mutableMapOf(
        builtin("println", listOf("obj" to "String"), "Nothing") {
        }
    )

    var result: Any = Unit


    override fun functionDecl(decl: YFunction) {
        if (decl.name in functions.keys)
            throw Exception("Multiple functions defined for ${decl.signature()}")
        functions[decl.name] = decl
    }

    override fun functionCall(call: FunctionCall) {
        if (call.name !in functions.keys) {
            throw Exception("Undefined symbol: ${call.name}")
        }
        val func = functions[call.name]!!
        if (call.args.size != func.args.size) {
            throw Exception("Function requires: ${call.args.size} args, actually: ${call.args.size}")
        }
        val scope = mutableMapOf<String, Any>()
        for ((i, arg) in call.args.withIndex()) {
            arg.accept(this)
            scope[func.args[i].first] = result
        }
    }

    override fun functionReturn(ret: Return) {
        TODO("Not yet implemented")
    }

    override fun literal(literal: Literal<*>, type: Class<*>) {
        TODO("Not yet implemented")
    }

    override fun binaryExpr(expr: BinaryExpr) {
        TODO("Not yet implemented")
    }

    override fun getVariable(get: VariableGet) {
        TODO("Not yet implemented")
    }

    override fun declareVariable(decl: VariableDecl) {
        TODO("Not yet implemented")
    }

    override fun assignVariable(assign: VariableAssign) {
        TODO("Not yet implemented")
    }
}