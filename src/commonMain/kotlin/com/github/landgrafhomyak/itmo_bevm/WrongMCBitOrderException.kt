package com.github.landgrafhomyak.itmo_bevm

class WrongMCBitOrderException(
    @Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
    val bitsOrder: String
) : IllegalArgumentException("Биты переданы в конструктор в неправильном порядке, правильный: $bitsOrder")