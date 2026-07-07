package com.example.calculator_v01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calculator_v01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val engine = CalculatorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtonListeners()
        render()
    }

    private fun setupButtonListeners() {
        binding.btnAC.setOnClickListener { engine.onClear(); render() }
        binding.btnSign.setOnClickListener { engine.onSign(); render() }
        binding.btnPer.setOnClickListener { engine.onPercent(); render() }

        binding.btn0.setOnClickListener { engine.onDigit("0"); render() }
        binding.btn1.setOnClickListener { engine.onDigit("1"); render() }
        binding.btn2.setOnClickListener { engine.onDigit("2"); render() }
        binding.btn3.setOnClickListener { engine.onDigit("3"); render() }
        binding.btn4.setOnClickListener { engine.onDigit("4"); render() }
        binding.btn5.setOnClickListener { engine.onDigit("5"); render() }
        binding.btn6.setOnClickListener { engine.onDigit("6"); render() }
        binding.btn7.setOnClickListener { engine.onDigit("7"); render() }
        binding.btn8.setOnClickListener { engine.onDigit("8"); render() }
        binding.btn9.setOnClickListener { engine.onDigit("9"); render() }

        binding.btnDec.setOnClickListener { engine.onDecimal("."); render() }

        binding.btnDiv.setOnClickListener { engine.onOperator("/"); render() }
        binding.btnMul.setOnClickListener { engine.onOperator("*"); render() }
        binding.btnSub.setOnClickListener { engine.onOperator("-"); render() }
        binding.btnAdd.setOnClickListener { engine.onOperator("+"); render() }

        binding.btnEqual.setOnClickListener { engine.onEqual(); render() }
    }

    private fun render() {
        val state = engine.state
        binding.calcOutput.text = if (state.isError) getString(R.string.error) else state.output
        binding.calcHist.text = state.history
    }
}
