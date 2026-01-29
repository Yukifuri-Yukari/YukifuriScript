package yukifuri.script.compiler.visitor.walker.obj

class StringObject(
    val value: String
) : Object() {
    override fun toString(): String {
        return value
    }
}
