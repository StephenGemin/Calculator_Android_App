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
}
