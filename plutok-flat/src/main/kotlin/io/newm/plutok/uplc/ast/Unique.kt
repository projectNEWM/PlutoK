package io.newm.plutok.uplc.ast

import kotlinx.serialization.Serializable

@Serializable
data class Unique(val value: Long) {
    fun increment() = Unique(value + 1)
}

fun Unique.toLong(): Long = value

fun Long.toUnique(): Unique = Unique(this)
