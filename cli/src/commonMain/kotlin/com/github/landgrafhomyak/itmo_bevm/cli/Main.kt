package com.github.landgrafhomyak.itmo_bevm.cli


fun mainEmbeddable(argv: Array<String>): Int {
    val argi = argv.iterator()

    @Suppress("SpellCheckingInspection")
    val eprintlnHelp: (String) -> Unit = { s -> eprintln("$s, изучите 'bevm help' для подробностей") }

    if (!argi.hasNext()) {
        eprintln("Команда не указана, изучите 'bevm help' для подробностей")
        return 1
    }

    val commandName = argi.next()
    val command = Commands.byAlias(commandName)
    if (command == null) {
        eprintlnHelp("Неизвестная команда '$commandName'")
        return 2
    }

    val options = mutableMapOf<String, Any?>()
    argv@ while (argi.hasNext()) {
        val option = argi.next()
        aliasedOptions@ for ((optionKey, optionMeta) in command.options) {
            if (option in optionMeta.aliases) {
                if (optionKey in options.keys) {
                    eprintlnHelp("Повторное использование параметра '$option' запрещено")
                    return 3
                }
                if (optionMeta.type == null) {
                    options[optionKey] = null
                } else {
                    if (!argi.hasNext()) {
                        eprintlnHelp("Значение параметра '$option' не получено")
                        return 4
                    }
                    val value = argi.next()
                    options[optionKey] = when (optionMeta.type) {
                        Option.OptionType.BinFile  -> BinaryFile(value)
                        Option.OptionType.TextFile -> TextFile(value)
                        Option.OptionType.Unsigned -> value.toUIntOrNull() ?: (eprintln("Не удаётся конвертировать '$value' в беззнаковое число").also { return@mainEmbeddable 5 })
                    }
                }
                continue@argv
            }
        }

        unnamedOptions@ for ((optionKey, optionMeta) in command.options) {
            if (optionMeta.aliases.isEmpty() && optionKey !in options) {
                options[optionKey] = when (optionMeta.type!!) {
                    Option.OptionType.BinFile  -> BinaryFile(option)
                    Option.OptionType.TextFile -> TextFile(option)
                    Option.OptionType.Unsigned -> option.toUIntOrNull() ?: (eprintln("Не удаётся конвертировать '$option' в беззнаковое число").also { return@mainEmbeddable 5 })
                }
                continue@argv
            }
        }

        eprintlnHelp("Передан лишний параметр")
        return 6
    }

    for ((optionKey, optionMeta) in command.options) {
        if (optionMeta.required && optionKey !in options) {
            eprintln("Некоторые обязательные параметры не были переданы")
            return 7
        }
    }

    return command.execute(options)
}

fun mainNative(argv: Array<String>) {
    exit(mainEmbeddable(argv))
}