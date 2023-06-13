package io.newm.plutok.flat.zigzag

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigInteger
import java.util.stream.Stream

class ZigZagTest {

    @ParameterizedTest
    @MethodSource("testData")
    fun `test ZigZag`(arguments: Pair<String, String>) {
        println(arguments)
        val (input, expected) = arguments
        val value = BigInteger(input)
        val encoded = value.zigZagEncode()
        val decoded = encoded.zigZagDecode()
        assertThat(encoded).isEqualTo(BigInteger(expected))
        assertThat(decoded).isEqualTo(value)
    }

    companion object {
        @JvmStatic
        fun testData(): Stream<Arguments> = Stream.of(
            Arguments.of("123456" to "246912"),
            Arguments.of("12345678901234567890" to "24691357802469135780"),
            Arguments.of("123456789012345678901234567890" to "246913578024691357802469135780"),
            Arguments.of("-123456789012345678901234567890" to "246913578024691357802469135779"),
            Arguments.of("-20" to "39"),
            Arguments.of("-19" to "37"),
            Arguments.of("-18" to "35"),
            Arguments.of("-17" to "33"),
            Arguments.of("-16" to "31"),
            Arguments.of("-15" to "29"),
            Arguments.of("-14" to "27"),
            Arguments.of("-13" to "25"),
            Arguments.of("-12" to "23"),
            Arguments.of("-11" to "21"),
            Arguments.of("-10" to "19"),
            Arguments.of("-9" to "17"),
            Arguments.of("-8" to "15"),
            Arguments.of("-7" to "13"),
            Arguments.of("-6" to "11"),
            Arguments.of("-5" to "9"),
            Arguments.of("-4" to "7"),
            Arguments.of("-3" to "5"),
            Arguments.of("-2" to "3"),
            Arguments.of("-1" to "1"),
            Arguments.of("0" to "0"),
            Arguments.of("1" to "2"),
            Arguments.of("2" to "4"),
            Arguments.of("3" to "6"),
            Arguments.of("4" to "8"),
            Arguments.of("5" to "10"),
            Arguments.of("6" to "12"),
            Arguments.of("7" to "14"),
            Arguments.of("8" to "16"),
            Arguments.of("9" to "18"),
            Arguments.of("10" to "20"),
            Arguments.of("11" to "22"),
            Arguments.of("12" to "24"),
            Arguments.of("13" to "26"),
            Arguments.of("14" to "28"),
            Arguments.of("15" to "30"),
            Arguments.of("16" to "32"),
            Arguments.of("17" to "34"),
            Arguments.of("18" to "36"),
            Arguments.of("19" to "38"),
            Arguments.of("20" to "40"),
        )
    }
}
