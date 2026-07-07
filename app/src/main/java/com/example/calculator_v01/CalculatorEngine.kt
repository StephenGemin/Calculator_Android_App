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
        val currentOutput = normalizeOperand(state.output)
        if (lastNumeric && !errorState) {
            buildString.add(currentOutput + pendingCloseParens(currentOutput))
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
            try {
                val result = evalPercent(currentOutput)
                state = state.copy(output = result.toString())
                onlyDec = true
            } catch (ex: Exception) {
                state = state.copy(isError = true)
                errorState = true
                lastNumeric = false
            }
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
        val currentOutput = normalizeOperand(state.output)
        if (lastNumeric && !errorState && buildString.isNotEmpty()) {
            val txt = buildTextOutput() + currentOutput + pendingCloseParens(currentOutput)
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

    // Strips a dangling EE marker (see onExponentEntry) so "2E" is treated as "2"
    // instead of leaking an incomplete literal into eval().
    private fun normalizeOperand(output: String): String = output.removeSuffix("E")

    // nth-root (see onNthRoot in MainActivity) pushes an unclosed "^(1/" fragment onto
    // buildString; this closes it once the operand that completes it is finalized, whether
    // by another operator or by "=". Other operators never contain "(", so this is a no-op
    // for every existing operation.
    private fun pendingCloseParens(operand: String): String {
        val txt = buildTextOutput() + operand
        val opens = txt.count { it == '(' } - txt.count { it == ')' }
        return if (opens > 0) ")".repeat(opens) else ""
    }

    private fun unaryOp(build: (String) -> String) {
        val currentOutput = state.output
        if (!lastNumeric || errorState || currentOutput.toDoubleOrNull() == null) return
        try {
            state = state.copy(output = eval(build(currentOutput)))
            onlyDec = true
        } catch (ex: Exception) {
            state = state.copy(isError = true)
            errorState = true
            lastNumeric = false
        }
    }

    fun onSquared() = unaryOp { "$it^2" }
    fun onCubed() = unaryOp { "$it^3" }
    fun onInverse() = unaryOp { "1/$it" }
    fun onSquareRoot() = unaryOp { "SQRT($it)" }
    fun onCubeRoot() = unaryOp { "$it^(1/3)" }
    fun onLn() = unaryOp { "LOG($it)" }
    fun onLogTen() = unaryOp { "LOG10($it)" }
    fun onExpBaseX() = unaryOp { "e^$it" }
    fun onTenToX() = unaryOp { "10^$it" }
    fun onSinh() = unaryOp { "SINH($it)" }
    fun onCosh() = unaryOp { "COSH($it)" }
    fun onTanh() = unaryOp { "TANH($it)" }

    fun onCos() = unaryOp {
        val arg = if (mode.angleUnit == AngleUnit.RADIANS) "DEG($it)" else it
        "COS($arg)"
    }

    fun onTan() = unaryOp {
        val arg = if (mode.angleUnit == AngleUnit.RADIANS) "DEG($it)" else it
        "TAN($arg)"
    }

    // ASIN/ACOS/ATAN return degrees; convert to radians when that's the active unit.
    fun onAsin() = unaryOp {
        val result = "ASIN($it)"
        if (mode.angleUnit == AngleUnit.RADIANS) "RAD($result)" else result
    }

    fun onAcos() = unaryOp {
        val result = "ACOS($it)"
        if (mode.angleUnit == AngleUnit.RADIANS) "RAD($result)" else result
    }

    fun onAtan() = unaryOp {
        val result = "ATAN($it)"
        if (mode.angleUnit == AngleUnit.RADIANS) "RAD($result)" else result
    }

    fun onFactorial() {
        val currentOutput = state.output
        if (!lastNumeric || errorState) return
        val value = currentOutput.toDoubleOrNull() ?: return
        if (value < 0 || value != Math.floor(value)) {
            state = state.copy(isError = true)
            errorState = true
            lastNumeric = false
            return
        }
        unaryOp { "FACT($it)" }
    }

    fun onExp() {
        if (errorState || lastEqual || lastPercent) {
            buildString.clear()
            errorState = false
            lastEqual = false
            lastPercent = false
        }
        state = state.copy(output = eval("e"))
        lastNumeric = true
        onlyDec = true
    }

    fun onRandom() {
        if (errorState || lastEqual || lastPercent) {
            buildString.clear()
            errorState = false
            lastEqual = false
            lastPercent = false
        }
        state = state.copy(output = eval("RANDOM()"))
        lastNumeric = true
        onlyDec = true
    }

    // EE: appends a bare exponent marker; digits typed afterward extend the exponent
    // (e.g. "2" -> "2E" -> "2E3"). A dangling "E" with no exponent digits is stripped by
    // normalizeOperand() before it can reach eval() or combine with an operator.
    fun onExponentEntry() {
        if (errorState || !lastNumeric || state.output.contains("E")) return
        state = state.copy(output = state.output + "E")
    }

    fun onMemorySubtract() {
        val currentOutput = state.output.toDoubleOrNull() ?: return
        mode = mode.copy(memory = (mode.memory ?: 0.0) - currentOutput)
    }

    fun onMemoryClear() {
        mode = mode.copy(memory = null)
    }

    fun onSecondToggle() {
        mode = mode.copy(secondActive = !mode.secondActive)
    }

    fun onAngleUnitToggle() {
        mode = mode.copy(
            angleUnit = if (mode.angleUnit == AngleUnit.DEGREES) AngleUnit.RADIANS else AngleUnit.DEGREES
        )
    }

    fun eval(txt: String): String {
        val expression = Expression(txt)
        expression.setPrecision(12)
        return expression.eval().toPlainString()
    }
}
