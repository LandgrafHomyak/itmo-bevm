package com.github.landgrafhomyak.itmo_bevm

import com.github.landgrafhomyak.itmo_bevm.MicroprogramBuilder.Companion.microprogram


object DefaultMicroprogram : Microprogram {
    private inline fun MicroprogramBuilder.goto(label: String) = CTRL(RDPS, LTOL, 0b00010000u, false, label)
    private inline fun MicroprogramBuilder.ccr(bit: UByte, expected: Boolean, label: String) = CTRL(RDCR, if (bit >= 8u) HTOL else LTOL, 1u shl (if (bit >= 8u) (bit.toInt() - 8) else bit.toInt()), expected, label)

    @Suppress("SpellCheckingInspection")
    private val origin = microprogram {
        at(0x01u)
        -"INFETCH"
        mov(RDIP, WRBR, WRAR)
        OP(RDBR, PLS1, LTOL, HTOH, WRIP, LOAD)
        mov(RDDR, WRCR)
        ccr(15u, true, "CHKBR")
        ccr(14u, true, "CHKABS")
        ccr(13u, true, "CHKABS")
        ccr(12u, false, "ADDRLESS")
        goto("IO")
        -"CHKBR"
        ccr(14u, false, "CHKABS")
        ccr(13u, false, "CHKABS")
        ccr(12u, true, "BRANCHES")
        -"CHKABS"
        ccr(11u, false, "OPFETCH")
        -"ADFETCH"
        OP(RDCR, LTOL, SEXT, WRBR)
        ccr(10u, true, "T11XX")
        -"T10XX"
        sum(RDBR, RDIP, WRAR)
        OP(LOAD)
        ccr(9u, true, "T101X")
        -"T100X"
        ccr(8u, true, "RESERVED")
        -"T1000"
        goto("OPFETCH")
        -"T101X"
        ccr(8u, true, "T1011")
        -"T1010"
        OP(RDDR, PLS1, LTOL, HTOH, WRDR)
        OP(STOR)
        OP(RDDR, COML, LTOL, HTOH, WRDR)
        goto("OPFETCH")
        -"T1011"
        OP(RDDR, COML, LTOL, HTOH, WRDR)
        OP(STOR)
        goto("OPFETCH")
        -"T11XX"
        ccr(9u, true, "T111X")
        -"T110X"
        ccr(8u, true, "RESERVED")
        -"T1100"
        sum(RDBR, RDSP, WRDR)
        goto("OPFETCH")
        -"T111X"
        ccr(8u, false, "T1110")
        -"T1111"
        mov(RDBR, WRDR)
        goto("EXEC")
        -"T1110"
        sum(RDBR, RDIP, WRDR)
        -"OPFETCH"
        ccr(15u, false, "RDVALUE")
        ccr(14u, true, "CMD11XX")
        -"RDVALUE"
        mov(RDDR, WRAR)
        OP(LOAD)
        -"EXEC"
        ccr(15u, true, "CMD1XXX")
        -"CMD0XXX"
        ccr(14u, true, "CMD01XX")
        -"CMD000X"
        ccr(12u, true, "OR")
        -"AND"
        OP(RDAC, RDDR, SORA, LTOL, HTOH, SETV, STNZ, WRAC)
        goto("INT")
        -"OR"
        OP(RDAC, RDDR, COML, COMR, SORA, LTOL, HTOH, WRBR)
        OP(RDBR, COML, LTOL, HTOH, SETV, STNZ, WRAC)
        goto("INT")
        -"CMD01XX"
        ccr(13u, true, "CMD011X")
        -"CMD010X"
        ccr(12u, true, "ADC")
        -"ADD"
        OP(RDAC, RDDR, LTOL, HTOH, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"ADC"
        CTRL(RDPS, LTOL, 0b00000001u, false, "ADD")
        OP(RDAC, RDDR, PLS1, LTOL, HTOH, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"CMD011X"
        ccr(12u, true, "CMP")
        -"SUB"
        OP(RDAC, RDDR, COMR, PLS1, LTOL, HTOH, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"CMP"
        OP(RDAC, RDDR, COMR, PLS1, LTOL, HTOH, SETC, SETV, STNZ)
        goto("INT")
        -"CMD1XXX"
        ccr(13u, true, "CMD101X")
        -"CMD100X"
        ccr(12u, true, "RESERVED")
        -"LOOP"
        OP(RDDR, COML, LTOL, HTOH, WRDR)
        OP(RDDR, COML, LTOL, HTOH, WRBR, STOR)
        CTRL(RDBR, HTOL, 0b10000000u, false, "INT")
        OP(RDIP, PLS1, LTOL, HTOH, WRIP)
        goto("INT")
        -"CMD101X"
        ccr(12u, true, "SWAM")
        -"LD"
        OP(RDDR, LTOL, HTOH, SETV, STNZ, WRAC)
        goto("INT")
        -"SWAM"
        mov(RDDR, WRBR)
        mov(RDAC, WRDR)
        OP(RDBR, LTOL, HTOH, SETV, STNZ, WRAC, STOR)
        goto("INT")
        -"CMD11XX"
        ccr(13u, true, "ST")
        -"CMD110X"
        ccr(12u, true, "CALL")
        -"JUMP"
        mov(RDDR, WRIP)
        goto("INT")
        -"CALL"
        mov(RDDR, WRBR)
        mov(RDIP, WRDR)
        mov(RDBR, WRIP)
        -"PUSHVAL"
        OP(RDSP, COML, LTOL, HTOH, WRSP, WRAR)
        goto("STORE")
        -"ST"
        mov(RDDR, WRAR)
        mov(RDAC, WRDR)
        -"STORE"
        OP(STOR)
        goto("INT")
        -"BRANCHES"
        ccr(11u, true, "BR1XXX")
        -"BR0XXX"
        ccr(10u, true, "BR01XX")
        -"BR00XX"
        ccr(9u, true, "BR001X")
        -"BR000X"
        ccr(8u, true, "BNE")
        -"BEQ"
        CTRL(RDPS, LTOL, 0b00000100u, false, "INT")
        -"BR"
        OP(RDCR, LTOL, SEXT, WRBR)
        sum(RDBR, RDIP, WRIP)
        goto("INT")
        -"BNE"
        CTRL(RDPS, LTOL, 0b00000100u, false, "BR")
        goto("INT")
        -"BR001X"
        ccr(8u, true, "BPL")
        -"BMI"
        CTRL(RDPS, LTOL, 0b00001000u, true, "BR")
        goto("INT")
        -"BPL"
        CTRL(RDPS, LTOL, 0b00001000u, false, "BR")
        goto("INT")
        -"BR01XX"
        ccr(9u, true, "BR011X")
        -"BR010X"
        ccr(8u, true, "BCC")
        -"BCS"
        CTRL(RDPS, LTOL, 0b00000001u, true, "BR")
        goto("INT")
        -"BCC"
        CTRL(RDPS, LTOL, 0b00000001u, false, "BR")
        goto("INT")
        -"BR011X"
        ccr(8u, true, "BVC")
        -"BVS"
        CTRL(RDPS, LTOL, 0b00000010u, true, "BR")
        goto("INT")
        -"BVC"
        CTRL(RDPS, LTOL, 0b00000010u, false, "BR")
        goto("INT")
        -"BR1XXX"
        ccr(10u, true, "RESERVED")
        -"BR10XX"
        ccr(9u, true, "RESERVED")
        -"BR100X"
        ccr(8u, true, "BGE")
        -"BLT"
        CTRL(RDPS, LTOL, 0b00001000u, false, "BVS")
        goto("BVC")
        -"BGE"
        CTRL(RDPS, LTOL, 0b00001000u, false, "BVC")
        goto("BVS")
        -"ADDRLESS"
        ccr(11u, true, "AL1XXX")
        -"AL0XXX"
        ccr(10u, true, "AL01XX")
        -"AL00XX"
        ccr(9u, true, "AL001X")
        -"AL000X"
        ccr(8u, false, "INT")
        -"HLT"
        goto("STOP")
        -"AL001X"
        ccr(8u, true, "AL0011")
        -"AL0010"
        ccr(7u, true, "NOT")
        -"CLA"
        OP(SETV, STNZ, WRAC)
        goto("INT")
        -"NOT"
        OP(RDAC, COML, LTOL, HTOH, SETV, STNZ, WRAC)
        goto("INT")
        -"AL0011"
        ccr(7u, true, "CMC")
        -"CLC"
        OP(SETC)
        goto("INT")
        -"CMC"
        CTRL(RDPS, LTOL, 0b00000001u, true, "CLC")
        OP(COML, COMR, HTOH, SETC)
        goto("INT")
        -"AL01XX"
        ccr(9u, true, "AL011X")
        -"AL010X"
        ccr(8u, true, "AL0101")
        -"AL0100"
        ccr(7u, true, "ROR")
        -"ROL"
        OP(RDAC, SHLT, SHL0, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"ROR"
        OP(RDAC, SHRT, SHRF, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"AL0101"
        ccr(7u, true, "ASR")
        -"ASL"
        /* OMC(RDAC, SHLT, SETC, SETV, STNZ, WRAC) */
        mov(RDAC, WRDR)
        /*
        Заменить для экономии памяти
        goto("ADD")
     */
        OP(RDAC, RDDR, LTOL, HTOH, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"ASR"
        OP(RDAC, SHRT, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"AL011X"
        ccr(8u, true, "AL0111")
        -"AL0110"
        ccr(7u, true, "SWAB")
        -"SXTB"
        OP(RDAC, LTOL, SEXT, SETV, STNZ, WRAC)
        goto("INT")
        -"SWAB"
        OP(RDAC, HTOL, LTOH, SETV, STNZ, WRAC)
        goto("INT")
        -"AL0111"
        ccr(7u, true, "NEG")
        -"AL01110"
        ccr(6u, true, "DEC")
        -"INC"
        OP(RDAC, PLS1, LTOL, HTOH, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"DEC"
        OP(RDAC, COMR, LTOL, HTOH, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"NEG"
        OP(RDAC, COML, PLS1, LTOL, HTOH, SETC, SETV, STNZ, WRAC)
        goto("INT")
        -"AL1XXX"
        ccr(10u, true, "AL11XX")
        -"AL10XX"
        mov(RDSP, WRAR)
        OP(LOAD)
        ccr(9u, true, "AL101X")
        -"AL100X"
        ccr(8u, true, "POPF")
        -"POP"
        OP(RDDR, LTOL, HTOH, SETV, STNZ, WRAC)
        -"INCSP"
        OP(RDSP, PLS1, LTOL, HTOH, WRSP)
        goto("INT")
        -"POPF"
        mov(RDDR, WRPS)
        goto("INCSP")
        -"AL101X"
        ccr(8u, true, "IRET")
        -"RET"
        mov(RDDR, WRIP)
        goto("INCSP")
        -"IRET"
        mov(RDDR, WRPS)
        OP(RDSP, PLS1, LTOL, HTOH, WRSP, WRAR)
        OP(LOAD)
        goto("RET")
        -"AL11XX"
        ccr(9u, true, "AL111X")
        -"AL110X"
        ccr(8u, true, "PUSHF")
        -"PUSH"
        mov(RDAC, WRDR)
        goto("PUSHVAL")
        -"PUSHF"
        mov(RDPS, WRDR)
        goto("PUSHVAL")
        -"AL111X"
        ccr(8u, true, "RESERVED")
        -"SWAP"
        mov(RDSP, WRAR)
        OP(LOAD)
        mov(RDDR, WRBR)
        mov(RDAC, WRDR)
        OP(RDBR, LTOL, HTOH, SETV, STNZ, WRAC, STOR)
        goto("INT")
        -"IO"
        ccr(11u, true, "IRQ")
        -"DOIO"
        OP(IO)
        -"INT"
        CTRL(RDPS, LTOL, 0b10000000u, false, "STOP")
        CTRL(RDPS, LTOL, 0b01000000u, false, "INFETCH")
        OP(INTS)
        -"IRQ"
        OP(RDSP, COML, LTOL, HTOH, WRSP, WRAR)
        mov(RDIP, WRDR)
        OP(STOR)
        OP(RDSP, COML, LTOL, HTOH, WRSP, WRAR)
        mov(RDPS, WRDR)
        OP(RDCR, LTOL, WRBR, STOR)
        OP(RDBR, SHLT, WRBR, WRAR)
        OP(LOAD)
        mov(RDDR, WRIP)
        OP(RDBR, PLS1, LTOL, WRAR)
        OP(LOAD)
        mov(RDDR, WRPS)
        goto("INFETCH")
        -"START"
        OP(SETC, SETV, STNZ, WRAC, WRDR, WRBR, WRCR, WRSP, WRAR)
        goto("DOIO")
        -"READ"
        mov(RDIP, WRAR)
        OP(RDIP, PLS1, LTOL, HTOH, WRIP, LOAD)
        goto("STOP")
        -"WRITE"
        mov(RDIP, WRAR)
        mov(RDIR, WRDR)
        OP(RDIP, PLS1, LTOL, HTOH, WRIP, STOR)
        goto("STOP")
        -"SETIP"
        mov(RDIR, WRIP)
        -"STOP"
        OP(HALT)
        goto("INFETCH")
        -"RESERVED"
    }

    override val labels: Map<String, UByte>
        get() = this.origin.labels
    override val commands: Array<out Microcommand>
        get() = this.origin.commands
}