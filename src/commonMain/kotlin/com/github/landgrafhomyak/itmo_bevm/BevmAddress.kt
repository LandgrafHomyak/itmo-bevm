package com.github.landgrafhomyak.itmo_bevm

@Suppress("SpellCheckingInspection")
class BevmAddress(
    override val bit10: Boolean,
    override val bit9: Boolean,
    override val bit8: Boolean,
    override val bit7: Boolean,
    override val bit6: Boolean,
    override val bit5: Boolean,
    override val bit4: Boolean,
    override val bit3: Boolean,
    override val bit2: Boolean,
    override val bit1: Boolean,
    override val bit0: Boolean
) : AbstractBevmByte {
    constructor() : this(
        false, false, false,
        false, false, false, false,
        false, false, false, false
    )

    constructor(byte: AbstractBevmByte) : this(
        byte.bit10, byte.bit9, byte.bit8,
        byte.bit7, byte.bit6, byte.bit5, byte.bit4,
        byte.bit3, byte.bit2, byte.bit1, byte.bit0
    )

    override val bit15: Boolean get() = false
    override val bit14: Boolean get() = false
    override val bit13: Boolean get() = false
    override val bit12: Boolean get() = false
    override val bit11: Boolean get() = false
}