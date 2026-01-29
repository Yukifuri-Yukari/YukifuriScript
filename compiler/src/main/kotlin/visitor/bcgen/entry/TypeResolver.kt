package yukifuri.script.compiler.visitor.bcgen.entry

import yukifuri.script.compiler.ast.base.Expression
import yukifuri.script.compiler.ast.base.Operator
import yukifuri.script.compiler.ast.expr.*
import yukifuri.script.compiler.ast.flow.ConditionalFor
import yukifuri.script.compiler.ast.flow.ConditionalJump
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.BooleanLiteral
import yukifuri.script.compiler.ast.literal.FloatLiteral
import yukifuri.script.compiler.ast.literal.IntegerLiteral
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.literal.StringLiteral
import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.FunctionSym
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.SymbolTable
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.SymbolType
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.VariableSym
import java.util.*

class TypeResolver(
    val table: SymbolTable
) : Visitor {
    val queue = Stack<String>()

    val priority = mapOf(
        "int" to 0,
        "float" to 1,
        "bool" to 2,
        "String" to 3,
    )

    val map = mapOf(
        IntegerLiteral::class to "int",
        FloatLiteral::class to "float",
        StringLiteral::class to "String",
        BooleanLiteral::class to "bool",
    )

    // Cache for function and variable
    val cache = mutableMapOf<String, String>()

    override fun functionDecl(decl: YFunction) { }

    override fun functionCall(call: FunctionCall) {
        if (cache[call.name] != null) {
            queue.push(cache[call.name])
            return
        }
        val function = table.global.find(call.name)
            ?: throw IllegalArgumentException("Function ${call.name} not found")
        function.assert(SymbolType.Function)
        function as FunctionSym
        for (arg in function.function.args) {
            cache[arg.first] = arg.second
        }
        if (function.retType != "Nothing") {
            queue.push(function.retType)
            function.used = true
            functionReturn(
                function.function.body.list().last() as Return
            )
            val actual = queue.pop()
            if (function.retType != actual)
                throw IllegalArgumentException("Type mismatch for function ${call.name}")
            cache[call.name] = function.retType
        }
    }

    override fun functionReturn(ret: Return) {
        ret.expr?.accept(this)
    }

    override fun literal(literal: Literal<*>) {
        queue.push(
            map[literal::class] ?: throw IllegalArgumentException("Unknown literal type")
        )
    }

    override fun binaryExpr(expr: BinaryExpr) {
        expr.l.accept(this)
        expr.r.accept(this)
        val r = queue.pop()
        val l = queue.pop()
        if (priority[l]!! > priority[r]!!) {
            queue.push(l)
        } else {
            queue.push(r)
        }
    }

    override fun unaryExpr(expr: UnaryExpr) {
        expr.expr.accept(this)
    }

    override fun getVariable(get: VariableGet) {
        if (cache[get.name] != null) {
            queue.push(cache[get.name])
            return
        }
        val variable =
            table.root.find(get.name)
            ?: throw IllegalArgumentException("Variable ${get.name} not found")
        variable.assert(SymbolType.Variable)
        variable as VariableSym
        queue.push(variable.type)
    }

    override fun declareVariable(decl: VariableDecl) { }
    override fun assignVariable(assign: VariableAssign) { }

    override fun condFor(loop: ConditionalFor) { }

    override fun condJump(jump: ConditionalJump) {
        jump.cond.accept(this)
        val cond = queue.pop()
        if (cond != "bool")
            throw IllegalArgumentException("Type mismatch for conditional jump")
        if (jump.ifBlock.list().isEmpty())
            throw IllegalArgumentException("If block is empty")
        if (jump.elseBlock == null || jump.elseBlock.list().isEmpty())
            throw IllegalArgumentException("Else block is empty")

        jump.ifBlock.list().last().accept(this)
        jump.elseBlock.list().last().accept(this)
        val elseType = queue.pop()
        val ifType = queue.pop()

        if (ifType != elseType) {
            throw IllegalArgumentException("Type mismatch for conditional jump")
        }
        queue.push(ifType)
    }

    fun getType(expr: Expression): String {
        expr.accept(this)
        val type = this.queue.pop()
        if (queue.isNotEmpty()) {
            throw IllegalArgumentException("Type mismatch")
        }
        return type
    }
}