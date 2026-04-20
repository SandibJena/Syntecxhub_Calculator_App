package com.noguts.calcy

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.noguts.calcy.data.CalculatorDatabase
import com.noguts.calcy.data.HistoryRepository
import com.noguts.calcy.ui.HistoryAdapter
import com.noguts.calcy.util.ExpressionEvaluator
import com.noguts.calcy.util.InputExpressionBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyRepository: HistoryRepository
    private var historyDialog: AlertDialog? = null

    private var expression = ""
    private var justEvaluated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        setupHistory()
        bindDigitButtons()
        bindOperatorButtons()
        bindActionButtons()
        bindScientificButtonsIfPresent()

        updateDisplay()
    }

    private fun setupHistory() {
        val historyDao = CalculatorDatabase
            .getInstance(applicationContext)
            .calculationHistoryDao()
        historyRepository = HistoryRepository(historyDao)

        val rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        historyAdapter = HistoryAdapter { entry ->
            expression = entry.result
            justEvaluated = true
            updateDisplay()
        }

        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter
        rvHistory.setHasFixedSize(true)

        findViewById<Button>(R.id.btnClearHistory).setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                historyRepository.clearHistory()
            }
        }

        lifecycleScope.launch {
            historyRepository.observeHistory().collectLatest { history ->
                historyAdapter.submitList(history)
            }
        }
    }

    private fun bindDigitButtons() {
        val digitIds = listOf(
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
            R.id.btn6,
            R.id.btn7,
            R.id.btn8,
            R.id.btn9
        )

        for (id in digitIds) {
            findViewById<Button>(id).setOnClickListener {
                appendDigit((it as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener {
            appendDecimalPoint()
        }

        findViewById<Button>(R.id.btnOpenParen).setOnClickListener {
            appendOpenParenthesis()
        }

        findViewById<Button>(R.id.btnCloseParen).setOnClickListener {
            appendCloseParenthesis()
        }
    }

    private fun bindOperatorButtons() {
        val operatorIds = mapOf(
            R.id.btnAdd to "+",
            R.id.btnSub to "-",
            R.id.btnMul to "*",
            R.id.btnDiv to "/"
        )

        for ((id, operator) in operatorIds) {
            findViewById<Button>(id).setOnClickListener {
                appendOperator(operator)
            }
        }
    }

    private fun bindActionButtons() {
        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            evaluateAndShowResult()
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            clearAll()
        }

        findViewById<Button>(R.id.btnBackspace).setOnClickListener {
            deleteLast()
        }

        bindOptionalButton(R.id.btnHistory) {
            showHistoryDialog()
        }

        bindOptionalButton(R.id.btnLandscape) {
            switchToLandscapeMode()
        }

        bindOptionalButton(R.id.btnPortrait) {
            switchToPortraitMode()
        }
    }

    private fun bindScientificButtonsIfPresent() {
        bindOptionalButton(R.id.btnPow) {
            appendOperator("^")
        }
        bindOptionalButton(R.id.btnPercent) {
            appendPercent()
        }
        bindOptionalButton(R.id.btnToggleSign) {
            toggleExpressionSign()
        }
        bindOptionalButton(R.id.btnSin) {
            appendFunction("sin")
        }
        bindOptionalButton(R.id.btnCos) {
            appendFunction("cos")
        }
        bindOptionalButton(R.id.btnTan) {
            appendFunction("tan")
        }
        bindOptionalButton(R.id.btnLog) {
            appendFunction("log")
        }
        bindOptionalButton(R.id.btnLn) {
            appendFunction("ln")
        }
        bindOptionalButton(R.id.btnSqrt) {
            appendFunction("sqrt")
        }
        bindOptionalButton(R.id.btnPi) {
            appendConstant("pi")
        }
        bindOptionalButton(R.id.btnE) {
            appendConstant("e")
        }
    }

    private fun bindOptionalButton(buttonId: Int, onClick: () -> Unit) {
        findViewById<Button?>(buttonId)?.setOnClickListener { onClick() }
    }

    private fun showHistoryDialog() {
        if (historyDialog?.isShowing == true) {
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_history, null)
        val rvHistoryDialog = dialogView.findViewById<RecyclerView>(R.id.rvHistoryDialog)
        val btnClearHistoryDialog = dialogView.findViewById<Button>(R.id.btnClearHistoryDialog)
        val tvHistoryEmpty = dialogView.findViewById<TextView>(R.id.tvHistoryEmpty)

        var currentDialog: AlertDialog? = null
        val dialogAdapter = HistoryAdapter { entry ->
            expression = entry.result
            justEvaluated = true
            updateDisplay()
            currentDialog?.dismiss()
        }

        rvHistoryDialog.layoutManager = LinearLayoutManager(this)
        rvHistoryDialog.adapter = dialogAdapter
        rvHistoryDialog.setHasFixedSize(true)

        val historyJob: Job = lifecycleScope.launch {
            historyRepository.observeHistory().collectLatest { history ->
                dialogAdapter.submitList(history)
                tvHistoryEmpty.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        btnClearHistoryDialog.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                historyRepository.clearHistory()
            }
        }

        currentDialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create()

        historyDialog = currentDialog
        currentDialog.setOnDismissListener {
            historyJob.cancel()
            historyDialog = null
        }
        currentDialog.show()
    }

    private fun switchToLandscapeMode() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    private fun switchToPortraitMode() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    private fun appendDigit(digit: String) {
        if (justEvaluated) {
            expression = ""
            justEvaluated = false
        }

        expression = InputExpressionBuilder.appendDigit(expression, digit)
        updateDisplay()
    }

    private fun appendDecimalPoint() {
        if (justEvaluated) {
            expression = ""
            justEvaluated = false
        }

        val updatedExpression = InputExpressionBuilder.appendDecimalPoint(expression)
        if (updatedExpression != expression) {
            expression = updatedExpression
            updateDisplay()
        }
    }

    private fun appendOpenParenthesis() {
        if (justEvaluated) {
            expression = ""
            justEvaluated = false
        }

        if (canInsertImplicitMultiplication()) {
            expression += "*"
        }

        expression += "("
        updateDisplay()
    }

    private fun appendCloseParenthesis() {
        if (expression.isEmpty()) {
            return
        }

        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        if (openCount <= closeCount) {
            return
        }

        if (!canTerminateValue()) {
            return
        }

        expression += ")"
        updateDisplay()
    }

    private fun appendOperator(operator: String) {
        if (expression.isEmpty()) {
            if (operator == "-") {
                expression = "-"
                updateDisplay()
            }
            return
        }

        justEvaluated = false

        val lastChar = expression.last()
        expression = when {
            lastChar in "+-*/^" -> expression.dropLast(1) + operator
            lastChar == '.' -> expression + "0$operator"
            lastChar == '(' -> if (operator == "-") expression + operator else expression
            lastChar.isLetter() && !endsWithConstant() -> expression
            else -> expression + operator
        }

        updateDisplay()
    }

    private fun appendFunction(name: String) {
        if (justEvaluated) {
            expression = ""
            justEvaluated = false
        }

        if (canInsertImplicitMultiplication()) {
            expression += "*"
        }

        expression += "$name("
        updateDisplay()
    }

    private fun appendConstant(constant: String) {
        if (justEvaluated) {
            expression = ""
            justEvaluated = false
        }

        if (canInsertImplicitMultiplication()) {
            expression += "*"
        }

        expression += constant
        updateDisplay()
    }

    private fun appendPercent() {
        if (expression.isEmpty() || !canTerminateValue()) {
            return
        }

        if (expression.last() == '%') {
            return
        }

        expression += "%"
        justEvaluated = false
        updateDisplay()
    }

    private fun toggleExpressionSign() {
        expression = when {
            expression.isEmpty() -> "-"
            expression == "-" -> ""
            expression.startsWith("-(") && expression.endsWith(")") && expression.length > 3 -> {
                expression.substring(2, expression.length - 1)
            }

            expression.startsWith("-") -> expression.drop(1)
            else -> "-($expression)"
        }

        justEvaluated = false
        updateDisplay()
    }

    private fun deleteLast() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
        }
        justEvaluated = false
        updateDisplay()
    }

    private fun clearAll() {
        expression = ""
        justEvaluated = false
        updateDisplay()
    }

    private fun evaluateAndShowResult() {
        if (expression.isEmpty()) {
            return
        }

        val sanitized = sanitizeExpression(expression)
            ?: run {
                showError()
                return
            }

        try {
            val result = evaluateExpression(sanitized)
            if (!result.isFinite()) {
                throw ArithmeticException("Invalid result")
            }

            val resultText = formatResult(result)
            expression = resultText
            tvDisplay.text = resultText
            justEvaluated = true

            lifecycleScope.launch(Dispatchers.IO) {
                historyRepository.addHistory(sanitized, resultText)
            }
        } catch (_: Exception) {
            showError()
        }
    }

    private fun sanitizeExpression(rawExpression: String): String? {
        var clean = rawExpression.trim()

        while (clean.isNotEmpty() && clean.last() in "+-*/^(.") {
            clean = clean.dropLast(1)
        }

        if (clean.isEmpty()) {
            return null
        }

        val openParentheses = clean.count { it == '(' }
        val closeParentheses = clean.count { it == ')' }

        if (closeParentheses > openParentheses) {
            return null
        }

        if (openParentheses > closeParentheses) {
            clean += ")".repeat(openParentheses - closeParentheses)
        }

        return clean
    }

    private fun showError() {
        tvDisplay.text = "Error"
        expression = ""
        justEvaluated = false
    }

    private fun canInsertImplicitMultiplication(): Boolean {
        if (expression.isEmpty()) {
            return false
        }

        val lastChar = expression.last()
        return lastChar.isDigit() || lastChar == ')' || lastChar == '%' || endsWithConstant()
    }

    private fun canTerminateValue(): Boolean {
        if (expression.isEmpty()) {
            return false
        }

        val lastChar = expression.last()
        return lastChar.isDigit() || lastChar == ')' || lastChar == '%' || endsWithConstant()
    }

    private fun endsWithConstant(): Boolean {
        return expression.endsWith("pi") || expression.endsWith("e")
    }

    private fun formatResult(value: Double): String {
        val rounded = BigDecimal.valueOf(value)
            .setScale(10, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
        return if (rounded == "-0") "0" else rounded
    }

    private fun updateDisplay() {
        tvDisplay.text = if (expression.isEmpty()) "0" else expression
    }

    private fun evaluateExpression(input: String): Double {
        return ExpressionEvaluator.evaluate(input)
    }
}