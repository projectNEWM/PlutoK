package io.newm.plutok.uplc.flat

import com.google.common.truth.Truth.assertThat
import io.newm.plutok.uplc.ast.ConstantTerm
import io.newm.plutok.uplc.ast.IntegerConstant
import io.newm.plutok.uplc.ast.IntegerType
import io.newm.plutok.uplc.ast.ListType
import io.newm.plutok.uplc.ast.Name
import io.newm.plutok.uplc.ast.Program
import io.newm.plutok.uplc.ast.ProtoListConstant
import io.newm.plutok.uplc.ast.Version
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FlatTest {

    @Test
    fun `test flat-encode Integer`() {
        val program = Program<Name>(
            version = Version(11, 22, 33),
            term = ConstantTerm<Name>(IntegerConstant(11.toBigInteger()))
        )

        val expectedBytes = byteArrayOf(
            0b00001011.toByte(),
            0b00010110.toByte(),
            0b00100001.toByte(),
            0b01001000.toByte(),
            0b00000101.toByte(),
            0b10000001.toByte()
        )

        val actualBytes: ByteArray = encodeToFlat(program)

        assertThat(actualBytes).isEqualTo(expectedBytes)
    }

    @Test
    fun `test flat-encode List List Integer`() {
        val program = Program<Name>(
            version = Version(1, 0, 0),
            term = ConstantTerm<Name>(
                ProtoListConstant(
                    type = ListType(IntegerType),
                    value = listOf(
                        ProtoListConstant(
                            type = IntegerType,
                            value = listOf(IntegerConstant(7.toBigInteger()))
                        ),
                        ProtoListConstant(
                            type = IntegerType,
                            value = listOf(IntegerConstant(5.toBigInteger()))
                        )
                    )
                )
            )
        )

        val expectedBytes = byteArrayOf(
            0b00000001.toByte(),
            0b00000000.toByte(),
            0b00000000.toByte(),
            0b01001011.toByte(),
            0b11010110.toByte(),
            0b11110101.toByte(),
            0b10000011.toByte(),
            0b00001110.toByte(),
            0b01100001.toByte(),
            0b01000001.toByte()
        )

        val actualBytes: ByteArray = encodeToFlat(program)

        assertThat(actualBytes).isEqualTo(expectedBytes)
    }

    @Test
    fun `test safeEncodeBits success`() {
        val expectedBytes = byteArrayOf(
            0b11000001.toByte(),
        )
        val numBits = 2
        val byte = 0b00000011
        val encoder = FlatEncoder()
        encoder.safeEncodeBits(numBits, byte)
        encoder.encodeFiller()
        val actualBytes = encoder.toByteArray()

        assertThat(actualBytes).isEqualTo(expectedBytes)
    }

    @Test
    fun `test safeEncodeBits overflow`() {
        val encoder = FlatEncoder()
        var numBits = 2
        var byte = 0b00000100
        var exception = assertThrows<IllegalArgumentException> {
            encoder.safeEncodeBits(numBits, byte)
        }
        assertThat(exception.message).isEqualTo("Overflow detected, cannot fit 4 in 2 bits.")

        numBits = 3
        byte = 0b00001001
        exception = assertThrows<IllegalArgumentException> {
            encoder.safeEncodeBits(numBits, byte)
        }
        assertThat(exception.message).isEqualTo("Overflow detected, cannot fit 9 in 3 bits.")
    }
}
