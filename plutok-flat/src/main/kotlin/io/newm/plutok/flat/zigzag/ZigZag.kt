package io.newm.plutok.flat.zigzag

import java.math.BigInteger

fun BigInteger.zigZagEncode(): BigInteger {
    // check if positive
    return if (this >= BigInteger.ZERO) {
        this.multiply(BigInteger.TWO)
    } else {
        this.multiply(BigInteger.TWO).negate().subtract(BigInteger.ONE)
    }
}

fun BigInteger.zigZagDecode(): BigInteger {
    val shiftedValue = this.shiftRight(1)
    val signBit = this.and(BigInteger.ONE).negate()
    return shiftedValue.xor(signBit)
}
