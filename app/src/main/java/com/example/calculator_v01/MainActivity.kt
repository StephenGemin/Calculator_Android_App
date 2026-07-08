package com.example.calculator_v01

import androidx.appcompat.app.AppCompatActivity
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.calculator_v01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val engine = CalculatorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyStatusBarVisibility()
        setupButtonListeners()
        render()
    }

    private fun applyStatusBarVisibility() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            controller.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            controller.show(WindowInsetsCompat.Type.statusBars())
        }
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

        binding.btnSin?.setOnClickListener {
            if (engine.mode.secondActive) engine.onAsin() else engine.onSin()
            render()
        }
        binding.btnCos?.setOnClickListener {
            if (engine.mode.secondActive) engine.onAcos() else engine.onCos()
            render()
        }
        binding.btnTan?.setOnClickListener {
            if (engine.mode.secondActive) engine.onAtan() else engine.onTan()
            render()
        }
        binding.btnSinh?.setOnClickListener { engine.onSinh(); render() }
        binding.btnCosh?.setOnClickListener { engine.onCosh(); render() }
        binding.btnTanh?.setOnClickListener { engine.onTanh(); render() }

        binding.btnPower?.setOnClickListener { engine.onOperator("^"); render() }
        binding.btnNthRoot?.setOnClickListener { engine.onOperator("^(1/"); render() }

        binding.btnSquared?.setOnClickListener { engine.onSquared(); render() }
        binding.btnCubed?.setOnClickListener { engine.onCubed(); render() }
        binding.btnInverse?.setOnClickListener { engine.onInverse(); render() }
        binding.btnSquareRoot?.setOnClickListener { engine.onSquareRoot(); render() }
        binding.btnCubeRoot?.setOnClickListener { engine.onCubeRoot(); render() }
        binding.btnLn?.setOnClickListener { engine.onLn(); render() }
        binding.btnLogTen?.setOnClickListener { engine.onLogTen(); render() }
        binding.btnFactorial?.setOnClickListener { engine.onFactorial(); render() }
        binding.btnExponent?.setOnClickListener { engine.onExpBaseX(); render() }
        binding.btnTenToX?.setOnClickListener { engine.onTenToX(); render() }
        binding.btnExpTen?.setOnClickListener { engine.onExponentEntry(); render() }

        binding.btnPi?.setOnClickListener { engine.onPi(); render() }
        binding.btnExp?.setOnClickListener { engine.onExp(); render() }
        binding.btnRandom?.setOnClickListener { engine.onRandom(); render() }

        binding.btnMemAdd?.setOnClickListener { engine.onMemoryAdd(); render() }
        binding.btnMemRecall?.setOnClickListener { engine.onMemoryRecall(); render() }
        binding.btnMemDel?.setOnClickListener { engine.onMemorySubtract(); render() }
        binding.btnMC?.setOnClickListener { engine.onMemoryClear(); render() }

        binding.btnSecond?.setOnClickListener { engine.onSecondToggle(); render() }
        binding.btnRadians?.setOnClickListener { engine.onAngleUnitToggle(); render() }
    }

    private fun render() {
        val state = engine.state
        binding.calcOutput.text = if (state.isError) getString(R.string.error) else state.output
        binding.calcHist.text = state.history

        val mode = engine.mode
        binding.btnSin?.text = getString(if (mode.secondActive) R.string.btnAsin else R.string.btnSin)
        binding.btnCos?.text = getString(if (mode.secondActive) R.string.btnAcos else R.string.btnCos)
        binding.btnTan?.text = getString(if (mode.secondActive) R.string.btnAtan else R.string.btnTan)
        binding.btnRadians?.text =
            getString(if (mode.angleUnit == AngleUnit.DEGREES) R.string.btnRadians else R.string.btnDegrees)
    }
}
