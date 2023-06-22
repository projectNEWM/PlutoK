package io.newm.plutok.uplc.ast

import java.math.BigInteger

data class Constr(
    val ix: BigInteger,
    val anyConstructor: BigInteger?,
    val fields: List<PlutusData>,
)
