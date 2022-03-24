package com.github.landgrafhomyak.itmo_bevm.cli


class Option(
    val type: OptionType?,
    val required: Boolean = false,
    vararg val aliases: String,
) {

    enum class OptionType(val label: String) {
        File("файл"),
        Unsigned("беззнаковое число")
    }
}