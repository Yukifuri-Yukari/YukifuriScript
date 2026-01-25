package yukifuri.script.compiler.visitor.bcgen

import yukifuri.script.compiler.ast.expr.*
import yukifuri.script.compiler.ast.flow.ConditionalFor
import yukifuri.script.compiler.ast.flow.ConditionalJump
import yukifuri.script.compiler.ast.function.FunctionCall
import yukifuri.script.compiler.ast.function.Return
import yukifuri.script.compiler.ast.function.YFunction
import yukifuri.script.compiler.ast.literal.Literal
import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.ast.visitor.Visitor
import yukifuri.script.compiler.visitor.bcgen.vm.CompiledFile
import javax.lang.model.type.NullType

class BytecodeGenerator : Visitor {
    companion object {
        val typeTable = mutableMapOf(
            "int" to 'I'.code.toByte(),
            "float" to 'F'.code.toByte(),
            "String" to 'S'.code.toByte(),
            "Nothing" to 'N'.code.toByte(),
        )
    }

    private lateinit var file: YFile
    private val bcf = CompiledFile()

    fun exec(fileIn: YFile): BytecodeGenerator {
        file = fileIn
        for (decl in file.table.functions().values) {
            decl.accept(this)
        }
        return this
    }

    fun toByteArray() = bcf.toByteArray()


    fun compiledFile() = bcf

    override fun functionDecl(decl: YFunction) {
        val signature = mutableListOf<Byte>()
        for (arg in decl.args) {
            signature.add(typeTable[arg.second]!!)
        }
        signature.add(typeTable[decl.returnType]!!)
        bcf.newFunction(bcf.constPtr, signature)
        bcf.newConstant(decl.name.toByteArray(Charsets.UTF_32).toList())
        decl.body.accept(this)
    }

    override fun functionCall(call: FunctionCall) {
    }

    override fun functionReturn(ret: Return) {
    }

    override fun literal(literal: Literal<*>) {
    }

    override fun binaryExpr(expr: BinaryExpr) {
    }

    override fun unaryExpr(expr: UnaryExpr) {
    }

    override fun getVariable(get: VariableGet) {
    }

    override fun declareVariable(decl: VariableDecl) {
    }

    override fun assignVariable(assign: VariableAssign) {
    }

    override fun condFor(loop: ConditionalFor) {
    }

    override fun condJump(jump: ConditionalJump) {
    }
}
