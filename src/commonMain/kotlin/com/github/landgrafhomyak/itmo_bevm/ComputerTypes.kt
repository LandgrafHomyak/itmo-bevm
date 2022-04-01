package com.github.landgrafhomyak.itmo_bevm

abstract class AbstractOriginalRegisters {
    abstract var accumulator: BevmByte

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("accumulator"))
    var AC by ::accumulator

    abstract var dataRegister: BevmByte

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("dataRegister"))
    var DR by ::dataRegister

    abstract var bufferRegister: BevmByte

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("bufferRegister"))
    var BR by ::bufferRegister

    abstract var commandRegister: BevmByte

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("commandRegister"))
    var CR by ::commandRegister

    abstract var programState: AbstractBevmByte

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("programState"))
    var PS by ::programState

    abstract var instructionPointer: BevmAddress

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("instructionPointer"))
    var IP by ::instructionPointer

    abstract var inputRegister: AbstractBevmByte

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("inputRegister"))
    var IR by ::inputRegister

    abstract var stackPointer: BevmAddress

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("stackPointer"))
    var SP by ::stackPointer

    abstract var addressRegister: BevmAddress

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("addressRegister"))
    var AR by ::addressRegister

    abstract var microCommandRegister: Microcommand

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("microCommandRegister"))
    var MR by ::microCommandRegister

    abstract var microCommandPointer: UByte

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя регистра", ReplaceWith("microCommandPointer"))
    var MP by ::microCommandPointer
}

abstract class AbstractOriginalFlags {
    abstract var isZero: Boolean

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя флага", ReplaceWith("isZero"))
    var Z by ::isZero


    abstract var isNegative: Boolean

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя флага", ReplaceWith("isNegative"))
    var N by ::isNegative

    abstract var carry: Boolean

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя флага", ReplaceWith("carry"))
    var C by ::carry

    abstract var overflow: Boolean

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя флага", ReplaceWith("overflow"))
    var V by ::overflow

    abstract var isInterruptionsAllowed: Boolean

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя флага", ReplaceWith("isInterruptionsAllowed"))
    var EI by ::isInterruptionsAllowed


    abstract var isInterruptionRequired: Boolean

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя флага", ReplaceWith("interruptionRequired"))
    var INT by ::isInterruptionRequired


    abstract var isWorking: Boolean

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя флага", ReplaceWith("isWorking"))
    var W by ::isWorking

    abstract var isRunning: Boolean

    @Suppress("PropertyName")
    @Deprecated("Используйте полное имя флага", ReplaceWith("isRunning"))
    var P by ::isRunning

}