package yukifuri.script.compiler.walker

import yukifuri.script.compiler.ast.base.Module
import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.ast.visitor.Visitor

class Walker(val file: YFile) : Visitor {
    val builtin = mapOf(
        "println" to YFunction(
            "println", listOf("message" to "String"), "Nothing",
            Module(listOf(object : Statement() {

                override fun accept(visitor: Visitor) {
                    // 直接从visitor获取上下文并打印message参数
                    val context = visitor.context()
                    val message = context["message"]
                    println(message)
                }
            }))
        )
    )

    var context = mapOf<String, Any?>()

    fun exec() {
        if (file.table.function("main") == null) {
            throw Exception("No main function")
        }
        val main = file.table.function("main")!!
        main.accept(this)
    }

    override fun visitFunctionCall(call: FunctionCall) {
        if ((call.args.map { it is Literal<*> }).contains(false)) {
            throw Exception("Arguments must be literal now")
        }
        val func = file.table.function(call.name) ?: (
                builtin[call.name] ?: throw Exception("No such function: ${call.name}")
                )
        val args = mutableMapOf<String, Any?>()

        for ((i, arg) in call.args.withIndex()) {
            args[func.args[i].first] = (arg as Literal<*>).get()
        }

        // 保存当前上下文
        val oldContext = context
        // 新上下文：参数覆盖当前上下文中的同名变量
        context = args + oldContext

        func.accept(this)

        // 恢复原始上下文
        context = oldContext
    }

    override fun context() = context
}
