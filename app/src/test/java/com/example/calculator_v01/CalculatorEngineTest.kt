package com.example.calculator_v01

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculatorEngineTest {

    private class FakeDisplayListener : CalculatorEngine.DisplayListener {
        var output: String = ""
        var history: String = ""
        var errorShown: Boolean = false
        override fun updateOutput(text: String) { output = text }
        override fun updateHistory(text: String) { history = text }
        override fun showError() { errorShown = true }
    }

    private lateinit var engine: CalculatorEngine
    private lateinit var fake: FakeDisplayListener

    @Before
    fun setUp() {
        engine = CalculatorEngine()
        fake = FakeDisplayListener()
        engine.setDisplayListener(fake)
    }

    private fun digit(d: String) = engine.onDigit(d, fake.output)
    private fun dec() = engine.onDecimal(".", fake.output)
    private fun op(o: String) = engine.onOperator(o, fake.output)
    private fun sign() = engine.onSign(fake.output)
    private fun percent() = engine.onPercent(fake.output)
    private fun equal(): Boolean = engine.onEqual(fake.output)
    private fun clear() = engine.onClear()

    // --- Regression tests for previously fixed bugs ---

    @Test
    fun `operator right after equals is not silently cleared`() {
        digit("9"); op("*"); digit("9"); equal()
        assertEquals("81", fake.output)

        op("*")
        assertEquals("*", fake.output)
        assertFalse(engine.lastNumeric)

        digit("9"); equal()
        assertEquals("729", fake.output)
    }

    @Test
    fun `digits after a decimal point append instead of resetting`() {
        digit("1"); dec(); digit("1")
        assertEquals("1.1", fake.output)
    }

    // --- Baseline healthy-path coverage ---

    @Test
    fun `basic digit entry`() {
        digit("1")
        assertEquals("1", fake.output)
        digit("2")
        assertEquals("12", fake.output)
    }

    @Test
    fun `simple addition sequence`() {
        digit("1"); op("+"); digit("1"); equal()
        assertEquals("2", fake.output)
    }

    @Test
    fun `chained operators respect standard operator precedence`() {
        digit("1"); op("+"); digit("2"); op("*"); digit("3"); equal()
        assertEquals("7", fake.output)
    }

    @Test
    fun `clear resets output history and state`() {
        digit("1"); op("+"); digit("1")
        clear()
        assertEquals("", fake.output)
        assertEquals("", fake.history)
        assertFalse(engine.lastNumeric)
        assertFalse(engine.onlyDec)
        assertFalse(engine.lastPercent)
        assertFalse(engine.errorState)
    }

    @Test
    fun `percent on empty input is a no-op`() {
        percent()
        assertEquals("", fake.output)
    }

    @Test
    fun `percent mid-expression scales against the pending operand`() {
        digit("2"); digit("0"); digit("0"); op("+"); digit("1"); digit("0"); percent()
        assertEquals("20.0", fake.output)
    }

    @Test
    fun `decimal then digit builds the same number as digit then decimal`() {
        digit("1"); dec(); digit("5")
        assertEquals("1.5", fake.output)

        clear()

        digit("2"); digit("5"); dec()
        assertEquals("25.", fake.output)
    }

    @Test
    fun `sign toggled twice returns to the original value`() {
        digit("5"); sign(); sign()
        assertEquals("5.0", fake.output)
    }

    @Test
    fun `decimal point works on a new number after equals`() {
        digit("1"); op("+"); digit("1"); equal()
        digit("2"); dec(); digit("5")
        assertEquals("2.5", fake.output)
    }

    @Test
    fun `decimal point is rejected after a percent conversion`() {
        digit("5"); percent()
        assertEquals("0.05", fake.output)

        dec()
        assertEquals("0.05", fake.output)
    }

    @Test
    fun `decimal point is rejected after sign toggle`() {
        digit("5"); sign()
        assertEquals("-5.0", fake.output)

        dec()
        assertEquals("-5.0", fake.output)
    }

    @Test
    fun `sign toggle is a no-op right after an operator`() {
        digit("1"); op("+")
        sign()
        assertEquals("+", fake.output)
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
        digit("1"); engine.onOperator("+*", fake.output); digit("1")

        equal()
        assertTrue(engine.errorState)
    }

    @Test
    fun `digit after percent starts a new number`() {
        digit("5"); percent()
        assertTrue(engine.lastPercent)

        digit("3")
        assertEquals("3", fake.output)
    }

    @Test
    fun `clear resets lastEqual`() {
        digit("1"); op("+"); digit("1"); equal()
        clear()
        assertFalse(engine.lastEqual)
    }
}
