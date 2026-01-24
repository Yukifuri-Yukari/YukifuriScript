package yukifuri.script.compiler.visitor.bcgen.vm


class CompiledFile {
    companion object {
        val FILE_HEADER = listOf(
            'Y'.code.toByte(), 'S'.code.toByte(),
            'B'.code.toByte(), 'C'.code.toByte(),
            0x00.toByte(),     0x00.toByte(),
            0x00.toByte(),     0x00.toByte(),
        )
    }

    // Pair.first: Size, Pair.second: Value
    val constants = mutableListOf<Pair<Byte, List<Byte>>>()
    // Pair.first: Name (Index of constant pool), Pair.second: Start
    val functions = mutableListOf<Pair<Byte, Int>>()

    val instructions = mutableListOf<Byte>()

    var constPtr: Byte = 0
    var funcPtr: Byte = 0
    var instPtr: Byte = 0

    fun newConstant(value: List<Byte>): CompiledFile {
        constants.add(Pair(value.size.toByte(), value))
        constPtr++
        return this
    }

    fun newFunction(name: Byte, start: Int = instructions.size): CompiledFile {
        functions.add(Pair(name, start))
        funcPtr++
        return this
    }

    fun addInst(vararg inst: Byte): CompiledFile {
        for (b in inst) {
            instructions.add(b)
            instPtr++
        }
        return this
    }

    fun toByteArray(): ByteArray {
        val array = mutableListOf<Byte>()
        array.addAll(FILE_HEADER)
        for (const in constants) {
            array.add(const.first)
            array.addAll(const.second.toList())
        }
        for (func in functions) {
            array.add(func.first)
            array.add(func.second.toByte())
        }
        array.addAll(instructions)
        return array.toByteArray()
    }
}
