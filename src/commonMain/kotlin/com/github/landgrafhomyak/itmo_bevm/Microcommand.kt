package com.github.landgrafhomyak.itmo_bevm

/**
 *
 */
@Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate", "unused")
sealed class Microcommand {
    abstract val bit39: Boolean
    abstract val bit38: Boolean
    abstract val bit37: Boolean
    abstract val bit36: Boolean
    abstract val bit35: Boolean
    abstract val bit34: Boolean
    abstract val bit33: Boolean
    abstract val bit32: Boolean
    abstract val bit31: Boolean
    abstract val bit30: Boolean
    abstract val bit29: Boolean
    abstract val bit28: Boolean
    abstract val bit27: Boolean
    abstract val bit26: Boolean
    abstract val bit25: Boolean
    abstract val bit24: Boolean
    abstract val bit23: Boolean
    abstract val bit22: Boolean
    abstract val bit21: Boolean
    abstract val bit20: Boolean
    abstract val bit19: Boolean
    abstract val bit18: Boolean
    abstract val bit17: Boolean
    abstract val bit16: Boolean
    abstract val bit15: Boolean
    abstract val bit14: Boolean
    abstract val bit13: Boolean
    abstract val bit12: Boolean
    abstract val bit11: Boolean
    abstract val bit10: Boolean
    abstract val bit9: Boolean
    abstract val bit8: Boolean
    abstract val bit7: Boolean
    abstract val bit6: Boolean
    abstract val bit5: Boolean
    abstract val bit4: Boolean
    abstract val bit3: Boolean
    abstract val bit2: Boolean
    abstract val bit1: Boolean
    abstract val bit0: Boolean

    fun toUnsigned(): ULong = packBE(
        this.bit39, this.bit38, this.bit37, this.bit36, this.bit35, this.bit34, this.bit33, this.bit32,
        this.bit31, this.bit30, this.bit29, this.bit28, this.bit27, this.bit26, this.bit25, this.bit24,
        this.bit23, this.bit22, this.bit21, this.bit20, this.bit19, this.bit18, this.bit17, this.bit16,
        this.bit15, this.bit14, this.bit13, this.bit12, this.bit11, this.bit10, this.bit9, this.bit8,
        this.bit7, this.bit6, this.bit5, this.bit4, this.bit3, this.bit2, this.bit1, this.bit0,
    )

    object Empty : Microcommand() {
        override val bit39 get() = false
        override val bit38 get() = false
        override val bit37 get() = false
        override val bit36 get() = false
        override val bit35 get() = false
        override val bit34 get() = false
        override val bit33 get() = false
        override val bit32 get() = false
        override val bit31 get() = false
        override val bit30 get() = false
        override val bit29 get() = false
        override val bit28 get() = false
        override val bit27 get() = false
        override val bit26 get() = false
        override val bit25 get() = false
        override val bit24 get() = false
        override val bit23 get() = false
        override val bit22 get() = false
        override val bit21 get() = false
        override val bit20 get() = false
        override val bit19 get() = false
        override val bit18 get() = false
        override val bit17 get() = false
        override val bit16 get() = false
        override val bit15 get() = false
        override val bit14 get() = false
        override val bit13 get() = false
        override val bit12 get() = false
        override val bit11 get() = false
        override val bit10 get() = false
        override val bit9 get() = false
        override val bit8 get() = false
        override val bit7 get() = false
        override val bit6 get() = false
        override val bit5 get() = false
        override val bit4 get() = false
        override val bit3 get() = false
        override val bit2 get() = false
        override val bit1 get() = false
        override val bit0 get() = false
    }

    sealed class NotEmpty(
        val rrL: RegisterReadingLeft?,
        val rrR: RegisterReadingRight?,
        val invL: Boolean,
        val invR: Boolean,
        val aluOp: ArithmeticalLogicUnitOperation
    ) : Microcommand() {
        @Suppress("LeakingThis")
        override val bit39
            get() = when (this) {
                is Operation -> false
                is Control   -> true
            }
        override val bit11 by aluOp::bit11
        override val bit10 by aluOp::bit10
        override val bit9 by ::invL
        override val bit8 by ::invR
        override val bit7 get() = this.rrL?.bit7 ?: false
        override val bit6 get() = this.rrL?.bit6 ?: false
        override val bit5 get() = this.rrL?.bit5 ?: false
        override val bit4 get() = this.rrL?.bit4 ?: false
        override val bit3 get() = this.rrR?.bit3 ?: false
        override val bit2 get() = this.rrR?.bit2 ?: false
        override val bit1 get() = this.rrR?.bit1 ?: false
        override val bit0 get() = this.rrR?.bit0 ?: false
    }

