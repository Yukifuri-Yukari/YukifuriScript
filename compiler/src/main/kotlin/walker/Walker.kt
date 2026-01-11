package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Operator
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.expr.*
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
import yukifuri.script.compiler.walker.obj.*
import java.util.*
import kotlin.collections.plus
import kotlin.collections.toMutableMap
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
        name, args, returnType, Module.from(
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
            println(Serializer.deserialize(context["obj"]?.first?.toString() ?: throw Exception()))
        },
        builtin("print", listOf("obj" to "Any"), "Nothing") {
            print(Serializer.deserialize(context["obj"]?.first?.toString() ?: throw Exception()))
        },
        builtin("round", listOf("num" to "Number"), "float") {
            results.push(FloatNumber(
                round(
                    (context["num"]!!.first as NumberObject).toDouble()
                )
            ))
        }
    )

    val results = Stack<Object>()
    var context = mutableMapOf<String, Pair3<Object, String, Boolean>>()
    var interruptLoops = false

    val type2Raw = mapOf(
        "int" to Integer::class.java,
        "float" to FloatNumber::class.java,
        "String" to StringObject::class.java,
        "Boolean" to BooleanObject::class.java,
        "Any" to Object::class.java,
        "Function" to FunctionReference::class.java
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
        context[decl.name] = Pair3(FunctionReference(decl), "Function", false)
    }

    override fun functionCall(call: FunctionCall) {
        val function = functions[call.name] ?:
                       builtins[call.name] ?:
                       context[call.name]?.first as FunctionReference? ?:
                       throw Exception("Undefined symbol: ${call.name}")

        val func =
            function as? YFunction ?:
            if (function is FunctionReference) function.func
            else throw Exception("Illegal function reference")

        if (call.args.size != func.args.size) {
            throw Exception("Function requires: ${call.args.size} args, actually: ${call.args.size}")
        }
        val scope = mutableMapOf<String, Pair3<Object, String, Boolean>>()
        for ((i, arg) in call.args.withIndex()) {
            arg.accept(this)
            val signature = func.args[i]
            val value = results.pop()
            typeCheck(value, signature.second)
            scope[signature.first] = Pair3(value, signature.second, false)
        }
        val original = context
        context = (scope + context).toMutableMap()
        func.body.accept(this)
        context = original
    }

    override fun functionReturn(ret: Return) {
        ret.expr.accept(this)
        interruptLoops = true
    }

    override fun literal(literal: Literal<*>, type: Class<*>) {
        results.push(literal.toObject())
    }

    override fun binaryExpr(expr: BinaryExpr) {
        expr.l.accept(this)
        expr.r.accept(this)
        val r = results.pop()
        val l = results.pop()
        if (l is StringObject || r is StringObject) {
            results.push(when (expr.operator) {
                Operator.Add -> StringObject(l.toString() + r.toString())
                else -> throw Exception("Incapability operator for ${expr.operator}")
            })
            return
        }
        l as NumberObject; r as NumberObject
        results.push(when (expr.operator) {
            Operator.Add -> l.add(r)
            Operator.Sub -> l.sub(r)
            Operator.Mul -> l.mul(r)
            Operator.Lt -> l.compareLt(r)
            Operator.Eq -> l.compareEq(r)
            else -> TODO()
        })
    }

    override fun unaryExpr(expr: UnaryExpr) {
        expr.expr.accept(this)
        results.push(when (expr.operator) {
            Operator.Sub -> Integer(0).sub(results.pop() as NumberObject)
            else -> TODO()
        })
    }

    override fun getVariable(get: VariableGet) {
        results.push(context[get.name]!!.first)
    }

    override fun declareVariable(decl: VariableDecl) {
        decl.value.accept(this)
        val result = results.pop()
        context[decl.name] = Pair3(result,
            if (decl.type == "auto") raw2Type[result::class.java]!!
            else decl.type, decl.mutable)
    }

    override fun assignVariable(assign: VariableAssign) {
        val value = context[assign.name] ?: throw Exception("No such variable ${assign.name}")
        assign.value.accept(this)
        if (assign.operator == Operator.Assign)
            context[assign.name] = Pair3(results.pop(), value.second, value.third)
    }

    override fun condFor(loop: ConditionalFor) {
        interruptLoops = false
        val original = context.toMutableMap()
        loop.init.accept(this)
        var cond: Boolean
        while (true) {
            loop.cond.accept(this)
            if (results.peek() !is BooleanObject)
                throw Exception("Requires boolean, actually: ${results.peek().javaClass.simpleName}")
            cond = (results.pop() as BooleanObject).value
            if (!cond || interruptLoops) break
            loop.body.accept(this)
            loop.updater.accept(this)
        }
        context = original
    }

    override fun condJump(jump: ConditionalJump) {
        val original = context.toMutableMap()
        jump.cond.accept(this)
        if (results.peek() !is BooleanObject) {
            throw Exception("Requires boolean, actually: ${results.peek().javaClass.simpleName}")
        }
        if ((results.pop() as BooleanObject).value) {
            jump.ifBlock.accept(this)
        } else {
            jump.elseBlock?.accept(this)
        }
        context = original
    }

    fun exec() {
        file.module.accept(this)
        file.table.function("main")!!.body.accept(this)
    }
}