package yukifuri.script.compiler.visitor.bcgen.vm

object Bytecodes {
    const val I_PUSH: Byte = 0x00
    const val I_POP: Byte = 0x01
    const val I_ADD: Byte = 0x02
    const val I_SUB: Byte = 0x03
    const val I_MUL: Byte = 0x04
    const val I_DIV: Byte = 0x05
    const val I_NEG: Byte = 0x06
    const val I_EQ: Byte = 0x07
    const val I_LT: Byte = 0x08
    const val I_LTE: Byte = 0x09
    const val I_LOAD: Byte = 0x0A
    const val I_STORE: Byte = 0x0B
    const val F_PUSH: Byte = 0x0C
    const val F_POP: Byte = 0x0D
    const val F_ADD: Byte = 0x0E
    const val F_SUB: Byte = 0x0F
    const val F_MUL: Byte = 0x10
    const val F_DIV: Byte = 0x11
    const val F_NEG: Byte = 0x12
    const val F_LOAD: Byte = 0x13
    const val F_STORE: Byte = 0x14
    const val S_LOAD: Byte = 0x15
    const val S_STORE: Byte = 0x16
    const val S_INVOKE: Byte = 0x17
    const val RETURN: Byte = 0x18
    const val I_RETURN: Byte = 0x19
    const val F_RETURN: Byte = 0x1A

    fun generate() {
        val inst = listOf(
            "i push", "i pop", "i add", "i sub", "i mul", "i div", "i neg",
            "i eq", "i lt", "i lte", "i load", "i store",

            "f push", "f pop", "f add", "f sub", "f mul", "f div", "f neg",
            "f load", "f store",

            "s load", "s store",

            "s invoke", "return", "i return", "f return"
        )
        for ((i, ins) in inst.withIndex()) {
            println(
                "    const val ${
                    ins.replace(" ", "_").uppercase()
                }: Byte = 0x${
                    i.toByte().toHexString().uppercase()
                }")
        }
    }
}
