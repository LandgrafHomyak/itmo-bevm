package com.github.landgrafhomyak.itmo_bevm

class BinTree<T : Any> {
    private val top = Node<T>()

    class Node<T : Any>(
        var zero: Node<T>? = null,
        var one: Node<T>? = null,
        var value: T? = null
    )

    fun add(elem: T, vararg seq: Boolean): Boolean {
        var p = this.top
        for (b in seq) {
            if (b) {
                if (p.one == null) {
                    p.one = Node()
                }
                p = p.one!!
            } else {
                if (p.zero == null) {
                    p.zero = Node()
                }
                p = p.zero!!
            }
        }
        if (p.value != null) return false
        p.value = elem
        return true
    }

    fun find(byte: BevmByte, start: UInt = BevmByte.BITS_SIZE - 1u): T? = this.find(iterator { for (b in start downTo 0u) yield(byte[b]) })

    @Suppress("MemberVisibilityCanBePrivate")
    inline fun find(seq: Iterator<Boolean>): T? = this.find(seq.asSequence().asIterable())
    @Suppress("unused")
    inline fun find(vararg seq: Boolean): T? = this.find(seq.asIterable())
    fun find(seq: Iterable<Boolean>): T? {
        var p = this.top
        for (b in seq) {
            p = if (b) {
                p.one ?: return null
            } else {
                p.zero ?: return null
            }
            return p.value ?: continue
        }
        return p.value
    }
}