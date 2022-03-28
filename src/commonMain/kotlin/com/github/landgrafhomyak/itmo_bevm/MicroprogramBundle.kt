package com.github.landgrafhomyak.itmo_bevm

open class MicroprogramBundle(
    val labels: Map<String, UByte>,
    val commands: Array<Microcommand>
)