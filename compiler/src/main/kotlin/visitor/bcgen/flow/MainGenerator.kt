package yukifuri.script.compiler.visitor.bcgen.flow

import yukifuri.script.compiler.ast.structure.YFile
import yukifuri.script.compiler.visitor.bcgen.BytecodeGenerator
import yukifuri.script.compiler.visitor.bcgen.entry.Entry

class MainGenerator {
    lateinit var file: YFile

    private val entry = Entry()
    private val bcGen = BytecodeGenerator()

    fun exec(fileIn: YFile): ByteArray {
        file = fileIn
        entry.exec(file)
        bcGen.exec(file, entry.table)
        return bcGen.toByteArray()
    }
}
