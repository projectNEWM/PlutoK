package io.newm.plutok.uplc.ast

import io.newm.plutok.uplc.builtin.BuiltinFunction

sealed interface Term<T>

/**
 * tag: 0
 */
data class VarTerm<T>(
    val variable: T,
) : Term<T>

/**
 * tag: 1
 */
data class DelayTerm<T>(
    val term: Term<T>,
) : Term<T>

/**
 * tag: 2
 */
data class LambdaTerm<T>(
    val parameterName: T,
    val body: Term<T>,
) : Term<T>

/**
 * tag: 3
 */
data class ApplyTerm<T>(
    val function: Term<T>,
    val argument: Term<T>,
) : Term<T>

/**
 * tag: 4
 */
data class ConstantTerm<T>(
    val constant: Constant,
) : Term<T>

/**
 * tag: 5
 */
data class ForceTerm<T>(
    val term: Term<T>,
) : Term<T>

/**
 * tag: 6
 */
class ErrorTerm<T>() : Term<T>

data class BuiltinTerm<T>(
    val function: BuiltinFunction,
) : Term<T>
