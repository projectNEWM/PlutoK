package io.newm.plutok.uplc.ast

import io.newm.plutok.uplc.builtin.BuiltinFunction
import io.newm.plutok.uplc.flat.FlatEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = TermSerializer::class)
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

object TermSerializer : KSerializer<Term<*>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TermSerializer")

    private const val TERM_TAG_WIDTH = 4

    override fun deserialize(decoder: Decoder): Term<*> {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Term<*>) {
        require(encoder is FlatEncoder)
        when (value) {
            is ConstantTerm -> {
                encoder.safeEncodeBits(TERM_TAG_WIDTH, 4)
                encoder.encodeSerializableValue(Constant.serializer(), value.constant)
            }

            else -> throw IllegalArgumentException("Unknown term type: $value")
        }
    }
}
