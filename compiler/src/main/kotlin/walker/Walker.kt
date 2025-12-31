package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.FloatLiteral
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.literal.StringLiteral
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.ast.visitor.Visitor
import java.util.Stack

class Walker(val file: YFile) : Visitor {
    private fun simpleFunction(
        name: String,
        args: List<Pair<String, String>>,
        returnType: String = "Nothing",
        body: (Visitor) -> Unit
    ): Pair<String, YFunction> = name to YFunction(
        name, args, returnType,
        Module(listOf(object : Statement() {
            override fun accept(visitor: Visitor) {
                body(visitor)
            }
        }))
    )

    val functionStack = Stack<String>()
    val builtin = mapOf(
        simpleFunction(
            "println", listOf("message" to "String"), "Nothing",
            { visitor -> println(visitor.context()["message"]!!) }
        ),
        simpleFunction(
            "print", listOf("message" to "String"), "Nothing",
            { visitor -> print(visitor.context()["message"]!!) }
        ),
    )

    var context = mutableMapOf<String, Any?>()

    fun exec() {
        if (file.table.function("main") == null) {
            throw Exception("No main function")
        }
        val main = file.table.function("main")!!
        main.accept(this)
    }

    override fun visitFunctionCall(call: FunctionCall) {
        val func = file.table.function(call.name) ?: (
                builtin[call.name] ?: throw Exception("No such function: ${call.name}")
                )
        if (functionStack.size >= 400) {
            println("Stack overflow")
            throw StackOverflowError()
        }
        val args = mutableMapOf<String, Any?>()

        for ((i, arg) in call.args.withIndex()) {
            arg.accept(this)
            args[func.args[i].first] = getReturn()
        }

        // 保存当前上下文
        val oldContext = context
        // 新上下文：参数覆盖当前上下文中的同名变量
        context = args
        func.accept(this)

        // 恢复原始上下文
        context = oldContext
    }

    var result: Any? = null

    override fun setReturn(obj: Any?) {
        result = obj
    }

    override fun getReturn() = result

    override fun context() = context
    override fun functionStack() = functionStack
}