    /**
     * Операционная микрокоманда (ОМК)
     */
    class Operation(
        rrL: RegisterReadingLeft?,
        rrR: RegisterReadingRight?,
        invL: Boolean,
        invR: Boolean,
        aluOp: ArithmeticalLogicUnitOperation,
        val cmtBs: ComutatorSourceCombination?,
        val setC: Boolean,
        val setV: Boolean,
        val setNZ: Boolean,
        val sAC: Boolean,
        val sDR: Boolean,
        val sBR: Boolean,
        val sCR: Boolean,
        val sPS: Boolean,
        val sIP: Boolean,
        val sSP: Boolean,
        val sAR: Boolean,
        val mem: MemoryAccess?,
        val io: Boolean,
        val ints: Boolean,
        val stop: Boolean
    ) : NotEmpty(rrL, rrR, invL, invR, aluOp) {
        override val bit38 by ::stop
        override val bit37 get() = false
        override val bit36 get() = false
        override val bit35 by ::ints
        override val bit34 by ::io
        override val bit33 get() = this.mem?.bit33 ?: false
        override val bit32 get() = this.mem?.bit32 ?: false
        override val bit31 by ::sAR
        override val bit30 by ::sPS
        override val bit29 by ::sBR
        override val bit28 by ::sAC
        override val bit27 by ::sSP
        override val bit26 by ::sIP
        override val bit25 by ::sCR
        override val bit24 by ::sDR
        override val bit23 by ::setNZ
        override val bit22 by ::setV
        override val bit21 by ::setC
        override val bit20 get() = this.cmtBs?.bit20 ?: false
        override val bit19 get() = this.cmtBs?.bit19 ?: false
        override val bit18 get() = this.cmtBs?.bit18 ?: false
        override val bit17 get() = this.cmtBs?.bit17 ?: false
        override val bit16 get() = this.cmtBs?.bit16 ?: false
        override val bit15 get() = this.cmtBs?.bit15 ?: false
        override val bit14 get() = this.cmtBs?.bit14 ?: false
        override val bit13 get() = this.cmtBs?.bit13 ?: false
        override val bit12 get() = this.cmtBs?.bit12 ?: false
    }

    /**
     * Управляющая микрокоманда (УМК)
     */
    class Control(
        rrL: RegisterReadingLeft?,
        rrR: RegisterReadingRight?,
        invL: Boolean,
        invR: Boolean,
        aluOp: ArithmeticalLogicUnitOperation,
        val cmtLbs: CommutatorLoByteSource?,
        val cmtHbs: CommutatorHiByteSource?,
        val chB0: Boolean,
        val chB1: Boolean,
        val chB2: Boolean,
        val chB3: Boolean,
        val chB4: Boolean,
        val chB5: Boolean,
        val chB6: Boolean,
        val chB7: Boolean,
        val addr: UByte,
        val cmpB: Boolean
    ) : NotEmpty(rrL, rrR, invL, invR, aluOp) {
        override val bit38 get() = false
        override val bit37 get() = false
        override val bit36 get() = false
        override val bit35 get() = false
        override val bit34 get() = false
        override val bit33 get() = false
        override val bit32 by ::cmpB
        private val addrUnpacked = unpack(this.addr.toULong(), 8u)
        override val bit31 get() = this.addrUnpacked[7]
        override val bit30 get() = this.addrUnpacked[6]
        override val bit29 get() = this.addrUnpacked[5]
        override val bit28 get() = this.addrUnpacked[4]
        override val bit27 get() = this.addrUnpacked[3]
        override val bit26 get() = this.addrUnpacked[2]
        override val bit25 get() = this.addrUnpacked[1]
        override val bit24 get() = this.addrUnpacked[0]
        override val bit23 by ::chB7
        override val bit22 by ::chB6
        override val bit21 by ::chB5
        override val bit20 by ::chB4
        override val bit19 by ::chB3
        override val bit18 by ::chB2
        override val bit17 by ::chB1
        override val bit16 by ::chB0
        override val bit15 get() = this.cmtHbs?.bit15 ?: false
        override val bit14 get() = this.cmtLbs?.bit14 ?: false
        override val bit13 get() = this.cmtHbs?.bit13 ?: false
        override val bit12 get() = this.cmtLbs?.bit12 ?: false
    }
}