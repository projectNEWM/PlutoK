package io.newm.plutok.uplc.ast

sealed interface Type

object BoolType : Type

object IntegerType : Type

object StringType : Type

object ByteStringType : Type

object UnitType : Type

data class ListType(val subType: Type) : Type

data class PairType(val type1: Type, val type2: Type) : Type

object DataType : Type
