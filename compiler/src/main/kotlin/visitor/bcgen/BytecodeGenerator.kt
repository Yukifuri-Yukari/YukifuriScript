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
import yukifuri.script.compiler.visitor.bcgen.entry.symbol.SymbolTable
import yukifuri.script.compiler.visitor.bcgen.vm.CompiledFile
import yukifuri.script.compiler.visitor.bcgen.vm.CompiledFile.ConstantEntry.ConstantType
import kotlin.experimental.and

class BytecodeGenerator : Visitor {
    companion object {
        fun String.toByteList(): List<Byte> {
            val bytes = toByteArray(Charsets.UTF_16BE)
            return bytes.toList()
        }

        fun Short.toByteList(): List<Byte> {
            return listOf(
                ((this.toInt() ushr 8) and 0xFF).toByte(),
                (this.toInt() and 0xFF).toByte()
            )
        }
    }

    private lateinit var file: YFile
    private lateinit var symbolTable: SymbolTable
    private val bcf = CompiledFile()

    fun exec(fileIn: YFile, symbolTableIn: SymbolTable): BytecodeGenerator {
        file = fileIn
        symbolTable = symbolTableIn
        for (decl in file.table.functions().values) {
            decl.accept(this)
        }
        return this
    }

    fun toByteArray() = bcf.toByteArray()

    fun compiledFile() = bcf

    fun type(type: String): String {
        return when (type) {
            "int" -> "I"
            "float" -> "F"
            "String" -> "S"
            "Nothing" -> "N"
            else -> TODO()
        }
    }

    override fun functionDecl(decl: YFunction) {
        val name = bcf.newConstant(decl.name.toByteList())
        val descriptorName = "(${
            decl.args.joinToString {
                "${type(it.second)},"
            }
        })${type(decl.returnType)};"
        val descriptor = bcf.newConstant(
            descriptorName.toByteList(),
            ConstantType.Descriptor
        )
        val methodRef = bcf.newConstant(
            name.toByteList() + descriptor.toByteList(),
            ConstantType.MethodRef
        )
        decl.body.accept(this)
        bcf.newFunction(methodRef, bcf.build())
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
