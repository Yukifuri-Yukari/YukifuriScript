package yukifuri.script.compiler.ast.visitor

import yukifuri.script.compiler.ast.base.Statement
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.literal.FloatLiteral
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.literal.StringLiteral
import java.util.Stack

interface Visitor {
    fun visitFunctionCall(call: FunctionCall)

    fun setReturn(obj: Any?)
    fun getReturn(): Any?

    fun context(): MutableMap<String, Any?>
    fun functionStack(): Stack<String>
}
