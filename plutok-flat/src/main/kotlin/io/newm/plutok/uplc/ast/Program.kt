package io.newm.plutok.uplc.ast

/**
 * This represents a program in Untyped Plutus Core (uplc).
 * It contains a version and a term that requires a generic type.
 */
data class Program<T>(
    val version: Version,
    val term: Term<T>,
)
