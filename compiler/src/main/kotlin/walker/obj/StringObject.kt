package yukifuri.script.compiler.walker.obj

class StringObject(
    val value: String
) : Object() {
    override fun toString(): String {
        return value
    }
}
