package com.example.calculator_v01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import net.objecthunter.exp4j.ExpressionBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ArithmeticException

class MainActivity : AppCompatActivity() {

    lateinit var calcOutput : TextView
    lateinit var calcHist : TextView
    var buildString = ArrayList<String>()
    var lastNumeric = false
    var lastEqual = false
    var errorState = false
    var onlyDec = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calcOutput = findViewById(R.id.calcOutput)
        calcHist = findViewById(R.id.calcHist)
    }

    //User clicks any button on the calc app
    fun onDigit(view:View){
        if (errorState || lastEqual){
            this.buildString = ArrayList()
            calcOutput.text = (view as Button).text
            errorState = false
            lastEqual = false
        }else{
            calcOutput.append((view as Button).text)
        }
        lastNumeric = true
    }

    fun onDecimal(view:View){
        if (lastNumeric && !errorState && !onlyDec){
            calcOutput.append((view as Button).text)
            lastNumeric = false
            onlyDec = true
        }
    }

    private fun buildTextOutput():String{
        var txtOut = ""
        for (item in buildString){
            txtOut += item
        }
        Toast.makeText(this, "expression: $txtOut", Toast.LENGTH_SHORT).show()
        return txtOut
    }

    fun onOperator(view:View){
        val txt:String?
        if (lastNumeric && !errorState){
            buildString.add(calcOutput.text.toString())
            buildString.add((view as Button).text.toString())
            txt = buildTextOutput()

            this.calcOutput.text = ""
            calcHist.text = txt
            lastNumeric = false
            onlyDec = false     //reset the decimal flag
        }
    }

    fun onClear(view:View){
        this.calcOutput.text = ""
        this.calcHist.text = ""
        lastNumeric = false
        errorState = false
        onlyDec = false
        this.buildString = ArrayList()
    }

    fun onEqual(view:View){
        var txt:String
        if (lastNumeric && !errorState){
            txt = buildTextOutput()
            txt += calcOutput.text.toString()
            Toast.makeText(this, "expression: $txt", Toast.LENGTH_SHORT).show()
            val expression = ExpressionBuilder(txt).build()
            try{
                val result = expression.evaluate()
                calcOutput.text = result.toString()
                onlyDec = true
            }catch (ex: ArithmeticException){
                calcOutput.text = getString(R.string.error)
                errorState = true
                lastNumeric = false

            }
        lastEqual = true
        this.buildString = ArrayList()
        }
    }
}
