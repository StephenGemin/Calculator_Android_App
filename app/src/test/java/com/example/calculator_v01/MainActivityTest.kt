package com.example.calculator_v01

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TestCalcOutput(val input: String, val expected: String) {

    companion object{
        @JvmStatic
        @Parameters
        fun data(): Collection<Array<String>>{
            return listOf(arrayOf("1+1", "2"),
                arrayOf("2+2", "4"),
                arrayOf("6*2", "12"),
                arrayOf("6*3.3", "19.8"),
                arrayOf("6/2", "3"),
                arrayOf("18/7/3", "0.857142857143"))
        }
    }

    @Test
    fun evaluate_calculator_output() {
        assertEquals(expected, MainActivity().eval(input))

    }
}