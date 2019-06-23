package com.example.calculator_v01

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import net.objecthunter.exp4j.ExpressionBuilder
import com.udojava.evalex.Expression

import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Text
import java.lang.ArithmeticException
import java.math.BigDecimal

private const val TAG = "CALC"

class MainActivity : AppCompatActivity() {

    lateinit var calcOutput : TextView
    lateinit var calcHist : TextView
    var buildString = ArrayList<String>()
    var lastNumeric = false
    var lastEqual = false
    var lastPercent = false
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
            onlyDec = false
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

    fun onSign(view:View){
        if (calcOutput.text.isBlank()){
            // don't do anything
        }else if (calcOutput.text.isNotBlank()){
            calcOutput.text = (toDbl(calcOutput) * -1).toString()
        }

    }

    fun onPercent(view:View){
        if (calcOutput.text.isBlank() && buildString.isEmpty() || calcOutput.text.toString().toDoubleOrNull() == null){
            // don't do anything
        }else if (buildString.isEmpty() && calcOutput.text.isNotBlank()){
            calcOutput.text = (toDbl(calcOutput) / 100).toString()
            lastPercent = true
        }else if (buildString.isNotEmpty() && calcOutput.text.isNotBlank()){
            calcOutput.text = evalPercent(calcOutput.text.toString()).toString()
//            Log.i(TAG, "Percent function -> buildString: $buildString")
//            Log.i(TAG, "Percent function -> calcOutput: ${calcOutput.text}")
        }
    }

    private fun evalPercent(userInput:String):Double{
        return eval(buildTextOutput() + "0").toDouble() * (userInput.toDouble() / 100)
    }

    fun onEqual(view:View){
        var txt:String
        if (lastNumeric && !errorState && buildString.isNotEmpty()){
            txt = buildTextOutput()
            txt += calcOutput.text.toString()
            try{
                calcOutput.text = eval(txt)
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


    fun eval(txt:String):String{
        Log.i(TAG, "eval method -> String to evaluate: $txt")
        val expression = Expression(txt)
        expression.setPrecision(12)
        return expression.eval().toString()
    }

    private fun dblToInt(num:Double):String{
        if (num - num.toInt() == 0.0){
            return num.toInt().toString()
        }
        return num.toString()
    }

    private fun toDbl(input:TextView):Double{
        return input.text.toString().toDouble()
    }
}
