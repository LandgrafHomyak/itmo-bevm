@file:Suppress("SpellCheckingInspection")

package com.github.landgrafhomyak.itmo_bevm

sealed interface RegisterReading

/**
 * Коды микрокоманд для передачи регистров на правый вход АЛУ
 */
enum class RegisterReadingRight : RegisterReading {
    /**
     * регистр DR -> правый вход АЛУ
     */
    RDDR {
        override val bit3 = false
        override val bit2 = false
        override val bit1 = false
        override val bit0 = true
    },

    /**
     * регистр CR -> правый вход АЛУ
     */
    RDCR {
        override val bit3 = false
        override val bit2 = false
        override val bit1 = true
        override val bit0 = false
    },

    /**
     * регистр IP -> правый вход АЛУ
     */
    RDIP {
        override val bit3 = false
        override val bit2 = true
        override val bit1 = false
        override val bit0 = false
    },

    /**
     * регистр SP -> правый вход АЛУ
     */
    RDSP {
        override val bit3 = true
        override val bit2 = false
        override val bit1 = false
        override val bit0 = false
    };

    abstract val bit3: Boolean
    abstract val bit2: Boolean
    abstract val bit1: Boolean
    abstract val bit0: Boolean
}

/**
 * Коды микрокоманд для передачи регистров на левый вход АЛУ
 */
enum class RegisterReadingLeft : RegisterReading {
    /**
     * регистр AC -> левый вход АЛУ
     */
    RDAC {
        override val bit7 = false
        override val bit6 = false
        override val bit5 = false
        override val bit4 = true
    },

    /**
     * регистр BR -> левый вход АЛУ
     */
    RDBR {
        override val bit7 = false
        override val bit6 = false
        override val bit5 = true
        override val bit4 = false
    },

    /**
     * регистр PS -> левый вход АЛУ
     */
    RDPS {
        override val bit7 = false
        override val bit6 = true
        override val bit5 = false
        override val bit4 = false
    },

    /**
     * регистр IR -> левый вход АЛУ
     */
    RDIR {
        override val bit7 = true
        override val bit6 = false
        override val bit5 = false
        override val bit4 = false
    };

    abstract val bit7: Boolean
    abstract val bit6: Boolean
    abstract val bit5: Boolean
    abstract val bit4: Boolean
}

enum class ArithmeticalLogicUnitOperation {
    PLS {
        override val bit11 = false
        override val bit10 = false
    },
    PLS1 {
        override val bit11 = false
        override val bit10 = true
    },
    SORA {
        override val bit11 = true
        override val bit10 = false
    };

    abstract val bit11: Boolean
    abstract val bit10: Boolean
}

enum class CommutatorLoByteSource {
    LTOL {
        override val bit14 = false
        override val bit12 = true
    },
    HTOL {
        override val bit14 = true
        override val bit12 = false
    };

    abstract val bit14: Boolean
    abstract val bit12: Boolean
}

enum class CommutatorHiByteSource {
    LTOH {
        override val bit16 = false
        override val bit15 = false
        override val bit13 = true
    },
    HTOH {
        override val bit16 = false
        override val bit15 = true
        override val bit13 = false
    },
    SEXT {
        override val bit16 = true
        override val bit15 = false
        override val bit13 = false
    };

    abstract val bit16: Boolean
    abstract val bit15: Boolean
    abstract val bit13: Boolean
}

sealed class ComutatorSourceCombination {
    data class Forward(
        val lo: CommutatorLoByteSource?,
        val hi: CommutatorHiByteSource?
    ) : ComutatorSourceCombination() {
        override val bit20 get() = false
        override val bit19 get() = false
        override val bit18 get() = false
        override val bit17 get() = false
        override val bit16 get() = this.hi?.bit16 ?: false
        override val bit15 get() = this.hi?.bit15 ?: false
        override val bit14 get() = this.lo?.bit14 ?: false
        override val bit13 get() = this.hi?.bit13 ?: false
        override val bit12 get() = this.lo?.bit12 ?: false
    }

    class SHL(
        @Suppress("MemberVisibilityCanBePrivate")
        val shl0: Boolean
    ) : ComutatorSourceCombination() {
        override val bit20 get() = false
        override val bit19 get() = false
        override val bit18 by ::shl0
        override val bit17 get() = true
        override val bit16 get() = false
        override val bit15 get() = false
        override val bit14 get() = false
        override val bit13 get() = false
        override val bit12 get() = false
    }

    class SHR(
        @Suppress("MemberVisibilityCanBePrivate")
        val shrF: Boolean
    ) : ComutatorSourceCombination() {
        override val bit20 by ::shrF
        override val bit19 get() = true
        override val bit18 get() = false
        override val bit17 get() = false
        override val bit16 get() = false
        override val bit15 get() = false
        override val bit14 get() = false
        override val bit13 get() = false
        override val bit12 get() = false
    }

    abstract val bit20: Boolean
    abstract val bit19: Boolean
    abstract val bit18: Boolean
    abstract val bit17: Boolean
    abstract val bit16: Boolean
    abstract val bit15: Boolean
    abstract val bit14: Boolean
    abstract val bit13: Boolean
    abstract val bit12: Boolean
}

enum class MemoryAccess {
    LOAD {
        override val bit33 = false
        override val bit32 = true
    },
    STOR {
        override val bit33 = true
        override val bit32 = false
    };

    abstract val bit33: Boolean
    abstract val bit32: Boolean
}