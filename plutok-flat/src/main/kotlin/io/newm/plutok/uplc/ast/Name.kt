package io.newm.plutok.uplc.ast

import kotlinx.serialization.Serializable

/**
 * A Name containing it's parsed textual representation
 * and a unique id from string interning. The Name's text is
 * interned during parsing.
 */
@Serializable
data class Name(
    val text: String,
    val unique: Unique,
)
