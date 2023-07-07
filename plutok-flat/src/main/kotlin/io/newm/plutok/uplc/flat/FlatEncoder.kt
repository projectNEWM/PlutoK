package io.newm.plutok.uplc.flat

import io.newm.plutok.uplc.ast.ApplyConstant
import io.newm.plutok.uplc.ast.BoolConstant
import io.newm.plutok.uplc.ast.BoolType
import io.newm.plutok.uplc.ast.ByteStringConstant
import io.newm.plutok.uplc.ast.ByteStringType
import io.newm.plutok.uplc.ast.Constant
import io.newm.plutok.uplc.ast.DataConstant
import io.newm.plutok.uplc.ast.DataType
import io.newm.plutok.uplc.ast.IntegerConstant
import io.newm.plutok.uplc.ast.IntegerType
import io.newm.plutok.uplc.ast.ListType
import io.newm.plutok.uplc.ast.PairType
import io.newm.plutok.uplc.ast.ProtoListConstant
import io.newm.plutok.uplc.ast.ProtoPairConstant
import io.newm.plutok.uplc.ast.StringConstant
import io.newm.plutok.uplc.ast.StringType
import io.newm.plutok.uplc.ast.Type
import io.newm.plutok.uplc.ast.UnitConstant
import io.newm.plutok.uplc.ast.UnitType
import io.newm.plutok.uplc.flat.zigzag.zigZagEncode
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import java.math.BigInteger
import java.nio.ByteBuffer

class FlatEncoder : AbstractEncoder() {
    override val serializersModule: SerializersModule = EmptySerializersModule()

    private val buffer = ByteBuffer.allocate(1024 * 1000) // 1MB
    private var usedBits = 0
    private var currentByte = 0.toUByte()

    override fun encodeValue(value: Any) {
        when (value) {
            is Int -> encodeBigInteger(value.toBigInteger())
            is Long -> encodeBigInteger(value.toBigInteger())
            is BigInteger -> encodeBigInteger(value)
            is UByte -> encodeUByte(value)
            is Boolean -> encodeBoolean(value)
            else -> super.encodeValue(value)
        }
    }

    /**
     * Encode 1 unsigned byte.
     * Uses the next 8 bits in the buffer, can be byte aligned or byte unaligned
     */
    fun encodeUByte(value: UByte) {
        if (usedBits == 0) {
            currentByte = value
            nextWord()
        } else {
            encodeUByteUnaligned(value)
        }
    }

    /**
     * Encode an arbitrary length integer using zigzag encoding.
     */
    fun encodeBigInteger(value: BigInteger) {
        val i = value.zigZagEncode()
        encodeWord(i)
    }

    /**
     * Encode a boolean value. This is byte alignment agnostic.
     * Uses the next unused bit in the current byte to encode this information.
     * One for true and Zero for false
     */
    override fun encodeBoolean(value: Boolean) {
        if (value) {
            encodeOne()
        } else {
            encodeZero()
        }
    }

    /**
     * Write out byte regardless of current buffer alignment.
     * Write most significant bits in remaining unused bits for the current byte,
     * then write out the remaining bits at the beginning of the next byte.
     */
    private fun encodeUByteUnaligned(value: UByte) {
        val xShift = (currentByte.toUInt() or (value.toUInt() shr usedBits)).toUByte()
        buffer.put(xShift.toByte())

        currentByte = (value.toUInt() shl (8 - usedBits)).toUByte()
    }

    fun <T> encodeListWith(list: List<T>, encoderFunc: (T) -> Unit) {
        for (item in list) {
            encodeOne()
            encoderFunc(item)
        }
        encodeZero()
    }

    fun encodeTypeToList(type: Type, typeEncode: MutableList<Int>) {
        when (type) {
            BoolType -> typeEncode.add(4)
            IntegerType -> typeEncode.add(0)
            StringType -> typeEncode.add(2)
            ByteStringType -> typeEncode.add(1)
            UnitType -> typeEncode.add(3)
            is ListType -> {
                typeEncode.add(7)
                typeEncode.add(5)
                encodeTypeToList(type.subType, typeEncode)
            }

            is PairType -> {
                typeEncode.add(7)
                typeEncode.add(7)
                typeEncode.add(6)
                encodeTypeToList(type.type1, typeEncode)
                encodeTypeToList(type.type2, typeEncode)
            }

            DataType -> typeEncode.add(8)
        }
    }

