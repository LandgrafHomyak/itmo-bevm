package com.github.landgrafhomyak.itmo_bevm

open class OriginalComputer(
    open val microprogram: Microprogram = OriginalMicroprogram
) : OriginalComponent {
//    val bus = OriginalBus()

    inner class OriginalRegisters : AbstractOriginalRegisters(), OriginalComponent {
        override var accumulator = BevmByte()
        override var dataRegister = BevmByte()
        override var bufferRegister = BevmByte()
        override var commandRegister = BevmByte()
        override var programState: AbstractBevmByte
            get() = this@OriginalComputer.flags as AbstractBevmByte
            set(value) {
                this@OriginalComputer.flags = when (value) {
                    is OriginalFlags -> value
                    else             -> OriginalFlags(value)
                }
            }
        override var instructionPointer = BevmAddress()
        override var inputRegister: AbstractBevmByte = BevmByte()
        override var stackPointer = BevmAddress()
        override var addressRegister = BevmAddress()
        override var microCommandRegister: Microcommand = Microcommand.Empty
        override var microCommandPointer: UByte = 0u
    }

    class OriginalFlags() : AbstractOriginalFlags(), AbstractBevmByte, OriginalComponent {
        constructor(byte: AbstractBevmByte) : this() {
            this.bit0 = byte.bit0
            this.bit1 = byte.bit1
            this.bit2 = byte.bit2
            this.bit3 = byte.bit3
            this.bit4 = byte.bit4
            this.bit5 = byte.bit5
            this.bit6 = byte.bit6
            this.bit7 = byte.bit7
            this.bit8 = byte.bit8
            this.bit9 = byte.bit9
            this.bit10 = byte.bit10
            this.bit11 = byte.bit11
            this.bit12 = byte.bit12
            this.bit13 = byte.bit13
            this.bit14 = byte.bit14
            this.bit15 = byte.bit15
        }

        override var isZero: Boolean = false
        override var isNegative: Boolean = false
        override var carry: Boolean = false
        override var overflow: Boolean = false
        override var isInterruptionsAllowed: Boolean = false
        override var isInterruptionRequired: Boolean = false
        override var isWorking: Boolean = false
        override var isRunning: Boolean = false
        override var bit15: Boolean = false
        override var bit14: Boolean = false
        override var bit13: Boolean = false
        override var bit12: Boolean = false
        override var bit11: Boolean = false
        override var bit10: Boolean = false
        override var bit9: Boolean = false
        override var bit8: Boolean by ::isRunning
        override var bit7: Boolean by ::isWorking
        override var bit6: Boolean by ::isInterruptionRequired
        override var bit5: Boolean by ::isInterruptionsAllowed
        override var bit4: Boolean = false
        override var bit3: Boolean by ::isNegative
        override var bit2: Boolean by ::isZero
        override var bit1: Boolean by ::overflow
        override var bit0: Boolean by ::carry
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val registers: AbstractOriginalRegisters = this.OriginalRegisters()
    var flags: AbstractOriginalFlags = OriginalFlags()
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun leftRegistersBus(register: RegisterReadingLeft?) = when (register) {
        RegisterReadingLeft.RDAC -> this.registers.accumulator
        RegisterReadingLeft.RDBR -> this.registers.bufferRegister
        RegisterReadingLeft.RDPS -> this.registers.programState
        RegisterReadingLeft.RDIR -> this.registers.inputRegister
        null                     -> BevmByte()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun rightRegistersBus(register: RegisterReadingRight?) = when (register) {
        RegisterReadingRight.RDDR -> this.registers.dataRegister
        RegisterReadingRight.RDCR -> this.registers.commandRegister
        RegisterReadingRight.RDIP -> this.registers.instructionPointer
        RegisterReadingRight.RDSP -> this.registers.stackPointer
        null                      -> BevmByte()
    }

    open fun executeMicroCommand(): Unit = this.registers.microCommandRegister.let { mc ->
        when (mc) {
            is Microcommand.Empty    -> return@let
            is Microcommand.NotEmpty -> {}
        }
        var aluL = this.leftRegistersBus(mc.rrL)
        var aluR = this.rightRegistersBus(mc.rrR)
        if (mc.invL) {
            aluL = !aluL
        }
        if (mc.invR) {
            aluR = !aluR
        }
        val (alu, c, v) = when (mc.aluOp) {
            ArithmeticalLogicUnitOperation.PLS  -> aluL.plus(aluR, false)
            ArithmeticalLogicUnitOperation.PLS1 -> aluL.plus(aluR, this.flags.carry)
            ArithmeticalLogicUnitOperation.SORA -> BevmByteSum(aluL and aluR, carry = false, overflow = false)
        }

        val com = when (mc) {
            is Microcommand.Operation -> mc.cmtBs
            is Microcommand.Control   -> ComutatorSourceCombination.Forward(mc.cmtLbs, mc.cmtHbs)
        }.let { com ->
            when (com) {
                is ComutatorSourceCombination.Forward -> {
                    val lo = when (com.lo) {
                        CommutatorLoByteSource.LTOL -> arrayOf(
                            alu.bit7, alu.bit6, alu.bit5, alu.bit4, alu.bit3, alu.bit2, alu.bit1, alu.bit0,
                        )
                        CommutatorLoByteSource.HTOL -> arrayOf(
                            alu.bit15, alu.bit14, alu.bit13, alu.bit12, alu.bit11, alu.bit10, alu.bit9, alu.bit8,
                        )
                        null                        -> arrayOf(
                            false, false, false, false, false, false, false, false
                        )
                    }
                    val hi = when (com.hi) {
                        CommutatorHiByteSource.LTOH -> arrayOf(
                            alu.bit7, alu.bit6, alu.bit5, alu.bit4, alu.bit3, alu.bit2, alu.bit1, alu.bit0,
                        )
                        CommutatorHiByteSource.HTOH -> arrayOf(
                            alu.bit15, alu.bit14, alu.bit13, alu.bit12, alu.bit11, alu.bit10, alu.bit9, alu.bit8,
                        )
                        CommutatorHiByteSource.SEXT -> arrayOf(
                            alu.bit7, alu.bit7, alu.bit7, alu.bit7, alu.bit7, alu.bit7, alu.bit7, alu.bit7,
                        )
                        null                        -> arrayOf(
                            false, false, false, false, false, false, false, false
                        )
                    }
                    BevmByte(
                        hi[7], hi[6], hi[5], hi[4], hi[3], hi[2], hi[1], hi[0],
                        lo[7], lo[6], lo[5], lo[4], lo[3], lo[2], lo[1], lo[0]
                    )
                }
                is ComutatorSourceCombination.SHL     -> BevmByte(
                    alu.bit14, alu.bit13, alu.bit12, alu.bit11, alu.bit10, alu.bit9, alu.bit8, alu.bit7, alu.bit6, alu.bit5, alu.bit4, alu.bit3, alu.bit2, alu.bit1, alu.bit0,
                    if (com.shl0) this.flags.carry else false,
                )
                is ComutatorSourceCombination.SHR     -> BevmByte(
                    if (com.shrF) this.flags.carry else alu.bit15,
                    alu.bit15, alu.bit14, alu.bit13, alu.bit12, alu.bit11, alu.bit10, alu.bit9, alu.bit8, alu.bit7, alu.bit6, alu.bit5, alu.bit4, alu.bit3, alu.bit2, alu.bit1,
                )
                null                                  -> BevmByte()
            }
        }

        when (mc) {
            is Microcommand.Control   -> {
                if (((com.toUnsigned().toUByte() and mc.chB).toUInt() != 0u) == mc.cmpB) {
                    this.registers.microCommandPointer = mc.addr
                }
                return@let
            }
            is Microcommand.Operation -> {
                if (mc.setC) this.flags.carry = c
                if (mc.setV) this.flags.overflow = v
                if (mc.setNZ) {
                    this.flags.isNegative = com.signBit
                    this.flags.isZero = com.isZero()
                }

                if (mc.sAC) this.registers.accumulator = com
                if (mc.sDR) this.registers.dataRegister = com
                if (mc.sBR) this.registers.bufferRegister = com
                if (mc.sCR) this.registers.commandRegister = com
                if (mc.sSP) this.registers.stackPointer = com.toBevmAddress()
                if (mc.sIP) this.registers.instructionPointer = com.toBevmAddress()
                if (mc.sPS) this.registers.programState = com
                if (mc.sAR) this.registers.addressRegister = com.toBevmAddress()

                // todo io ints

                if (mc.stop) this.flags.isRunning = false
            }
        }

        return@let
    }

    fun runMcWithTracing(start: UByte = this.microprogram.entryPointAddress, debugger: (UByte, Microcommand, AbstractOriginalRegisters, AbstractOriginalFlags) -> Unit) {
        this.registers.microCommandPointer = start
        this.flags.isRunning = true
        while (this.flags.isRunning) {
            val address = this.registers.microCommandPointer++

            @Suppress("SpellCheckingInspection")
            val microcommand = this.microprogram[address]
            this.registers.microCommandRegister = microcommand
            this.executeMicroCommand()
            debugger(address, microcommand, this.registers, this.flags)
        }
    }

    fun runMcWithTracing(start: UByte = this.microprogram.entryPointAddress) = this.runMcWithTracing(start) { _, _, _, _ -> }
}