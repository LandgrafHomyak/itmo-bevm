package com.github.landgrafhomyak.itmo_bevm

import com.github.landgrafhomyak.itmo_bevm.MicroprogramBuilder.Companion.microprogram
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
}