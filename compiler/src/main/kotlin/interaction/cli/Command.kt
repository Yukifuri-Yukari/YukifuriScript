package yukifuri.script.compiler.interaction.cli

/**
 * This class helps to create a cli command.
 *
 * @param name The name of the command
 * @param desc The description of the command (Will be shown in :help)
 * @param arguments The number of arguments the command takes (Can be more than 1 case)
 *
 * or just a listOf(-1) means the command can take any number of arguments.
 *
 * ```kt
 * object : Command("help", "Print help messages", listOf(0, 1)) {
 *     override fun run(ctx: CliContext) {
 *         ...
 *     }
 * }
 * ```
 * Declares a command called "help" with 0 or 1 arguments.
 *
 * And its description is "Print help messages".
 */
abstract class Command(
    val name: String,
    val desc: String = "No Further Information",
    val arguments: List<Int> = listOf(0),
) {
    companion object {
        fun new(
            name: String,
            function: (CliContext) -> Unit
        ) = name to object : Command(name) {
            override fun run(ctx: CliContext) {
                function(ctx)
            }
        }

        fun new(
            name: String,
            desc: String,
            arguments: List<Int>,
            function: (CliContext) -> Unit
        ) = name to object : Command(name, desc, arguments) {
            override fun run(ctx: CliContext) {
                function(ctx)
            }
        }
    }

    abstract fun run(ctx: CliContext)
}
