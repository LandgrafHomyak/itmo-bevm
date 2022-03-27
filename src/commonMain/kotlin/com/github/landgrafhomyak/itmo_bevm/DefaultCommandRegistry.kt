package com.github.landgrafhomyak.itmo_bevm

object DefaultCommandRegistry : AbstractCommandRegistry<DefaultCommandRegistry.Commands>() {
    @Suppress("unused")
    enum class Commands : AbstractCommand {
        NOP {
            override fun execute(proc: Processor<*>) {}

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        HLT {
            override fun execute(proc: Processor<*>) {
                throw ShutdownSignal()
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        CLA {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = BevmByte.fromUnsigned(0x0000u)
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        NOT {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = !proc.registers.accumulator
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, true, false, true, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        CLC {
            override fun execute(proc: Processor<*>) {
                proc.flags.carry = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        CMC {
            override fun execute(proc: Processor<*>) {
                proc.flags.carry = !proc.flags.carry
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        ROL {
            override fun execute(proc: Processor<*>) {
                val c = proc.registers.accumulator.signBit
                proc.registers.accumulator = proc.registers.accumulator shl 1u
                proc.registers.accumulator[0u] = proc.flags.carry
                proc.flags.recalcFromAccumulator()
                proc.flags.carry = c
                // proc.flags.overflow = TODO()
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        ROR {
            override fun execute(proc: Processor<*>) {
                val c = proc.registers.accumulator[0u]
                proc.registers.accumulator = proc.registers.accumulator shr 1u
                proc.registers.accumulator.signBit = proc.flags.carry
                proc.flags.recalcFromAccumulator()
                proc.flags.carry = c
                // proc.flags.overflow = TODO()
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, false, false, true, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        ASL {
            override fun execute(proc: Processor<*>) {
                proc.registers.data = proc.registers.accumulator
                proc.registers.accumulator = proc.registers.accumulator.plus(proc.registers.data)
                proc.flags.recalcFromAccumulator()
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, false, true, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        ASR {
            override fun execute(proc: Processor<*>) {
                val c = proc.registers.accumulator[0u]
                proc.registers.accumulator = proc.registers.accumulator shr 1u
                proc.flags.recalcFromAccumulator()
                proc.flags.carry = c
                // proc.flags.overflow = TODO()
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, false, true, true, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        @Suppress("SpellCheckingInspection")
        SXTB {
            override fun execute(proc: Processor<*>) {
                for (b in 8u until BevmByte.BITS_SIZE) {
                    proc.registers.accumulator[b] = proc.registers.accumulator[7u]
                }
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, false, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        @Suppress("SpellCheckingInspection")
        SWAB {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = BevmByte.fromBits(
                    *(8u..15u).map(proc.registers.accumulator::bit).toBooleanArray(), *(0u..7u).map(proc.registers.accumulator::bit).toBooleanArray()
                )
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, false, true, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        INC {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator.plus(1u, proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        DEC {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator.plus(!BevmByte.fromUnsigned(0u), proc.flags)
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, true, false, true, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        NEG {
            override fun execute(proc: Processor<*>) {
                NOT.execute(proc)
                INC.execute(proc)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, true, true, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        POP {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.memory[proc.registers.stackPointer++]
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        @Suppress("SpellCheckingInspection")
        POPF {
            override fun execute(proc: Processor<*>) {
                proc.registers.programState = proc.memory[proc.registers.stackPointer++]
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, false, false, true, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        RET {
            override fun execute(proc: Processor<*>) {
                proc.registers.instructionPointer = proc.memory[proc.registers.stackPointer++]
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        @Suppress("SpellCheckingInspection")
        IRET {
            override fun execute(proc: Processor<*>) {
                POPF.execute(proc)
                RET.execute(proc)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, false, true, true, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        PUSH {
            override fun execute(proc: Processor<*>) {
                proc.memory[--proc.registers.stackPointer] = proc.registers.accumulator
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        @Suppress("SpellCheckingInspection")
        PUSHF {
            override fun execute(proc: Processor<*>) {
                proc.memory[--proc.registers.stackPointer] = proc.registers.programState
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, true, false, true, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        SWAP {
            override fun execute(proc: Processor<*>) {
                val ac = proc.registers.accumulator
                proc.registers.accumulator = proc.memory[proc.registers.stackPointer]
                proc.memory[proc.registers.stackPointer] = ac
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        DI {
            override fun execute(proc: Processor<*>) {
                proc.flags.allowInterruption = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, true,
                false, false, false, false,
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        EI {
            override fun execute(proc: Processor<*>) {
                proc.flags.allowInterruption = true
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, true,
                false, false, false, true,
            )

            override fun format(repr: BevmByte): String = this.mnemonic
        },

        AND {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator and AddressType.resolveAndGet(proc)
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, true, false,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        OR {
            override fun execute(proc: Processor<*>) {
                proc.registers.data = !AddressType.resolveAndGet(proc)
                proc.registers.accumulator = !proc.registers.accumulator and proc.registers.data
                NOT.execute(proc)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, true, true,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        ADD {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator.plus(AddressType.resolveAndGet(proc), proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, true, false, false,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        ADC {
            override fun execute(proc: Processor<*>) {
                ADD.execute(proc)
                proc.registers.accumulator = proc.registers.accumulator.plus(BevmByte.fromUnsigned(if (proc.flags.carry) 1u else 0u), proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, true, false, true,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        SUB {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator.plus(-AddressType.resolveAndGet(proc), proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, true, true, false,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        CMP {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator.plus(-AddressType.resolveAndGet(proc), proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, true, true, true,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        LOOP {
            override fun execute(proc: Processor<*>) {
                (--proc.memory[AddressType.resolve(proc.registers.command).resolve(proc) ?: throw InvalidCommandSignal()]).also { i ->
                    if (i.signBit or i.isZero()) {
                        proc.registers.instructionPointer++
                    }
                }
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, false, false, false,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        LD {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = AddressType.resolveAndGet(proc)
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, false, true, false,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        SWAM {
            override fun execute(proc: Processor<*>) {
                val address = AddressType.resolve(proc.registers.command).resolve(proc) ?: throw InvalidCommandSignal()
                val tmp = proc.memory[address]
                proc.memory[address] = proc.registers.accumulator
                proc.registers.accumulator = tmp
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, false, true, true,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        JUMP {
            override fun execute(proc: Processor<*>) {
                proc.registers.instructionPointer = AddressType.resolve(proc.registers.command).resolve(proc) ?: throw InvalidCommandSignal()
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, false, false,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        CALL {
            override fun execute(proc: Processor<*>) {
                proc.memory[--proc.registers.stackPointer] = proc.registers.instructionPointer
                JUMP.execute(proc)
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, false, true,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        ST {
            override fun execute(proc: Processor<*>) {
                proc.memory[AddressType.resolve(proc.registers.command).resolve(proc) ?: throw InvalidCommandSignal()] = proc.registers.accumulator
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, false,
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.resolve(repr).format(repr)}"
        },

        BZS {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.zero) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, false, false, false, false
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BZC {
            override fun execute(proc: Processor<*>) {
                if (!proc.flags.zero) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, false, false, false, true
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BNS {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.sign) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, false, false, true, false
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BNC {
            override fun execute(proc: Processor<*>) {
                if (!proc.flags.sign) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, false, false, true, true
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BCS {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.carry) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, false, true, false, false
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BCC {
            override fun execute(proc: Processor<*>) {
                if (!proc.flags.carry) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, false, true, false, true
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BVS {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.overflow) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, false, true, true, false
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BVC {
            override fun execute(proc: Processor<*>) {
                if (!proc.flags.overflow) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, false, true, false, true
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BLT {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.overflow != proc.flags.sign) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, true, false, false, false
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        },

        BGE {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.overflow == proc.flags.sign) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true, true, false, false, true
            )

            override fun format(repr: BevmByte): String = "${this.mnemonic} ${AddressType.OffsetPointer.format(repr)}"
        };

        abstract val prefix: Array<Boolean>
        open override val mnemonic: String = this::class.simpleName!!
    }

    enum class AddressType {
        AbsolutePointer {
            override fun resolve(proc: Processor<*>): BevmByte = proc.registers.command[0u..10u]
            override val prefix: Array<Boolean> = arrayOf(false)
            override fun format(repr: BevmByte): String = "0x" + repr[0u..10u].formatToStringP()
        },

        OffsetPointerPointer {
            override fun resolve(proc: Processor<*>): BevmByte {
                proc.registers.buffer = proc.registers.command[0u..7u]
                return proc.memory[proc.registers.buffer + proc.registers.instructionPointer]
            }

            override val prefix: Array<Boolean> = arrayOf(true, false, false, false)

            override fun format(repr: BevmByte): String = "*0x" + repr[0u..7u].toUnsigned().toString(16).padStart(2, '0')
        },
        OffsetPointerPointerIncrement {
            override fun resolve(proc: Processor<*>): BevmByte {
                proc.registers.buffer = proc.registers.command[0u..7u]
                proc.memory[proc.registers.buffer + proc.registers.instructionPointer]
                proc.registers.data++
                proc.memory[proc.registers.address] = proc.registers.data
                return --proc.registers.data

            }

            override val prefix: Array<Boolean> = arrayOf(true, false, true, false)
            override fun format(repr: BevmByte): String = "*0x" + repr[0u..7u].toUnsigned().toString(16).padStart(2, '0') + "++"

        },
        OffsetPointerPointerDecrement {
            override fun resolve(proc: Processor<*>): BevmByte {
                proc.registers.buffer = proc.registers.command[0u..7u]
                proc.memory[proc.registers.buffer + proc.registers.instructionPointer]
                proc.registers.data--
                proc.memory[proc.registers.address] = proc.registers.data
                return proc.registers.data
            }

            override val prefix: Array<Boolean> = arrayOf(true, false, true, true)
            override fun format(repr: BevmByte): String = "--*0x" + repr[0u..7u].toUnsigned().toString(16).padStart(2, '0')

        },
        StackOffset {
            override fun resolve(proc: Processor<*>): BevmByte {
                proc.registers.buffer = proc.registers.command[0u..7u]
                proc.registers.data = proc.registers.buffer + proc.registers.stackPointer
                return proc.registers.data
            }

            override val prefix: Array<Boolean> = arrayOf(true, true, false, false)
            override fun format(repr: BevmByte): String = "s0x" + repr[0u..7u].toUnsigned().toString(16).padStart(2, '0')
        },
        OffsetPointer {
            override fun resolve(proc: Processor<*>): BevmByte {
                proc.registers.buffer = proc.registers.command[0u..7u]
                proc.registers.data = proc.registers.buffer + proc.registers.instructionPointer
                return proc.registers.data
            }

            override val prefix: Array<Boolean> = arrayOf(true, true, true, false)
            override fun format(repr: BevmByte): String = "~0x" + repr[0u..7u].toUnsigned().toString(16).padStart(2, '0')
        },
        Constant {
            override fun resolve(proc: Processor<*>): BevmByte? {
                proc.registers.buffer = proc.registers.command[0u..7u]
                proc.registers.data = proc.registers.buffer
                return null
            }

            override val prefix: Array<Boolean> = arrayOf(true, true, true, true)
            override fun format(repr: BevmByte): String = "#0x" + repr[0u..7u].toUnsigned().toString(16).padStart(2, '0')
        };

        abstract fun resolve(proc: Processor<*>): BevmByte?
        abstract val prefix: Array<Boolean>
        abstract fun format(repr: BevmByte): String

        companion object {
            private val searchTree = BinTree<AddressType>()

            init {
                @Suppress("RemoveRedundantQualifierName") for (v in AddressType.values()) {
                    this.searchTree.add(v, *v.prefix.toBooleanArray())
                }
            }

            @Suppress("MemberVisibilityCanBePrivate")
            fun resolve(byte: BevmByte): AddressType = this.searchTree.find(byte, 11u) ?: throw InvalidCommandSignal()

            fun resolveAndGet(proc: Processor<*>, command: BevmByte = proc.registers.command): BevmByte = this.resolve(command).resolve(proc)?.also { a -> proc.memory[a] }.let { proc.registers.data }
        }
    }

    private val searchTree = BinTree<Commands>()

    init {
        for (c in Commands.values()) {
            this.searchTree.add(c, *c.prefix.toBooleanArray())
        }
    }

    override fun parse(repr: BevmByte): Commands = this.searchTree.find(repr, 15u) ?: throw InvalidCommandSignal()
}