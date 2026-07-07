package com.example.calculator_v01

import com.udojava.evalex.Expression

data class CalculatorUiState(
    val output: String = "",
    val history: String = "",
    val isError: Boolean = false
)

enum class AngleUnit { DEGREES, RADIANS }

data class ScientificMode(
    val angleUnit: AngleUnit = AngleUnit.DEGREES,
    val secondActive: Boolean = false,
    val memory: Double? = null
)

class CalculatorEngine {
    private val buildString = ArrayList<String>()
    var state = CalculatorUiState()
        private set
    var mode = ScientificMode()
        private set
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

    fun onDigit(digitText: String) {
        val currentOutput = state.output
        if (errorState || lastEqual || lastPercent) {
            buildString.clear()
            state = state.copy(output = digitText, isError = false)
            errorState = false
            lastEqual = false
            lastPercent = false
            onlyDec = false
        } else if (!lastNumeric) {
            state = state.copy(output = digitText)
        } else {
            state = state.copy(output = currentOutput + digitText)
        }
        lastNumeric = true
    }

    fun onDecimal(decimalText: String) {
        if (errorState) return
        if (!lastNumeric) {
            state = state.copy(output = "0$decimalText")
            lastNumeric = true
            onlyDec = true
        } else if (!onlyDec) {
            state = state.copy(output = state.output + decimalText)
            onlyDec = true
        }
    }

    fun onOperator(operatorText: String) {
        val currentOutput = state.output
        if (lastNumeric && !errorState) {
            buildString.add(currentOutput)
            buildString.add(operatorText)
            state = state.copy(history = buildTextOutput(), output = operatorText)
            lastNumeric = false
            onlyDec = false
            lastEqual = false
            lastPercent = false
        }
    }

    fun onClear() {
        state = CalculatorUiState()
        lastNumeric = false
        errorState = false
        onlyDec = false
        lastPercent = false
        lastEqual = false
        buildString.clear()
    }

    fun onSign() {
        val currentOutput = state.output
        if (lastNumeric && !errorState) {
            val newValue = if (currentOutput.startsWith("-")) {
                currentOutput.removePrefix("-")
            } else {
                "-$currentOutput"
            }
            state = state.copy(output = newValue)
        }
    }

    fun onPercent() {
        val currentOutput = state.output
        if ((currentOutput.isBlank() && buildString.isEmpty()) || currentOutput.toDoubleOrNull() == null) {
            return
        } else if (buildString.isEmpty() && currentOutput.isNotBlank()) {
            val result = (currentOutput.toDoubleOrNull() ?: 0.0) / 100
            state = state.copy(output = result.toString())
            lastPercent = true
            onlyDec = true
        } else if (buildString.isNotEmpty() && currentOutput.isNotBlank()) {
            val result = evalPercent(currentOutput)
            state = state.copy(output = result.toString())
            onlyDec = true
        }
    }

    fun onSin() {
        val currentOutput = state.output
        if (!lastNumeric || errorState || currentOutput.toDoubleOrNull() == null) return
        // EvalEx's SIN expects its argument in degrees; DEG() converts radians to degrees.
        val arg = if (mode.angleUnit == AngleUnit.RADIANS) "DEG($currentOutput)" else currentOutput
        val result = eval("SIN($arg)")
        state = state.copy(output = result)
        onlyDec = true
    }

    fun onPi() {
        if (errorState || lastEqual || lastPercent) {
            buildString.clear()
            errorState = false
            lastEqual = false
            lastPercent = false
        }
        state = state.copy(output = eval("PI"))
        lastNumeric = true
        onlyDec = true
    }

    fun onMemoryAdd() {
        val currentOutput = state.output.toDoubleOrNull() ?: return
        mode = mode.copy(memory = (mode.memory ?: 0.0) + currentOutput)
    }

    fun onMemoryRecall() {
        val memoryValue = mode.memory ?: return
        state = state.copy(output = memoryValue.toString())
        lastNumeric = true
        onlyDec = true
    }

    fun onEqual(): Boolean {
        val currentOutput = state.output
        if (lastNumeric && !errorState && buildString.isNotEmpty()) {
            val txt = buildTextOutput() + currentOutput
            return try {
                val result = eval(txt)
                state = state.copy(output = result, isError = false)
                onlyDec = true
                lastEqual = true
                buildString.clear()
                true
            } catch (ex: Exception) {
                state = state.copy(isError = true)
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
