package com.github.landgrafhomyak.itmo_bevm

abstract class AbstractCommandRegistry<out T : AbstractCommand> {
    abstract fun parse(repr: BevmByte): T
}