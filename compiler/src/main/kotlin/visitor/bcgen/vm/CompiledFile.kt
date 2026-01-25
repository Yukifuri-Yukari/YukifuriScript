package yukifuri.script.compiler.visitor.bcgen.vm

import yukifuri.script.compiler.util.Pair3


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
    val functions = mutableListOf<Pair3<Byte, List<Byte>, Int>>()

    val instructions = mutableListOf<Byte>()

    var constPtr: Byte = 0
    var funcPtr: Byte = 0
    var instPtr = 0

    fun newConstant(value: List<Byte>): CompiledFile {
        constants.add(Pair(value.size.toByte(), value))
        constPtr++
        return this
    }

    fun newFunction(name: Byte, signature: List<Byte>, start: Int = instructions.size): CompiledFile {
        functions.add(Pair3(name, signature, start))
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
        array.add(constPtr)
        for (const in constants) {
            array.add(const.first)
            array.addAll(const.second.toList())
        }
        array.add(funcPtr)
        for (func in functions) {
            array.add(func.first)
            array.addAll(func.second)
            array.add(func.third.toByte())
        }
        array.addAll(instructions)
        return array.toByteArray()
    }

    override fun toString(): String {
        return "CompiledFile(constants=$constants, functions=$functions, instructions=$instructions)"
    }
}
