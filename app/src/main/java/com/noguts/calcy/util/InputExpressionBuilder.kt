package com.noguts.calcy.util

object InputExpressionBuilder {

    fun appendDigit(expression: String, digit: String): String {
        return if (canInsertImplicitMultiplicationBeforeNumber(expression)) {
            "$expression*$digit"
        } else {
            expression + digit
        }
    }

    fun appendDecimalPoint(expression: String): String {
        if (expression.isEmpty()) {
            return "0."
        }

        if (canInsertImplicitMultiplicationBeforeNumber(expression)) {
            return "$expression*0."
        }

        val lastChar = expression.last()
        if (lastChar in "+-*/^(") {
            return "${expression}0."
        }

        val currentNumber = expression.takeLastWhile { it.isDigit() || it == '.' }
        return if (currentNumber.contains('.')) expression else "$expression."
    }

    private fun canInsertImplicitMultiplicationBeforeNumber(expression: String): Boolean {
        if (expression.isEmpty()) {
            return false
        }

        val lastChar = expression.last()
        return lastChar == ')' || lastChar == '%' || endsWithConstant(expression)
    }

    private fun endsWithConstant(expression: String): Boolean {
        return expression.endsWith("pi") || expression.endsWith("e")
    }
}