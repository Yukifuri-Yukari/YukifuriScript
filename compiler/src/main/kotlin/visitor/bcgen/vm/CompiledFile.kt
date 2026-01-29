package yukifuri.script.compiler.visitor.bcgen.vm

import yukifuri.script.compiler.util.toBytes
import yukifuri.script.compiler.visitor.bcgen.BytecodeGenerator.Companion.toByteList
import kotlin.experimental.or

class CompiledFile {
    companion object {
        val FILE_HEADER = listOf(
            'Y'.code.toByte(), 'S'.code.toByte(),
            'B'.code.toByte(), 'C'.code.toByte(),
            0x00.toByte(),     0x00.toByte(),
            0x00.toByte(),     0x00.toByte(),
        )
    }

    private val constants = mutableListOf<ConstantEntry>()
    private val constantMap = mutableMapOf<List<Byte>, Short>()

    private val functions = mutableListOf<Method>()
    private val functionMap = mutableMapOf<String, Short>()

    private val instructions = mutableListOf<Byte>()

    fun newConstant(
        value: List<Byte>,
        type: ConstantEntry.ConstantType = ConstantEntry.ConstantType.String
    ): Short {
        val ind = constantMap[value]
        if (ind != null) return ind

        val size = constants.size.toShort()
        constants.add(ConstantEntry(type, size, value))
        constantMap[value] = size

        return size
    }

    fun newFunction(methodRef: Short, body: List<Byte>): Short {
        val index = functions.size.toShort()
        functions.add(Method(methodRef, FieldAccessModifier.serialize(
            FieldAccessModifier.Public,
            FieldAccessModifier.Static,
            FieldAccessModifier.Final
        ), body))
        return index
    }

    fun addInst(vararg bytes: Byte) {
        instructions.addAll(bytes.toList())
    }

    fun build(): List<Byte> {
        return instructions.also { instructions.clear() }
    }

    fun getConst(id: Short) = constants[id.toInt()]

    fun getFunc(id: Short) = functions[id.toInt()]

    fun toByteArray(): ByteArray {
        val array = mutableListOf<Byte>()
        array.addAll(FILE_HEADER)
        array.addAll(constants.size.toBytes())

        for (constant in constants) {
            array.add(constant.enumType.id())
            array.addAll(constant.size.toBytes())
            array.addAll(constant.value)
        }

        array.addAll(functions.size.toBytes())
        for (func in functions) {
            array.addAll(func.methodRef.toByteList())
            array.add(func.accessTag)
            array.addAll(func.body)
        }

        return array.toByteArray()
    }

    override fun toString(): String {
        return "CompiledFile(constants=$constants, functions=$functions)"
    }

    data class ConstantEntry(
        val enumType: ConstantType,
        val index: Short,
        val value: List<Byte>,
        val size: Byte =
            if (enumType != ConstantType.String)
                enumType.size
            else value.size.toByte(),
    ) {
        enum class ConstantType(val size: Byte) {
            Int(1),
            Float(2),
            String(-1),
            MethodRef(4),
            Class(-1), // Reserved
            Descriptor(-1),
            ;
            fun id() = ordinal.toByte()
        }

        override fun toString(): String {
            return "ConstantEntry(enumType=$enumType, index=$index, value=$value, size=$size)"
        }
    }

    data class Method(
        val methodRef: Short, // CP Index
        val accessTag: Byte, // Access Modifier
        val body: List<Byte>
    ) {
        override fun toString(): String {
            return "Method(methodRef=$methodRef, accessTag=$accessTag, body=$body)"
        }
    }

    enum class FieldAccessModifier(val mask: Byte) {
        Public(0x01),
        Private(0x02),
        Protected(0x04),
        Static(0x08),
        Final(0x0A),
        ;
        companion object {
            fun serialize(vararg modifiers: FieldAccessModifier): Byte {
                var res: Byte = 0
                for (modifier in modifiers) {
                    res = res or modifier.mask
                }
                return res
            }
        }
    }
}
