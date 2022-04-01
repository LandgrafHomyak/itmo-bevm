package com.github.landgrafhomyak.itmo_bevm

@Suppress("SpellCheckingInspection")
class BevmByte(
    override val bit15: Boolean,
    override val bit14: Boolean,
    override val bit13: Boolean,
    override val bit12: Boolean,
    override val bit11: Boolean,
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
        false, false, false, false,
        false, false, false, false,
        false, false, false, false,
        false, false, false, false
    )

    constructor(addr: AbstractBevmByte) : this(
        addr.bit15, addr.bit14, addr.bit13, addr.bit12,
        addr.bit11, addr.bit10, addr.bit9, addr.bit8,
        addr.bit7, addr.bit6, addr.bit5, addr.bit4,
        addr.bit3, addr.bit2, addr.bit1, addr.bit0
    )
}