package com.example.calculator_v01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calculator_v01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), CalculatorEngine.DisplayListener {

    private lateinit var binding: ActivityMainBinding
    private val engine = CalculatorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        engine.setDisplayListener(this)
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.btnAC.setOnClickListener { engine.onClear() }
        binding.btnSign.setOnClickListener { engine.onSign(binding.calcOutput.text.toString()) }
        binding.btnPer.setOnClickListener { engine.onPercent(binding.calcOutput.text.toString()) }

        binding.btn0.setOnClickListener { engine.onDigit("0", binding.calcOutput.text.toString()) }
        binding.btn1.setOnClickListener { engine.onDigit("1", binding.calcOutput.text.toString()) }
        binding.btn2.setOnClickListener { engine.onDigit("2", binding.calcOutput.text.toString()) }
        binding.btn3.setOnClickListener { engine.onDigit("3", binding.calcOutput.text.toString()) }
        binding.btn4.setOnClickListener { engine.onDigit("4", binding.calcOutput.text.toString()) }
        binding.btn5.setOnClickListener { engine.onDigit("5", binding.calcOutput.text.toString()) }
        binding.btn6.setOnClickListener { engine.onDigit("6", binding.calcOutput.text.toString()) }
        binding.btn7.setOnClickListener { engine.onDigit("7", binding.calcOutput.text.toString()) }
        binding.btn8.setOnClickListener { engine.onDigit("8", binding.calcOutput.text.toString()) }
        binding.btn9.setOnClickListener { engine.onDigit("9", binding.calcOutput.text.toString()) }

        binding.btnDec.setOnClickListener { engine.onDecimal(".", binding.calcOutput.text.toString()) }

        binding.btnDiv.setOnClickListener { engine.onOperator("/", binding.calcOutput.text.toString()) }
        binding.btnMul.setOnClickListener { engine.onOperator("*", binding.calcOutput.text.toString()) }
        binding.btnSub.setOnClickListener { engine.onOperator("-", binding.calcOutput.text.toString()) }
        binding.btnAdd.setOnClickListener { engine.onOperator("+", binding.calcOutput.text.toString()) }

        binding.btnEqual.setOnClickListener { engine.onEqual(binding.calcOutput.text.toString()) }
    }

    override fun updateOutput(text: String) {
        binding.calcOutput.text = text
    }

    override fun updateHistory(text: String) {
        binding.calcHist.text = text
    }

    override fun showError() {
        binding.calcOutput.text = getString(R.string.error)
    }
}
