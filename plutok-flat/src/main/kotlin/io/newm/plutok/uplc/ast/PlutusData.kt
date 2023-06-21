package io.newm.plutok.uplc.ast

import java.math.BigInteger

sealed interface PlutusData

data class ConstrPlutusData(val constr: Int, val plutusData: PlutusData) : PlutusData

data class BigIntegerPlutusData(val value: BigInteger) : PlutusData

data class ByteArrayPlutusData(val value: ByteArray) : PlutusData {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ByteArrayPlutusData) return false

        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}

data class ArrayPlutusData(val value: List<PlutusData>) : PlutusData

data class MapPlutusData(val value: List<Pair<PlutusData, PlutusData>>) : PlutusData
