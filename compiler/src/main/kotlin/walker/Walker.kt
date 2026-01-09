package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Operator
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.VariableAssign
import yukifuri.script.compiler.ast.expr.VariableDecl
import yukifuri.script.compiler.ast.expr.VariableGet
import yukifuri.script.compiler.ast.flow.ConditionalFor
import yukifuri.script.compiler.ast.flow.ConditionalJump
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.util.Pair3
import kotlin.math.round

class Walker(
    val file: YFile
) : Visitor {
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
    val builtins = mapOf(
        builtin("println", listOf("obj" to "Any"), "Nothing") {
            println(context["obj"]?.first ?: "null")
        },
        builtin("round", listOf("num" to "Any"), "float") {
            result = round((context["num"]!!.first as Number).toDouble())
        }
    )

    var result: Any = Unit
    var context = mutableMapOf<String, Pair3<Any, String, Boolean>>()


    override fun functionDecl(decl: YFunction) {
        if (decl.name in functions.keys)
            throw Exception("Multiple functions defined for ${decl.signature()}, ${functions.map { "${it.value.signature()}" }}")
        functions[decl.name] = decl
    }

    override fun functionCall(call: FunctionCall) {
        val func = functions[call.name] ?: builtins[call.name] ?: throw Exception("Undefined symbol: ${call.name}")
        if (call.args.size != func.args.size) {
            throw Exception("Function requires: ${call.args.size} args, actually: ${call.args.size}")
        }
        val scope = mutableMapOf<String, Pair3<Any, String, Boolean>>()
        for ((i, arg) in call.args.withIndex()) {
            arg.accept(this)
            val signature = func.args[i]
            val value = result
            when (signature.second) {
                "Any" -> {}
                "String" if value is String -> {}
                "float" if value is Double -> {}
                "int" if value is Int -> {}
                else -> throw Exception(
                    "Incapability type between ${
                        signature.second
                    } and ${
                        value.javaClass.simpleName
                    }")
            }
            scope[signature.first] = Pair3(result, signature.second, false)
        }
        val original = context
        context = (scope + context).toMutableMap()
        func.body.accept(this)
        context = original
    }

    override fun functionReturn(ret: Return) {
        ret.expr.accept(this)
    }

    override fun literal(literal: Literal<*>, type: Class<*>) {
        result = literal.get()!!
    }

    override fun binaryExpr(expr: BinaryExpr) {
        expr.l.accept(this)
        val l = result
        expr.r.accept(this)
        val r = result
        result = when (expr.operator) {
            Operator.Add -> {
                if (l is String || r is String) {
                    l.toString() + r.toString()
                } else
                    (l as Number).toDouble() + (r as Number).toDouble()
            }
            Operator.Mul -> (l as Number).toDouble() * (r as Number).toDouble()
            Operator.Lt -> (l as Number).toDouble() < (r as Number).toDouble()
            else -> TODO()
        }
    }

    override fun getVariable(get: VariableGet) {
        result = context[get.name]!!.first
    }

    override fun declareVariable(decl: VariableDecl) {
        decl.value.accept(this)
        context[decl.name] = Pair3(result, decl.type, decl.mutable)
    }

    override fun assignVariable(assign: VariableAssign) {
        val value = context[assign.name] ?: throw Exception("No such variable ${assign.name}")
        assign.value.accept(this)
        if (assign.operator == Operator.Assign)
            context[assign.name] = Pair3(result, value.second, value.third)
    }

    override fun condFor(loop: ConditionalFor) {
    }

    override fun condJump(jump: ConditionalJump) {
        jump.cond.accept(this)
        if (result !is Boolean) {
            throw Exception("Requires boolean, actually: ${result.javaClass.simpleName}")
        }
        if (result as Boolean) {
            jump.ifBlock.accept(this)
        } else {
            jump.elseBlock?.accept(this)
        }
    }

    fun exec() {
        file.module.accept(this)
        file.table.function("main")!!.body.accept(this)
    }
}