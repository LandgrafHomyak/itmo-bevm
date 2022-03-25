@file:Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@file:OptIn(ExperimentalUnsignedTypes::class)

package com.github.landgrafhomyak.itmo_bevm.cli

@Suppress("MemberVisibilityCanBePrivate", "unused")
enum class Commands(
    val alias: String,
    val help: String,
    val options: Map<String, Option>,
    val action: (Map<String, Any?>) -> Int
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
            "out" to Option(Option.OptionType.BinFile, true, "--dump"),
            "start" to Option(Option.OptionType.Unsigned, false, "-ip"),
            "in" to Option(Option.OptionType.BinFile, true)
        ),
        action@{ args ->
            val `in`: FileLike.Binary = (args["in"] ?: BinaryStd) as FileLike.Binary
            val out: FileLike.Binary = (args["out"] ?: BinaryStd) as FileLike.Binary
            val start: UInt = (args["ip"] ?: 0u) as UInt

            return@action 0
        }
    ),


    PrettyBin(@Suppress("SpellCheckingInspection") "viewbin", "форматирует бинарные файлы в текстовый вид",
        mapOf(
            "out" to Option(Option.OptionType.TextFile, false, "-o", "--out"),
            "len" to Option(Option.OptionType.Unsigned, false, "-l", "--length"),
            "word" to Option(null, false, "-w", "--word"),
            "in" to Option(Option.OptionType.BinFile, true)
        ),
        action@{ args ->
            val `in`: FileLike.Binary = args["in"]!! as FileLike.Binary
            val out: FileLike.Text = (args["out"] ?: TextStd) as FileLike.Text
            val len: UInt = (args["len"] ?: 16u) as UInt
            val word: Boolean = args.containsKey("word")

            if (len == 0u) {
                eprintln("Длинна строки должна быть больше нуля")
                return@action -1
            }

            val data = `in`.readAll().let { data ->
                return@let if (word) {
                    (data.indices step 2).map { i -> (data[i].toUInt() shl 8) or (if (i + 1 < data.size) data[i + 1].toUInt() else 0u) }.toUIntArray()
                } else {
                    data.map(UByte::toUInt).toUIntArray()
                }
            }
            val addressSize = data.size.toString(16).length
            for (pos in data.indices step len.toInt()) {
                out.write("0x${pos.toString(16).padStart(addressSize, '0')} | ")
                out.write(data.slice(pos until min(pos + len.toInt(), data.size)).joinToString(separator = " ") { b -> b.toString(16).padStart(if (word) 4 else 2, '0') })
                out.write("\n")
            }

            return@action 0
        }
    )
    ;

    init {
        @Suppress("RemoveRedundantQualifierName")
        Commands.byAlias[this.alias] = this
    }

    fun execute(args: Map<String, Any?>): Int = this.action(args)

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