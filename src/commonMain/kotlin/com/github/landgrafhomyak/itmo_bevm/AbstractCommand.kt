package com.github.landgrafhomyak.itmo_bevm

interface AbstractCommand {
    fun execute(proc: Processor<*>)

    val mnemonic: String
}