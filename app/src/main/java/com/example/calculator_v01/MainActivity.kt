package com.example.calculator_v01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import net.objecthunter.exp4j.ExpressionBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    lateinit var calcOutput : TextView
    var lastNumeric = false
    var errorState = false
    var onlyDec = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calcOutput = findViewById(R.id.calcOutput)
    }

    //User clicks any button on the calc app
    fun onDigit(view:View){
        if (errorState){
            calcOutput.text = (view as Button).text
            errorState = false
        }else{
            calcOutput.append((view as Button).text)
        }
        lastNumeric = true
    }

    fun onDecimal(view:View){
        if (lastNumeric && !errorState && !onlyDec){
            calcOutput.append(".")
            lastNumeric = false
            onlyDec = true
        }
    }

    fun onOperator(view:View){
        if (lastNumeric && !errorState){
            calcOutput.append((view as Button).text)
            lastNumeric = false
            onlyDec = false     //reset the decimal flag
        }
    }

    fun onClear(view:View){
        this.calcOutput.text = ""
        lastNumeric = false
        errorState = false
        onlyDec = false
    }

    fun onEqual(view:View){
        if (lastNumeric && !errorState){
            val txt = calcOutput.text.toString()
            val expression = ExpressionBuilder(txt).build()
            try{
                val result = expression.evaluate()
                calcOutput.text = result.toString()
                onlyDec = true
            }catch (ex: ArithmeticException){
                calcOutput.text = "Error"
                errorState = true
                lastNumeric = false
            }

        }
    }
}
