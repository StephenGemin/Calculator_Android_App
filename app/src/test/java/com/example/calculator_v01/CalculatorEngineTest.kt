package com.example.calculator_v01

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setUp() {
        engine = CalculatorEngine()
    }

    private val output get() = engine.state.output
    private val history get() = engine.state.history

    private fun digit(d: String) = engine.onDigit(d)
    private fun dec() = engine.onDecimal(".")
    private fun op(o: String) = engine.onOperator(o)
    private fun sign() = engine.onSign()
    private fun percent() = engine.onPercent()
    private fun equal(): Boolean = engine.onEqual()
    private fun clear() = engine.onClear()

    // --- Regression tests for previously fixed bugs ---

    @Test
    fun `operator right after equals is not silently cleared`() {
        digit("9"); op("*"); digit("9"); equal()
        assertEquals("81", output)

        op("*")
        assertEquals("*", output)
        assertFalse(engine.lastNumeric)

        digit("9"); equal()
        assertEquals("729", output)
    }

    @Test
    fun `digits after a decimal point append instead of resetting`() {
        digit("1"); dec(); digit("1")
        assertEquals("1.1", output)
    }

    @Test
    fun `operator after a leading percent is not silently cleared`() {
        digit("1"); percent(); op("+"); digit("1"); equal()
        assertEquals("1.01", output)
    }

    // --- Baseline healthy-path coverage ---

    @Test
    fun `basic digit entry`() {
        digit("1")
        assertEquals("1", output)
        digit("2")
        assertEquals("12", output)
    }

    @Test
    fun `simple addition sequence`() {
        digit("1"); op("+"); digit("1"); equal()
        assertEquals("2", output)
    }

    @Test
    fun `chained operators respect standard operator precedence`() {
        digit("1"); op("+"); digit("2"); op("*"); digit("3"); equal()
        assertEquals("7", output)
    }

    @Test
    fun `clear resets output history and state`() {
        digit("1"); op("+"); digit("1")
        clear()
        assertEquals("", output)
        assertEquals("", history)
        assertFalse(engine.lastNumeric)
        assertFalse(engine.onlyDec)
        assertFalse(engine.lastPercent)
        assertFalse(engine.errorState)
    }

    @Test
    fun `percent on empty input is a no-op`() {
        percent()
        assertEquals("", output)
    }

    @Test
    fun `percent mid-expression scales against the pending operand`() {
        digit("2"); digit("0"); digit("0"); op("+"); digit("1"); digit("0"); percent()
        assertEquals("20.0", output)
    }

    @Test
    fun `decimal then digit builds the same number as digit then decimal`() {
        digit("1"); dec(); digit("5")
        assertEquals("1.5", output)

        clear()

        digit("2"); digit("5"); dec()
        assertEquals("25.", output)
    }

    @Test
    fun `sign toggled twice returns to the original value`() {
        digit("5"); sign(); sign()
        assertEquals("5", output)
    }

    @Test
    fun `sign toggle preserves integer formatting instead of forcing a decimal`() {
        digit("1"); sign()
        assertEquals("-1", output)
    }

    @Test
    fun `decimal point works on a new number after equals`() {
        digit("1"); op("+"); digit("1"); equal()
        digit("2"); dec(); digit("5")
        assertEquals("2.5", output)
    }

    @Test
    fun `decimal point is rejected after a percent conversion`() {
        digit("5"); percent()
        assertEquals("0.05", output)

        dec()
        assertEquals("0.05", output)
    }

    @Test
    fun `decimal point is allowed after sign toggle when no decimal is present yet`() {
        digit("5"); sign()
        assertEquals("-5", output)

        dec()
        assertEquals("-5.", output)
    }

    @Test
    fun `decimal point is still rejected after sign toggle if a decimal is already present`() {
        digit("5"); dec(); digit("0"); sign()
        assertEquals("-5.0", output)

        dec()
        assertEquals("-5.0", output)
    }

    @Test
    fun `sign toggle is a no-op right after an operator`() {
        digit("1"); op("+")
        sign()
        assertEquals("+", output)
    }

    @Test
    fun `sign toggle is a no-op during an error state`() {
        digit("1"); op("/"); digit("0"); equal()
        assertTrue(engine.errorState)

        sign()
        assertTrue(engine.errorState)
    }

    @Test
    fun `malformed expression shows an error instead of crashing`() {
        digit("1"); engine.onOperator("+*"); digit("1")

        equal()
        assertTrue(engine.errorState)
    }

    @Test
    fun `digit after percent starts a new number`() {
        digit("5"); percent()
        assertTrue(engine.lastPercent)

        digit("3")
        assertEquals("3", output)
    }

    @Test
    fun `decimal point right after an operator starts a new number at 0`() {
        digit("1"); op("+"); dec()
        assertEquals("0.", output)

        digit("5"); equal()
        assertEquals("1.5", output)
    }

    @Test
    fun `clear resets lastEqual`() {
        digit("1"); op("+"); digit("1"); equal()
        clear()
        assertFalse(engine.lastEqual)
    }

    // --- Scientific mode groundwork ---

    @Test
    fun `sin computes in degrees by default`() {
        digit("3"); digit("0"); engine.onSin()
        assertEquals("0.5", output)
    }

    @Test
    fun `sin is a no-op on empty input`() {
        engine.onSin()
        assertEquals("", output)
    }

    @Test
    fun `power uses caret as the operator`() {
        digit("2"); engine.onOperator("^"); digit("3"); equal()
        assertEquals("8", output)
    }

    @Test
    fun `pi inserts the constant as a fresh operand`() {
        engine.onPi()
        assertEquals("3.14159265359", output)
        assertTrue(engine.lastNumeric)
    }

    @Test
    fun `pi after equals starts a new expression instead of appending`() {
        digit("1"); op("+"); digit("1"); equal()
        engine.onPi()
        assertEquals("3.14159265359", output)
    }

    @Test
    fun `memory add then recall round-trips the value`() {
        digit("5"); engine.onMemoryAdd()
        clear()
        engine.onMemoryRecall()
        assertEquals("5.0", output)
    }

    @Test
    fun `memory add accumulates across multiple calls`() {
        digit("5"); engine.onMemoryAdd()
        clear()
        digit("3"); engine.onMemoryAdd()
        clear()
        engine.onMemoryRecall()
        assertEquals("8.0", output)
    }

    @Test
    fun `memory recall on empty memory is a no-op`() {
        engine.onMemoryRecall()
        assertEquals("", output)
    }

    // --- Remaining scientific functions ---

    @Test
    fun `squared and cubed compute powers`() {
        digit("3"); engine.onSquared()
        assertEquals("9", output)

        clear()
        digit("3"); engine.onCubed()
        assertEquals("27", output)
    }

    @Test
    fun `inverse computes reciprocal`() {
        digit("4"); engine.onInverse()
        assertEquals("0.25", output)
    }

    @Test
    fun `square root and cube root`() {
        digit("9"); engine.onSquareRoot()
        assertEquals("3", output)

        clear()
        digit("8"); engine.onCubeRoot()
        assertEquals("2", output)
    }

    @Test
    fun `square root of a negative number is an error, not a crash`() {
        digit("9"); sign(); engine.onSquareRoot()
        assertTrue(engine.errorState)
    }

    @Test
    fun `ln and log10`() {
        digit("1"); engine.onLn()
        assertEquals("0", output)

        clear()
        digit("1"); digit("0"); digit("0"); engine.onLogTen()
        assertEquals("2", output)
    }

    @Test
    fun `factorial of a small integer`() {
        digit("5"); engine.onFactorial()
        assertEquals("120", output)
    }

    @Test
    fun `factorial of a negative or non-integer is an error`() {
        digit("5"); sign(); engine.onFactorial()
        assertTrue(engine.errorState)

        clear()
        digit("2"); dec(); digit("5"); engine.onFactorial()
        assertTrue(engine.errorState)
    }

    @Test
    fun `cos and tan compute in degrees by default`() {
        digit("6"); digit("0"); engine.onCos()
        assertEquals("0.5", output)

        clear()
        digit("4"); digit("5"); engine.onTan()
        assertEquals("1", output)
    }

    @Test
    fun `e to the x and ten to the x`() {
        digit("0"); engine.onExpBaseX()
        assertEquals("1", output)

        clear()
        digit("2"); engine.onTenToX()
        assertEquals("100", output)
    }

    @Test
    fun `hyperbolic functions are not angle-unit sensitive`() {
        digit("0"); engine.onSinh()
        assertEquals("0", output)
    }

    @Test
    fun `nth root computes x to the power of 1 over n`() {
        digit("8"); engine.onOperator("^(1/"); digit("3"); equal()
        assertEquals("2", output)
    }

    @Test
    fun `percent pressed while nth root's index is still pending is an error, not a crash`() {
        digit("8"); engine.onOperator("^(1/"); digit("3"); percent()
        assertTrue(engine.errorState)
    }

    @Test
    fun `nth root chained with another operator does not swallow the operator`() {
        digit("8"); engine.onOperator("^(1/"); digit("3"); op("+"); digit("1"); equal()
        assertEquals("3", output)
    }

    @Test
    fun `e constant inserts euler's number as a fresh operand`() {
        engine.onExp()
        assertEquals("2.71828182846", output)
        assertTrue(engine.lastNumeric)
    }

    @Test
    fun `second toggle flips secondActive`() {
        assertFalse(engine.mode.secondActive)
        engine.onSecondToggle()
        assertTrue(engine.mode.secondActive)
    }

    @Test
    fun `asin acos atan return degrees by default and radians when toggled`() {
        digit("0"); digit("."); digit("5"); engine.onAsin()
        assertEquals("30", output)

        clear()
        engine.onAngleUnitToggle()
        digit("0"); digit("."); digit("5"); engine.onAsin()
        assertEquals("0.523598775598", output)
    }

    @Test
    fun `angle unit toggle flips between degrees and radians`() {
        assertEquals(AngleUnit.DEGREES, engine.mode.angleUnit)
        engine.onAngleUnitToggle()
        assertEquals(AngleUnit.RADIANS, engine.mode.angleUnit)
        engine.onAngleUnitToggle()
        assertEquals(AngleUnit.DEGREES, engine.mode.angleUnit)
    }

    @Test
    fun `memory subtract and clear`() {
        digit("5"); engine.onMemoryAdd()
        clear()
        digit("2"); engine.onMemorySubtract()
        clear()
        engine.onMemoryRecall()
        assertEquals("3.0", output)

        engine.onMemoryClear()
        assertNull(engine.mode.memory)
    }

    @Test
    fun `exponent entry appends E and digits extend it`() {
        digit("2"); engine.onExponentEntry(); digit("3")
        assertEquals("2E3", output)

        op("+"); digit("0"); equal()
        assertEquals("2000", output)
    }

    @Test
    fun `dangling exponent entry is dropped instead of corrupting the next operator`() {
        digit("2"); engine.onExponentEntry(); op("+"); digit("3"); equal()
        assertEquals("5", output)
    }
}
