package com.github.landgrafhomyak.itmo_bevm

object DefaultCommandRegistry : AbstractCommandRegistry<DefaultCommandRegistry.Commands>() {
    @Suppress("unused")
    enum class Commands : AbstractCommand {
        NOP {
            override fun execute(proc: Processor<*>) {}
            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            )
        },

        HLT {
            override fun execute(proc: Processor<*>) {
                throw ShutdownSignal()
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false
            )
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
        },

        CLC {
            override fun execute(proc: Processor<*>) {
                proc.flags.carry = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, true, true, false, false, false, false, false, false, false, false
            )
        },

        CMC {
            override fun execute(proc: Processor<*>) {
                proc.flags.carry = !proc.flags.carry
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, false, true, true, true, false, false, false, false, false, false, false
            )
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
        },

        @Suppress("SpellCheckingInspection")
        SWAB {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = BevmByte.fromBits(
                    *(8u..15u).map(proc.registers.accumulator::bit).toBooleanArray(),
                    *(0u..7u).map(proc.registers.accumulator::bit).toBooleanArray()
                )
                proc.flags.recalcFromAccumulator()
                proc.flags.overflow = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, false, true, false, false, false, false, false, false, false
            )
        },

        INC {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator.plus(1u, proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, true, false, false, false, false, false, false, false, false
            )
        },

        DEC {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator.plus(!BevmByte.fromUnsigned(0u), proc.flags)
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, true, false, true, false, false, false, false, false, false
            )
        },

        NEG {
            override fun execute(proc: Processor<*>) {
                NOT.execute(proc)
                INC.execute(proc)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, false, true, true, true, true, false, false, false, false, false, false, false
            )
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
        },

        @Suppress("SpellCheckingInspection")
        POPF {
            override fun execute(proc: Processor<*>) {
                proc.registers.programState = proc.memory[proc.registers.stackPointer++]
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, false, false, true, false, false, false, false, false, false, false, false
            )
        },

        RET {
            override fun execute(proc: Processor<*>) {
                proc.registers.instructionPointer = proc.memory[proc.registers.stackPointer++]
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, false, true, false, false, false, false, false, false, false, false, false
            )
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
        },

        PUSH {
            override fun execute(proc: Processor<*>) {
                proc.memory[--proc.registers.stackPointer] = proc.registers.accumulator
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, true, false, false, false, false, false, false, false, false, false, false
            )
        },

        @Suppress("SpellCheckingInspection")
        PUSHF {
            override fun execute(proc: Processor<*>) {
                proc.memory[--proc.registers.stackPointer] = proc.registers.programState
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, false, true, true, false, true, false, false, false, false, false, false, false, false
            )
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
        },

        DI {
            override fun execute(proc: Processor<*>) {
                proc.flags.allowInterruption = false
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, true,
                false, false, false, false,
            )
        },

        EI {
            override fun execute(proc: Processor<*>) {
                proc.flags.allowInterruption = true
            }


            override val prefix: Array<Boolean> = arrayOf(
                false, false, false, true,
                false, false, false, true,
            )
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
        },

        ADD {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator.plus(AddressType.resolveAndGet(proc), proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, true, false, false,
            )
        },

        ADC {
            override fun execute(proc: Processor<*>) {
                ADD.execute(proc)
                proc.registers.accumulator = proc.registers.accumulator.plus(BevmByte.fromUnsigned(if (proc.flags.carry) 1u else 0u), proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, true, false, true,
            )
        },

        SUB {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator = proc.registers.accumulator.plus(-AddressType.resolveAndGet(proc), proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, true, true, false,
            )
        },

        CMP {
            override fun execute(proc: Processor<*>) {
                proc.registers.accumulator.plus(-AddressType.resolveAndGet(proc), proc.flags)
            }

            override val prefix: Array<Boolean> = arrayOf(
                false, true, true, true,
            )
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
        },

        JUMP {
            override fun execute(proc: Processor<*>) {
                proc.registers.instructionPointer = AddressType.resolve(proc.registers.command).resolve(proc) ?: throw InvalidCommandSignal()
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, false, false,
            )
        },

        CALL {
            override fun execute(proc: Processor<*>) {
                proc.memory[--proc.registers.stackPointer] = proc.registers.instructionPointer
                JUMP.execute(proc)
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, false, true,
            )
        },

        ST {
            override fun execute(proc: Processor<*>) {
                proc.memory[AddressType.resolve(proc.registers.command).resolve(proc) ?: throw InvalidCommandSignal()] = proc.registers.accumulator
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, false,
            )
        },

        BZS {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.zero) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                false, false, false, false
            )
        },

        BZC {
            override fun execute(proc: Processor<*>) {
                if (!proc.flags.zero) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                false, false, false, true
            )
        },

        BNS {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.sign) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                false, false, true, false
            )
        },

        BNC {
            override fun execute(proc: Processor<*>) {
                if (!proc.flags.sign) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                false, false, true, true
            )
        },

        BCS {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.carry) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                false, true, false, false
            )
        },

        BCC {
            override fun execute(proc: Processor<*>) {
                if (!proc.flags.carry) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                false, true, false, true
            )
        },

        BVS {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.overflow) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                false, true, true, false
            )
        },

        BVC {
            override fun execute(proc: Processor<*>) {
                if (!proc.flags.overflow) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }


            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                false, true, false, true
            )
        },

        BLT {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.overflow != proc.flags.sign) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                true, false, false, false
            )
        },

        BGE {
            override fun execute(proc: Processor<*>) {
                if (proc.flags.overflow == proc.flags.sign) proc.registers.instructionPointer = AddressType.OffsetPointer.resolve(proc)!!
            }

            override val prefix: Array<Boolean> = arrayOf(
                true, true, true, true,
                true, false, false, true
            )
        };

        abstract val prefix: Array<Boolean>
        open override val mnemonic: String = this::class.simpleName!!
    }

    enum class AddressType {
        AbsolutePointer {
            override fun resolve(proc: Processor<*>): BevmByte = proc.registers.command[0u..10u]
            override val prefix: Array<Boolean> = arrayOf(false)
        },

        OffsetPointerPointer {
            override fun resolve(proc: Processor<*>): BevmByte {
                proc.registers.buffer = proc.registers.command[0u..7u]
                return proc.memory[proc.registers.buffer + proc.registers.instructionPointer]
            }

            override val prefix: Array<Boolean> = arrayOf(true, false, false, false)

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
        },
        StackOffset {
            override fun resolve(proc: Processor<*>): BevmByte {
                proc.registers.buffer = proc.registers.command[0u..7u]
                proc.registers.data = proc.registers.buffer + proc.registers.stackPointer
                return proc.registers.data
            }

            override val prefix: Array<Boolean> = arrayOf(true, true, false, false)
        },
        OffsetPointer {
            override fun resolve(proc: Processor<*>): BevmByte {
                proc.registers.buffer = proc.registers.command[0u..7u]
                proc.registers.data = proc.registers.buffer + proc.registers.instructionPointer
                return proc.registers.data
            }

            override val prefix: Array<Boolean> = arrayOf(true, true, true, false)
        },
        Constant {
            override fun resolve(proc: Processor<*>): BevmByte? {
                proc.registers.buffer = proc.registers.command[0u..7u]
                proc.registers.data = proc.registers.buffer
                return null
            }

            override val prefix: Array<Boolean> = arrayOf(true, true, true, true)
        };

        abstract fun resolve(proc: Processor<*>): BevmByte?
        abstract val prefix: Array<Boolean>

        companion object {
            private val searchTree = BinTree<AddressType>()

            init {
                @Suppress("RemoveRedundantQualifierName")
                for (v in AddressType.values()) {
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