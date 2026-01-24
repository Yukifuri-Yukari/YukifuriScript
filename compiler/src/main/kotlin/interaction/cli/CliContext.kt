package yukifuri.script.compiler.interaction.cli

data class CliContext(
    val history: MutableList<String> = mutableListOf(),
    val vars: MutableMap<String, String> = mutableMapOf(),
    var args: List<String> = listOf()
)
