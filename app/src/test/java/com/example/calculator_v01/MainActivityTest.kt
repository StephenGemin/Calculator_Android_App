package com.example.calculator_v01

import org.junit.Test
import org.junit.Assert.*
import org.junit.runners.Parameterized

class MainActivityTest {

    @Test
    fun onEqual() {
    }


//    @Parameterized.Parameters
    @Test
    fun evaluate_calculator_output() {
        val result: String = MainActivity().eval("1+1")
        assertEquals("2.0", result)

    }
}