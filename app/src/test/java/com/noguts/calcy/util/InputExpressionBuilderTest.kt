package com.noguts.calcy.util

import org.junit.Assert.assertEquals
import org.junit.Test

class InputExpressionBuilderTest {

    @Test
    fun appends_multi_digit_numbers_without_implicit_multiplication() {
        assertEquals("90", InputExpressionBuilder.appendDigit("9", "0"))
    }

    @Test
    fun inserts_implicit_multiplication_after_parenthesis_or_constant() {
        assertEquals("(2+3)*4", InputExpressionBuilder.appendDigit("(2+3)", "4"))
        assertEquals("pi*2", InputExpressionBuilder.appendDigit("pi", "2"))
    }

    @Test
    fun appends_decimal_point_only_when_allowed() {
        assertEquals("9.", InputExpressionBuilder.appendDecimalPoint("9"))
        assertEquals("9.", InputExpressionBuilder.appendDecimalPoint("9."))
        assertEquals("7+0.", InputExpressionBuilder.appendDecimalPoint("7+"))
    }
}