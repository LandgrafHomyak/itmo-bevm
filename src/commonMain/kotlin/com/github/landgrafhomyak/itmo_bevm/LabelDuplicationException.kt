package com.github.landgrafhomyak.itmo_bevm

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class LabelDuplicationException(val label: String) : IllegalArgumentException("Goto label with name '$label' already exists")