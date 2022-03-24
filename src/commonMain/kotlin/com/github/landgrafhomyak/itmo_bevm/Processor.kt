package com.github.landgrafhomyak.itmo_bevm

import kotlin.jvm.JvmField

@Suppress("unused")
class Processor {
    class Registers internal constructor() {
        @JvmField
        var accumulator: Byte = Byte.uninitialized()

        @JvmField
        var data: Byte = Byte.uninitialized()

        @JvmField
        var buffer: Byte = Byte.uninitialized()

        @JvmField
        var command: Byte = Byte.uninitialized()

        @JvmField
        internal var programState: Byte = Byte.uninitialized()

        @JvmField
        var stackPointer: Byte = Byte.uninitialized()

        @JvmField
        var input: Byte = Byte.uninitialized()

        @JvmField
        var instructionPointer: Byte = Byte.uninitialized()

        @JvmField
        var address: Byte = Byte.uninitialized()
    }

    @JvmField
    val registers = Registers()

    inner class Flags internal constructor() {
        var carry by this@Processor.registers.programState.bitProperty(0u)
        var overflow by this@Processor.registers.programState.bitProperty(1u)
        var zero by this@Processor.registers.programState.bitProperty(2u)
        var sign by this@Processor.registers.programState.bitProperty(3u)
    }

    @JvmField
    val flags = this.Flags()

    @JvmField
    val memory = Memory(2048u)

    /**
     * Запускает программу в заданном адресе
     */
    fun runAt(address: UInt) {

    }
}