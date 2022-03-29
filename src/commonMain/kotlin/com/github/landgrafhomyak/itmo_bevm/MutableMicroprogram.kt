package com.github.landgrafhomyak.itmo_bevm


abstract class MutableMicroprogram : Microprogram {
    abstract override val labels: MutableMap<String, UByte>
    abstract override val commands: Array<Microcommand>

    fun overwrite(mp: MicroprogramBuilder.() -> Unit) {
        val newMP = microprogramWithBuildInfo(mp)
        (this.labels.keys intersect newMP.labels.keys).apply {
            if (isNotEmpty()) {
                throw LabelDuplicationException(first())
            }
        }
        this.labels.putAll(newMP.labels)
        @Suppress("SpellCheckingInspection")
        for (addr in 0u until Microprogram.MICROPROGRAM_SIZE) {
            if (newMP.mask[addr]) {
                this.commands[addr] = newMP.commands[addr]
            }
        }
    }
}