    fun encodeConstantValue(constant: Constant) {
        when (constant) {
            is ApplyConstant -> TODO()
            is BoolConstant -> TODO()
            is ByteStringConstant -> TODO()
            is DataConstant -> TODO()
            is IntegerConstant -> encodeBigInteger(constant.value)
            is ProtoListConstant -> encodeListWith(constant.value) { encodeConstantValue(it as Constant) }
            is ProtoPairConstant -> TODO()
            is StringConstant -> TODO()
            UnitConstant -> {}
        }
        // fn encode_constant_value(x: &Constant, e: &mut Encoder) -> Result<(), en::Error> {
        //    match x {
        //        Constant::Integer(x) => {
        //            let x: i128 = x.try_into().unwrap();
        //
        //            x.encode(e)
        //        }
        //        Constant::ByteString(b) => b.encode(e),
        //        Constant::String(s) => s.encode(e),
        //        Constant::Unit => Ok(()),
        //        Constant::Bool(b) => b.encode(e),
        //        Constant::ProtoList(_, list) => {
        //            e.encode_list_with(list, encode_constant_value)?;
        //            Ok(())
        //        }
        //        Constant::ProtoPair(_, _, a, b) => {
        //            encode_constant_value(a, e)?;
        //
        //            encode_constant_value(b, e)
        //        }
        //        Constant::Data(data) => {
        //            let cbor = data
        //                .encode_fragment()
        //                .map_err(|err| en::Error::Message(err.to_string()))?;
        //
        //            cbor.encode(e)
        //        }
        //    }
        // }
    }

    /**
     * Encode a unsigned integer of any size.
     * This is byte alignment agnostic.
     * We encode the 7 least significant bits of the unsigned byte. If the char value is greater than
     * 127 we encode a leading 1 followed by repeating the above for the next 7 bits and so on.
     */
    fun encodeWord(value: BigInteger) {
        var d = value
        do {
            var w = (d and 127.toBigInteger()).toInt()
            d = d shr 7

            if (d != BigInteger.ZERO) {
                w = w or 128
            }
            encodeBits(8, w)
        } while (d != BigInteger.ZERO)
    }

    /**
     * Encode a byte in numBits bits with overflow protection.
     */
    fun safeEncodeBits(numBits: Int, byte: Int) {
        if ((1 shl numBits) - 1 < byte) {
            throw IllegalArgumentException("Overflow detected, cannot fit $byte in $numBits bits.")
        } else {
            encodeBits(numBits, byte)
        }
    }

    /**
     * Encode up to 8 bits of information and is byte alignment agnostic.
     * Uses unused bits in the current byte to write out the passed in byte value.
     * Overflows to the most significant digits of the next byte if number of bits to use is greater than unused bits.
     * Expects that number of bits to use is greater than or equal to required bits by the value.
     * The param num_bits is i64 to match unused_bits type.
     */
    private fun encodeBits(numBits: Int, value: Int) {
        when (numBits) {
            1 -> when (value) {
                0 -> encodeZero()
                1 -> encodeOne()
                else -> throw IllegalArgumentException("Invalid value for 1 bit: $value")
            }

            2 -> when (value) {
                0 -> {
                    encodeZero()
                    encodeZero()
                }

                1 -> {
                    encodeZero()
                    encodeOne()
                }

                2 -> {
                    encodeOne()
                    encodeZero()
                }

                3 -> {
                    encodeOne()
                    encodeOne()
                }

                else -> throw IllegalArgumentException("Invalid value for 2 bits: $value")
            }

            else -> {
                usedBits += numBits
                val unusedBits = 8 - usedBits
                when {
                    unusedBits > 0 -> {
                        currentByte = (currentByte.toUInt() or (value.toUInt() shl unusedBits)).toUByte()
                    }

                    unusedBits == 0 -> {
                        currentByte = (currentByte.toUInt() or value.toUInt()).toUByte()
                        nextWord()
                    }

                    else -> {
                        val used = -unusedBits
                        currentByte = (currentByte.toUInt() or (value.toUInt() shr used)).toUByte()
                        nextWord()
                        currentByte = (value.toUInt() shl (8 - used)).toUByte()
                        usedBits = used
                    }
                }
            }
        }
    }

    /**
     * A filler amount of end 0's followed by a 1 at the end of a byte.
     * Used to byte align the buffer by padding out the rest of the byte.
     */
    internal fun encodeFiller() {
        currentByte = currentByte or 1u.toUByte()
        nextWord()
    }

    /**
     * Write a 0 bit into the current byte.
     * Write out to buffer if last used bit in the current byte.
     */
    private fun encodeZero() {
        if (usedBits == 7) {
            nextWord()
        } else {
            usedBits += 1
        }
    }

    /**
     * Write a 1 bit into the current byte.
     * Write out to buffer if last used bit in the current byte.
     */
    private fun encodeOne() {
        if (usedBits == 7) {
            currentByte = (currentByte.toUInt() or 1u).toUByte()
            nextWord()
        } else {
            currentByte = (currentByte.toUInt() or (128u shr usedBits)).toUByte()
            usedBits += 1
        }
    }

    /**
     * Write the current byte out to the buffer and begin next byte to write out.
     * Add current byte to the buffer and set current byte and used bits to 0.
     */
    private fun nextWord() {
        buffer.put(currentByte.toByte())

        currentByte = 0.toUByte()
        usedBits = 0
    }

    fun toByteArray(): ByteArray {
        buffer.flip()
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes
    }
}

fun <T> encodeToFlat(serializer: SerializationStrategy<T>, value: T): ByteArray {
    val encoder = FlatEncoder()
    encoder.encodeSerializableValue(serializer, value)
    encoder.encodeFiller()
    return encoder.toByteArray()
}

inline fun <reified T> encodeToFlat(value: T) = encodeToFlat(serializer(), value)
