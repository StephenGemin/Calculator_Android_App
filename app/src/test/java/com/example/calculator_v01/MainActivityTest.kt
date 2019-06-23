package com.example.calculator_v01
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import android.content.Context
import androidx.test.core.app.ApplicationProvider


internal class MainActivityTest {
    val cont = ApplicationProvider.getApplicationContext<Context>()
//    @org.junit.jupiter.api.Test
//    fun onDigit() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun onDecimal() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun onOperator() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun onClear() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun onSign() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun onPercent() {
//    }
//
//    @org.junit.jupiter.api.Test
//    fun onEqual() {
//    }

    @Test
    @DisplayName("Check output of the calculator based on the buttons clicked by the user")
    fun evalCalc() {
        val funObj = MainActivity()
        val result:String = MainActivity.eval("2+2")
        assertEquals("4", result)
    }
}


