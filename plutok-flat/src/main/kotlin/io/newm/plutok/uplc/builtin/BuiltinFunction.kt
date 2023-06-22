package io.newm.plutok.uplc.builtin

/**
 * All the possible builtin functions in Untyped Plutus Core.
 */
enum class BuiltinFunction(val index: Int) {
    // Integer functions
    AddInteger(0),
    SubtractInteger(1),
    MultiplyInteger(2),
    DivideInteger(3),
    QuotientInteger(4),
    RemainderInteger(5),
    ModInteger(6),
    EqualsInteger(7),
    LessThanInteger(8),
    LessThanEqualsInteger(9),

    // ByteString functions
    AppendByteString(10),
    ConsByteString(11),
    SliceByteString(12),
    LengthOfByteString(13),
    IndexByteString(14),
    EqualsByteString(15),
    LessThanByteString(16),
    LessThanEqualsByteString(17),

    // Cryptography and hash functions
    Sha2x256(18),
    Sha3x256(19),
    Blake2b256(20),
    VerifyEd25519Signature(21),
    VerifyEcdsaSecp256k1Signature(52),
    VerifySchnorrSecp256k1Signature(53),

    // String functions
    AppendString(22),
    EqualsString(23),
    EncodeUtf8(24),
    DecodeUtf8(25),

    // Bool function
    IfThenElse(26),

    // Unit function
    ChooseUnit(27),

    // Tracing function
    Trace(28),

    // Pairs functions
    FstPair(29),
    SndPair(30),

    // List functions
    ChooseList(31),
    MkCons(32),
    HeadList(33),
    TailList(34),
    NullList(35),

    // Data functions
    // It is convenient to have a "choosing" function for a data type that has more than two
    // constructors to get pattern matching over it and we may end up having multiple such data
    // types, hence we include the name of the data type as a suffix.
    ChooseData(36),
    ConstrData(37),
    MapData(38),
    ListData(39),
    IData(40),
    BData(41),
    UnConstrData(42),
    UnMapData(43),
    UnListData(44),
    UnIData(45),
    UnBData(46),
    EqualsData(47),
    SerialiseData(51),

    // Misc constructors
    // Constructors that we need for constructing e.g. Data. Polymorphic builtin
    // constructors are often problematic (See note [Representable built-in
    // functions over polymorphic built-in types])
    MkPairData(48),
    MkNilData(49),
    MkNilPairData(50),
}

fun String.toBuiltinFunction(): BuiltinFunction =
    BuiltinFunction.valueOf(this.replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it })

fun BuiltinFunction.toDisplayName(): String =
    this.name.replaceFirstChar { if (it.isUpperCase()) it.lowercaseChar() else it }
