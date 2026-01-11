package yukifuri.script.compiler.walker

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
import yukifuri.script.compiler.ast.literal.StringLiteral
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.ast.visitor.Visitor

class Uk(
    val file: YFile
) : Visitor {
    override fun functionDecl(decl: YFunction) {
        print("fun ${decl.name}(${
            decl.args.joinToString(", ") { "${it.first}: ${it.second}" } 
        })${if (decl.returnType != "Nothing") ": ${decl.returnType}" else ""} {")
        decl.body.accept(this)
        println("}\n")
    }

    override fun functionCall(call: FunctionCall) {
        print("${call.name}(")
        call.args.forEachIndexed { i, arg ->
            arg.accept(this)
            if (i != call.args.size - 1) print(", ")
        }
        println(")")
    }

    override fun functionReturn(ret: Return) {
        print("return ")
        ret.expr.accept(this)
        println()
    }

    override fun literal(literal: Literal<*>, type: Class<*>) {
        when (literal) {
            is StringLiteral -> print("\"${literal.get()}\"")
            else -> print(literal.get())
        }
    }

    override fun binaryExpr(expr: BinaryExpr) {
        expr.l.accept(this)
        print(" ")
        print(expr.operator.op)
        print(" ")
        expr.r.accept(this)
    }

    override fun unaryExpr(expr: UnaryExpr) {
        print(expr.operator.op)
        expr.expr.accept(this)
    }

    override fun getVariable(get: VariableGet) {
        print(get.name)
    }

    override fun declareVariable(decl: VariableDecl) {
        print("${if (decl.mutable) "var" else "val"} ${decl.name}: ${decl.type} = ")
        decl.value.accept(this)
        println()
    }

    override fun assignVariable(assign: VariableAssign) {
        print("${assign.name} ${assign.operator.op} ")
        assign.value.accept(this)
        println()
    }

    override fun condFor(loop: ConditionalFor) {
        print("for (")
        loop.init.accept(this)
        print("; ")
        loop.cond.accept(this)
        print("; ")
        loop.updater.accept(this)
        print(") {")
        loop.body.accept(this)
        println("}")
    }

    override fun condJump(jump: ConditionalJump) {
        print("if (")
        jump.cond.accept(this)
        print(") {")
        jump.ifBlock.accept(this)
        print("}")
        if (jump.elseBlock != null) {
            print(" else {")
            jump.elseBlock.accept(this)
            print("}")
        }
        println()
    }

    fun exec() {
        file.module.accept(this)
    }
}
