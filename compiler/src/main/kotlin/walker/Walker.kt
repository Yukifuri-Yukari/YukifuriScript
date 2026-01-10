package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Operator
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.BinaryExpr
import yukifuri.script.compiler.ast.expr.UnaryExpr
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
import yukifuri.script.compiler.util.Serializer
import yukifuri.script.compiler.walker.obj.BooleanObject
import yukifuri.script.compiler.walker.obj.FloatNumber
import yukifuri.script.compiler.walker.obj.Integer
import yukifuri.script.compiler.walker.obj.NumberObject
import yukifuri.script.compiler.walker.obj.Object
import yukifuri.script.compiler.walker.obj.StringObject
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
            println(Serializer.deserialize(context["obj"]?.first?.toString() ?: "null"))
        },
        builtin("print", listOf("obj" to "Any"), "Nothing") {
            print(Serializer.deserialize(context["obj"]?.first?.toString() ?: "null"))
        },
        builtin("sleep", listOf("ms" to "int"), "Nothing") {
            Thread.sleep((context["ms"]!!.first as NumberObject).toLong())
        },
        builtin("round", listOf("num" to "Number"), "float") {
            result = FloatNumber(
                round(
                    (context["num"]!!.first as NumberObject).toDouble()
                )
            )
        }
    )

    var result: Object = Object.Null
    var context = mutableMapOf<String, Pair3<Object, String, Boolean>>()

    val type2Raw = mapOf(
        "int" to Integer::class.java,
        "float" to FloatNumber::class.java,
        "String" to StringObject::class.java,
        "Boolean" to BooleanObject::class.java,
        "Any" to Object::class.java
    )

    val raw2Type = type2Raw.entries.associate { it.value to it.key }

    fun typeCheck(value: Object, excepted: String) {
        for (type in type2Raw.keys) {
            if (type2Raw[type]!! == value::class.java) {
                return
            }
        }

        throw Exception(
            "Incapability type between $excepted and ${
                raw2Type[value::class.java] ?: "NO_TYPE_FOUND"
            }"
        )
    }


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
        val scope = mutableMapOf<String, Pair3<Object, String, Boolean>>()
        for ((i, arg) in call.args.withIndex()) {
            arg.accept(this)
            val signature = func.args[i]
            val value = result
            typeCheck(value, signature.second)
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
        result = literal.toObject()
    }

    override fun binaryExpr(expr: BinaryExpr) {
        expr.l.accept(this)
        val l = result
        expr.r.accept(this)
        val r = result
        if (l is StringObject || r is StringObject) {
            result = when (expr.operator) {
                Operator.Add -> StringObject(l.toString() + r.toString())
                else -> throw Exception("Incapability operator for ${expr.operator}")
            }
            return
        }
        l as NumberObject; r as NumberObject
        result = when (expr.operator) {
            Operator.Add -> l.add(r)
            Operator.Sub -> l.sub(r)
            Operator.Mul -> l.mul(r)
            Operator.Lt -> l.compareLt(r)
            else -> TODO()
        }
    }

    override fun unaryExpr(expr: UnaryExpr) {
        expr.expr.accept(this)
        result = when (expr.operator) {
            Operator.Sub -> Integer(0).sub(result as NumberObject)
            else -> TODO()
        }
    }

    override fun getVariable(get: VariableGet) {
        result = context[get.name]!!.first
    }

    override fun declareVariable(decl: VariableDecl) {
        decl.value.accept(this)
        context[decl.name] = Pair3(result,
            if (decl.type == "auto") raw2Type[result::class.java]!!
            else decl.type, decl.mutable)
    }

    override fun assignVariable(assign: VariableAssign) {
        val value = context[assign.name] ?: throw Exception("No such variable ${assign.name}")
        assign.value.accept(this)
        if (assign.operator == Operator.Assign)
            context[assign.name] = Pair3(result, value.second, value.third)
    }

    override fun condFor(loop: ConditionalFor) {
        loop.init.accept(this)
        var cond: Boolean
        while (true) {
            loop.cond.accept(this)
            if (result !is BooleanObject)
                throw Exception("Requires boolean, actually: ${result.javaClass.simpleName}")
            cond = (result as BooleanObject).value
            if (!cond) break
            loop.body.accept(this)
            loop.updater.accept(this)
        }
    }

    override fun condJump(jump: ConditionalJump) {
        jump.cond.accept(this)
        if (result !is BooleanObject) {
            throw Exception("Requires boolean, actually: ${result.javaClass.simpleName}")
        }
        if ((result as BooleanObject).value) {
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