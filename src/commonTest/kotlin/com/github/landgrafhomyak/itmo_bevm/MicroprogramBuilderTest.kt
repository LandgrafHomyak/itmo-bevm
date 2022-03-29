package com.github.landgrafhomyak.itmo_bevm

import kotlin.test.Test
import kotlin.test.assertFailsWith

class MicroprogramBuilderTest {
    @Test
    fun invalidBitOrder() {
        assertFailsWith(WrongMCBitOrderException::class)
        {
            microprogram {
                OP(WRAC, RDAC)
            }
        }
    }

    @Test
    fun bitDuplication() {
        assertFailsWith(WrongMCBitOrderException::class)
        {
            microprogram {
                OP(RDDR, RDDR)
            }
        }
    }

    @Test
    fun labelDuplication() {
        assertFailsWith(LabelDuplicationException::class)
        {
            microprogram {
                -"LABEL"
                -"LABEL"
            }
        }
    }

    @Test
    fun labelDuplicationInOverwrite() {
        assertFailsWith(LabelDuplicationException::class)
        {
            microprogram {
                -"LABEL"
            }.overwrite {
                -"LABEL"
            }
        }
    }

    @Test
    fun tooLargeProgram() {
        assertFailsWith(IndexOutOfBoundsException::class)
        {
            microprogram {
                at((Microprogram.MICROPROGRAM_SIZE - 2u).toUByte())
                null(2u)
            }
        }
    }
}