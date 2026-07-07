package com.example.calculator_v01

import java.lang.ArithmeticException
import com.udojava.evalex.Expression

class CalculatorEngine {
    private val buildString = ArrayList<String>()
    var lastNumeric = false
        private set
    var lastEqual = false
        private set
    var lastPercent = false
        private set
    var errorState = false
        private set
    var onlyDec = false
        private set

    interface DisplayListener {
        fun updateOutput(text: String)
        fun updateHistory(text: String)
        fun showError()
    }

    private var displayListener: DisplayListener? = null

    fun setDisplayListener(listener: DisplayListener) {
        this.displayListener = listener
    }

    fun onDigit(digitText: String, currentOutput: String) {
        if (errorState || lastEqual) {
            buildString.clear()
            displayListener?.updateOutput(digitText)
            errorState = false
            lastEqual = false
        } else if (!lastNumeric) {
            displayListener?.updateOutput(digitText)
        } else {
            displayListener?.updateOutput(currentOutput + digitText)
        }
        lastNumeric = true
    }

    fun onDecimal(decimalText: String, currentOutput: String) {
        if (lastNumeric && !errorState && !onlyDec) {
            displayListener?.updateOutput(currentOutput + decimalText)
            onlyDec = true
        }
    }

    fun onOperator(operatorText: String, currentOutput: String) {
        if (lastNumeric && !errorState) {
            buildString.add(currentOutput)
            buildString.add(operatorText)
            displayListener?.updateHistory(buildTextOutput())
            displayListener?.updateOutput(operatorText)
            lastNumeric = false
            onlyDec = false
            lastEqual = false
        }
    }

    fun onClear() {
        displayListener?.updateOutput("")
        displayListener?.updateHistory("")
        lastNumeric = false
        errorState = false
        onlyDec = false
        lastPercent = false
        buildString.clear()
    }

    fun onSign(currentOutput: String) {
        if (currentOutput.isNotBlank()) {
            val newValue = (currentOutput.toDoubleOrNull() ?: 0.0) * -1
            displayListener?.updateOutput(newValue.toString())
        }
    }

    fun onPercent(currentOutput: String) {
        if ((currentOutput.isBlank() && buildString.isEmpty()) || currentOutput.toDoubleOrNull() == null) {
            return
        } else if (buildString.isEmpty() && currentOutput.isNotBlank()) {
            val result = (currentOutput.toDoubleOrNull() ?: 0.0) / 100
            displayListener?.updateOutput(result.toString())
            lastPercent = true
        } else if (buildString.isNotEmpty() && currentOutput.isNotBlank()) {
            val result = evalPercent(currentOutput)
            displayListener?.updateOutput(result.toString())
        }
    }

    fun onEqual(currentOutput: String): Boolean {
        if (lastNumeric && !errorState && buildString.isNotEmpty()) {
            val txt = buildTextOutput() + currentOutput
            return try {
                val result = eval(txt)
                displayListener?.updateOutput(result)
                onlyDec = true
                lastEqual = true
                buildString.clear()
                true
            } catch (ex: ArithmeticException) {
                displayListener?.showError()
                errorState = true
                lastNumeric = false
                false
            }
        }
        return false
    }

    private fun buildTextOutput(): String {
        var txtOut = ""
        for (item in buildString) {
            txtOut += item
        }
        return txtOut
    }

    private fun evalPercent(userInput: String): Double {
        return eval(buildTextOutput() + "0").toDouble() * (userInput.toDoubleOrNull() ?: 0.0) / 100
    }

    fun eval(txt: String): String {
        val expression = Expression(txt)
        expression.setPrecision(12)
        return expression.eval().toString()
    }
}
