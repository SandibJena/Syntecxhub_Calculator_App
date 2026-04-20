package com.noguts.calcy.util

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

object ExpressionEvaluator {

    fun evaluate(rawExpression: String): Double {
        val parser = Parser(rawExpression)
        val value = parser.parseExpression()
        parser.ensureFullyConsumed()
        return value
    }

    private class Parser(private val input: String) {
        private var index = 0

        private data class FactorValue(
            val value: Double,
            val isStandalonePercent: Boolean
        )

        private data class TermValue(
            val value: Double,
            val isContextualPercent: Boolean
        )

        fun parseExpression(): Double {
            var value = parseTerm().value
            while (true) {
                skipWhitespace()
                value = when {
                    match('+') -> {
                        val right = parseTerm()
                        val addend = if (right.isContextualPercent) {
                            value * right.value
                        } else {
                            right.value
                        }
                        value + addend
                    }

                    match('-') -> {
                        val right = parseTerm()
                        val subtrahend = if (right.isContextualPercent) {
                            value * right.value
                        } else {
                            right.value
                        }
                        value - subtrahend
                    }

                    else -> return value
                }
            }
        }

        fun ensureFullyConsumed() {
            skipWhitespace()
            if (index != input.length) {
                throw IllegalArgumentException("Unexpected token at position $index")
            }
        }

        private fun parseTerm(): TermValue {
            val firstFactor = parsePower()
            var value = firstFactor.value
            var isContextualPercent = firstFactor.isStandalonePercent

            while (true) {
                skipWhitespace()
                when {
                    match('*') -> {
                        val right = parsePower()
                        value *= right.value
                        isContextualPercent = false
                    }

                    match('/') -> {
                        val divisor = parsePower()
                        if (divisor.value == 0.0) {
                            throw ArithmeticException("Division by zero")
                        }
                        value /= divisor.value
                        isContextualPercent = false
                    }

                    else -> return TermValue(value, isContextualPercent)
                }
            }
        }

        private fun parsePower(): FactorValue {
            var value = parseUnary()
            skipWhitespace()
            if (match('^')) {
                val exponent = parsePower()
                value = FactorValue(
                    value.value.pow(exponent.value),
                    isStandalonePercent = false
                )
            }
            return value
        }

        private fun parseUnary(): FactorValue {
            skipWhitespace()
            return when {
                match('+') -> parseUnary()
                match('-') -> {
                    val unary = parseUnary()
                    FactorValue(-unary.value, unary.isStandalonePercent)
                }

                else -> parsePostfix()
            }
        }

        private fun parsePostfix(): FactorValue {
            var value = parsePrimary()
            var hasPercent = false

            while (true) {
                skipWhitespace()
                if (match('%')) {
                    value /= 100.0
                    hasPercent = true
                } else {
                    return FactorValue(value, hasPercent)
                }
            }
        }

        private fun parsePrimary(): Double {
            skipWhitespace()

            if (match('(')) {
                val nested = parseExpression()
                expect(')')
                return nested
            }

            val current = peek()
            if (current.isDigit() || current == '.') {
                return parseNumber()
            }

            if (current.isLetter()) {
                val identifier = parseIdentifier().lowercase()
                return when (identifier) {
                    "pi" -> Math.PI
                    "e" -> Math.E
                    "sin", "cos", "tan", "log", "ln", "sqrt" -> {
                        val argument = parseFunctionArgument()
                        applyFunction(identifier, argument)
                    }

                    else -> throw IllegalArgumentException("Unknown function or constant: $identifier")
                }
            }

            throw IllegalArgumentException("Unexpected token at position $index")
        }

        private fun parseFunctionArgument(): Double {
            skipWhitespace()
            return if (match('(')) {
                val value = parseExpression()
                expect(')')
                value
            } else {
                parseUnary().value
            }
        }

        private fun applyFunction(name: String, value: Double): Double {
            return when (name) {
                "sin" -> sin(Math.toRadians(value))
                "cos" -> cos(Math.toRadians(value))
                "tan" -> tan(Math.toRadians(value))
                "log" -> {
                    if (value <= 0.0) {
                        throw IllegalArgumentException("log argument must be > 0")
                    }
                    log10(value)
                }

                "ln" -> {
                    if (value <= 0.0) {
                        throw IllegalArgumentException("ln argument must be > 0")
                    }
                    ln(value)
                }

                "sqrt" -> {
                    if (value < 0.0) {
                        throw IllegalArgumentException("sqrt argument must be >= 0")
                    }
                    sqrt(value)
                }

                else -> throw IllegalArgumentException("Unsupported function: $name")
            }
        }

        private fun parseNumber(): Double {
            val start = index
            var hasDot = false

            while (index < input.length) {
                val c = input[index]
                if (c == '.') {
                    if (hasDot) {
                        break
                    }
                    hasDot = true
                    index++
                    continue
                }

                if (!c.isDigit()) {
                    break
                }
                index++
            }

            val numberText = input.substring(start, index)
            return numberText.toDoubleOrNull()
                ?: throw IllegalArgumentException("Invalid number: $numberText")
        }

        private fun parseIdentifier(): String {
            val start = index
            while (index < input.length && input[index].isLetter()) {
                index++
            }
            return input.substring(start, index)
        }

        private fun expect(expected: Char) {
            if (!match(expected)) {
                throw IllegalArgumentException("Expected '$expected' at position $index")
            }
        }

        private fun match(expected: Char): Boolean {
            if (peek() == expected) {
                index++
                return true
            }
            return false
        }

        private fun peek(): Char {
            return if (index < input.length) input[index] else '\u0000'
        }

        private fun skipWhitespace() {
            while (index < input.length && input[index].isWhitespace()) {
                index++
            }
        }
    }
}
