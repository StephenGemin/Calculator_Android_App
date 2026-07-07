package com.example.calculator_v01

import android.util.Log
import com.udojava.evalex.Expression

private const val TAG = "CALC"

class CalculatorEngine {
    fun eval(txt: String): String {
        Log.i(TAG, "eval method -> String to evaluate: $txt")
        val expression = Expression(txt)
        expression.setPrecision(12)
        return expression.eval().toString()
    }
}
