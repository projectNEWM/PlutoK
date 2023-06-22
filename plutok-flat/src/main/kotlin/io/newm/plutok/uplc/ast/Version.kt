package io.newm.plutok.uplc.ast

import io.newm.plutok.uplc.flat.FlatEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = VersionSerializer::class)
data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
)

object VersionSerializer : KSerializer<Version> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Version")

    override fun deserialize(decoder: Decoder): Version {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Version) {
        require(encoder is FlatEncoder)
        // Version is encoded without zigzag encoding
        encoder.encodeWord(value.major.toBigInteger())
        encoder.encodeWord(value.minor.toBigInteger())
        encoder.encodeWord(value.patch.toBigInteger())
    }
}
