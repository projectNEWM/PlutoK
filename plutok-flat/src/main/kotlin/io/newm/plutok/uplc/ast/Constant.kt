package io.newm.plutok.uplc.ast

import java.math.BigInteger

sealed interface Constant

// tag: 0
data class IntegerConstant(val value: BigInteger) : Constant

// tag: 1
data class ByteStringConstant(val value: ByteArray) : Constant {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteStringConstant) return false

        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

// tag: 2
data class StringConstant(val value: String) : Constant

// tag: 3
object UnitConstant : Constant

// tag: 4
data class BoolConstant(val value: Boolean) : Constant

// tag: 5
data class ProtoListConstant(val type: Type, val value: List<Constant>) : Constant

// tag: 6
data class ProtoPairConstant(val type1: Type, val type2: Type, val value1: Constant, val value2: Constant) : Constant

// tag: 7
data class ApplyConstant(val value: Constant, val type: Type) : Constant

// tag: 8
data class DataConstant(val value: PlutusData) : Constant
