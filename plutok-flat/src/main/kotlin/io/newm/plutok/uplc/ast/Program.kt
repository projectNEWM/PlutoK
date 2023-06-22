package io.newm.plutok.uplc.ast

import io.newm.plutok.uplc.flat.FlatEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * This represents a program in Untyped Plutus Core (uplc).
 * It contains a version and a term that requires a generic type.
 */
@Serializable(with = ProgramSerializer::class)
data class Program<T>(
    val version: Version,
    val term: Term<T>,
)

object ProgramSerializer : KSerializer<Program<*>> {
    override val descriptor = buildClassSerialDescriptor("Program")

    override fun deserialize(decoder: Decoder): Program<*> {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Program<*>) {
        require(encoder is FlatEncoder)
        encoder.encodeSerializableValue(Version.serializer(), value.version)
        encoder.encodeSerializableValue(TermSerializer, value.term)
    }
}
