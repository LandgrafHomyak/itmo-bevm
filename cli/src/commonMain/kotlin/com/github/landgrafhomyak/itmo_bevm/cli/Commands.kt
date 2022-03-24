package com.github.landgrafhomyak.itmo_bevm.cli

@Suppress("MemberVisibilityCanBePrivate", "unused")
enum class Commands(
    val alias: String,
    val help: String,
    val options: Map<String, Option>,
    val action: () -> Int
) {
    Help(
        "help", "показывает это сообщение",
        mapOf(),
        action@{
            println("Использование:")
            println("\tbevm <команда>")
            println()
            println("Команды:")

            val maxRowSize: UInt
            Commands.byAlias.map { (name, command) ->
                "$name " + command.options.values.joinToString(separator = " ") { option ->
                    var s = option.aliases.joinToString(separator = " | ")
                    if (option.aliases.size > 1 && (option.required || option.type != null)) {
                        s = "( $s )"
                    }
                    if (option.type != null) {
                        "<${option.type.label}>".also { a ->
                            s = if (option.aliases.isEmpty()) a else "$s $a"
                        }
                    }
                    if (!option.required) {
                        s = "[ $s ]"
                    }
                    return@joinToString s
                } to command.help
            }.apply {
                maxRowSize = maxOf { (usage, _) -> usage.length }.toUInt()
            }.forEach { (usage, help) ->
                println("\t" + usage.padEnd(maxRowSize.toInt(), ' ') + " - $help")
            }

            println()
            println("Исходный код: https://github.com/landgrafhomyak/itmo-bevm")
            return@action 0
        }
    ),
    Run("run", "запускает скомпилированную программу",
        mapOf(
            "out" to Option(Option.OptionType.File, false, "--dump"),
            "start" to Option(Option.OptionType.Unsigned, false, "-ip"),
            "in" to Option(Option.OptionType.File, false)
        ),
        action@{
            return@action 0
        }
    )
    ;

    init {
        @Suppress("RemoveRedundantQualifierName")
        Commands.byAlias[this.alias] = this
    }

    fun execute(args: Map<String, Any?>): Int = this.action()

    companion object {
        private val byAlias = mutableMapOf<String, Commands>()
        fun byAlias(name: String): Commands? = this.byAlias[name]

        init {
            @Suppress("SpellCheckingInspection")
            /* Костыль, чтобы починить ленивую инициализацию членов енама в Kotlin/Native */
            @Suppress("ControlFlowWithEmptyBody", "RemoveRedundantQualifierName")
            for (v in Commands.values()) {
            }
        }
    }
}