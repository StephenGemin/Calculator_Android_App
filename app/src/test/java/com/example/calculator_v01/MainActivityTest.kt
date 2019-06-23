package com.example.calculator_v01

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TestCalcOutput(val input: String, val expected: Double) {

    companion object{
        @JvmStatic
        @Parameters
        fun data(): Collection<Array<Any>>{
            val temp = 6*3.3
            return listOf(arrayOf("1+1", 2.0),
                arrayOf("2+2", 4.0),
                arrayOf("6*2", 12.0),
                arrayOf("6*3.3", temp),
                arrayOf("6/2", 3.0),
                arrayOf("18/7/3", 0.8571428571428572))
        }
    }

    @Test
    fun evaluate_calculator_output() {
        assertEquals(expected, MainActivity().eval(input), 0.0)

    }
}