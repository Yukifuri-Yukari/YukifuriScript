package yukifuri.script.compiler.visitor.bcgen

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
import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.visitor.bcgen.vm.CompiledFile

class BytecodeGenerator : Visitor {
    val file = CompiledFile()

    override fun functionDecl(decl: YFunction) {
        file.newFunction(file.constPtr)
        file.newConstant(decl.name.toByteArray().toList())
        decl.args.forEach {
        }
    }

    override fun functionCall(call: FunctionCall) {
        TODO("Not yet implemented")
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

    override fun unaryExpr(expr: UnaryExpr) {
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

    override fun condFor(loop: ConditionalFor) {
        TODO("Not yet implemented")
    }

    override fun condJump(jump: ConditionalJump) {
        TODO("Not yet implemented")
    }
}
