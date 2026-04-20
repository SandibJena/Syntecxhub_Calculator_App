package com.noguts.calcy

import com.noguts.calcy.util.ExpressionEvaluator
import org.junit.Test

import org.junit.Assert.assertEquals

/**
 * Local evaluator tests for calculator behavior.
 */
class ExampleUnitTest {

    @Test
    fun evaluates_decimal_math() {
        assertEquals(7.5, ExpressionEvaluator.evaluate("3.5+4"), 1e-9)
    }

    @Test
    fun evaluates_parentheses_and_precedence() {
        assertEquals(14.0, ExpressionEvaluator.evaluate("2+(3*4)"), 1e-9)
    }

    @Test
    fun evaluates_scientific_operations() {
        assertEquals(1.0, ExpressionEvaluator.evaluate("sin(90)"), 1e-9)
        assertEquals(3.0, ExpressionEvaluator.evaluate("sqrt(9)"), 1e-9)
        assertEquals(8.0, ExpressionEvaluator.evaluate("2^3"), 1e-9)
    }
}