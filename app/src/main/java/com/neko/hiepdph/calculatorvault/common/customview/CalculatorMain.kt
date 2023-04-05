package com.neko.hiepdph.calculatorvault.common.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.ARC_COS
import com.neko.hiepdph.calculatorvault.common.utils.ARC_SIN
import com.neko.hiepdph.calculatorvault.common.utils.ARC_TAN
import com.neko.hiepdph.calculatorvault.databinding.CalculatorViewBinding

interface CalculatorFunction {
    fun onPressButton0()
    fun onPressButton1()
    fun onPressButton2()
    fun onPressButton3()
    fun onPressButton4()
    fun onPressButton5()
    fun onPressButton6()
    fun onPressButton7()
    fun onPressButton8()
    fun onPressButton9()
    fun onPressButtonComma()
    fun onPressButtonPlus()
    fun onPressButtonSubtract()
    fun onPressButtonTimes()
    fun onPressButtonDivide()
    fun onPressButtonEqual()
    fun onPressButtonE()
    fun onPressButtonSwitch()
    fun onPressButtonReset()
    fun onPressButtonBackspace()
    fun onPressButtonPercent()
    fun onPressButtonPi()
    fun onPressButtonInverse()
    fun onPressButtonExponent()
    fun onPressButtonSqrt()
    fun onPressButtonPow()
    fun onPressButtonLg()
    fun onPressButtonLn()
    fun onPressButtonOpenBracket()
    fun onPressButtonCloseBracket()
    fun onPressButton2nd()
    fun onPressButtonDeg()
    fun onPressButtonRad()
    fun onPressButtonSin()
    fun onPressButtonCos()
    fun onPressButtonTan()
    fun onPressButtonArcSin()
    fun onPressButtonArcCos()
    fun onPressButtonArcTan()

}


class CalculatorMain @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: CalculatorViewBinding
    private var callback: CalculatorFunction? = null
    private var expandState = false

    init {
        binding = CalculatorViewBinding.inflate(LayoutInflater.from(context), this, false)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalculatorMain)
        initView()

        addView(binding.root)
        typedArray.recycle()
    }

    companion object {
        var isChangeFunctionEnabled = false //2nd
        var isFlexDegEnabled = false //deg
    }

    private fun changeButtonColorTheme() {
        val sharedPreferences = AppSharePreference.INSTANCE
        binding.btnReset.changeTextColor(sharedPreferences.getThemeColor(context.getColor(R.color.primary)))
        binding.btnDelete.setTint(sharedPreferences.getThemeColor(context.getColor(R.color.primary)))
        binding.btnPercent.changeTextColor(sharedPreferences.getThemeColor(context.getColor(R.color.primary)))
        binding.btnDivide.setTint(sharedPreferences.getThemeColor(context.getColor(R.color.primary)))
        binding.btnTimes.changeTextColor(sharedPreferences.getThemeColor(context.getColor(R.color.primary)))
        binding.btnSubtract.changeTextColor(sharedPreferences.getThemeColor(context.getColor(R.color.primary)))
        binding.btnPlus.changeTextColor(sharedPreferences.getThemeColor(context.getColor(R.color.primary)))
//        binding.btnEqual.changeTextColor(sharedPreferences.getThemeColor(context.getColor(R.color.primary)))
    }


    private fun initView() {
        changeButtonColorTheme()
        binding.btnSwitch.setOnClickListener {
            expandState = !expandState
            if (expandState) {
                switchToExpandLayout()
            } else {
                switchToNormalLayout()
            }
        }

        binding.btn1.setOnClickListener {
            callback?.onPressButton1()
        }
        binding.btn2.setOnClickListener {
            callback?.onPressButton2()
        }
        binding.btn3.setOnClickListener {
            callback?.onPressButton3()
        }
        binding.btn4.setOnClickListener {
            callback?.onPressButton4()
        }
        binding.btn5.setOnClickListener {
            callback?.onPressButton5()
        }
        binding.btn6.setOnClickListener {
            callback?.onPressButton6()
        }
        binding.btn7.setOnClickListener {
            callback?.onPressButton7()
        }
        binding.btn8.setOnClickListener {
            callback?.onPressButton8()
        }
        binding.btn9.setOnClickListener {
            callback?.onPressButton9()
        }
        binding.btn0.setOnClickListener {
            callback?.onPressButton0()
        }
        binding.btnEqual.setOnClickListener {
            callback?.onPressButtonEqual()
        }
        binding.btnE.setOnClickListener {
            callback?.onPressButtonE()
        }
        binding.btnComma.setOnClickListener {
            callback?.onPressButtonComma()
        }
        binding.btnReset.setOnClickListener {
            callback?.onPressButtonReset()
        }
        binding.btnDelete.setOnClickListener {
            callback?.onPressButtonBackspace()
        }
        binding.btnPlus.setOnClickListener {
            callback?.onPressButtonPlus()
        }
        binding.btnSubtract.setOnClickListener {
            callback?.onPressButtonSubtract()
        }
        binding.btnTimes.setOnClickListener {
            callback?.onPressButtonTimes()
        }
        binding.btnDivide.setOnClickListener {
            callback?.onPressButtonDivide()
        }
        binding.btnPi.setOnClickListener {
            callback?.onPressButtonPi()
        }
        binding.btnInverse.setOnClickListener {
            callback?.onPressButtonInverse()
        }
        binding.btnExponentiation.setOnClickListener {
            callback?.onPressButtonExponent()
        }
        binding.btnSqrt.setOnClickListener {
            callback?.onPressButtonSqrt()
        }
        binding.btnPow.setOnClickListener {
            callback?.onPressButtonPow()
        }
        binding.btnLn.setOnClickListener {
            callback?.onPressButtonLn()
        }
        binding.btnLg.setOnClickListener {
            callback?.onPressButtonLg()
        }
        binding.btnOpenBracket.setOnClickListener {
            callback?.onPressButtonOpenBracket()
        }
        binding.btnCloseBracket.setOnClickListener {
            callback?.onPressButtonCloseBracket()
        }
        binding.btn2nd.setOnClickListener {
            isChangeFunctionEnabled = !isChangeFunctionEnabled
            changeFunction()
            callback?.onPressButton2nd()
        }
        binding.btnFlexDeg.setOnClickListener {
            isFlexDegEnabled = !isFlexDegEnabled
            if (!isFlexDegEnabled) {
                callback?.onPressButtonDeg()
            } else {
                callback?.onPressButtonRad()
            }
        }
        binding.btnFlexTan.setOnClickListener {
            if (!isChangeFunctionEnabled) {
                callback?.onPressButtonTan()
            } else {
                callback?.onPressButtonArcTan()
            }
        }
        binding.btnFlexSin.setOnClickListener {
            if (!isChangeFunctionEnabled) {
                callback?.onPressButtonSin()
            } else {
                callback?.onPressButtonArcSin()
            }
        }
        binding.btnFlexCos.setOnClickListener {
            if (!isChangeFunctionEnabled) {
                callback?.onPressButtonCos()
            } else {
                callback?.onPressButtonArcCos()
            }
        }
        binding.btnPercent.setOnClickListener {
            callback?.onPressButtonPercent()
        }

    }

    fun setCalculatorMainCallback(callback: CalculatorFunction) {
        this.callback = callback
    }

    private fun switchToNormalLayout() {
        binding.apply {
            btnE.hide()
            btn2nd.hide()
            btnFlexDeg.hide()
            btnFlexSin.hide()
            btnFlexCos.hide()
            btnFlexTan.hide()
            btnPow.hide()
            btnLg.hide()
            btnLn.hide()
            btnOpenBracket.hide()
            btnCloseBracket.hide()
            btnSqrt.hide()
            btnExponentiation.hide()
            btnInverse.hide()
            btnPi.hide()
            btnSwitch.setBackgroundResource(R.drawable.bg_item_calculator_normal)
        }

    }

    private fun changeFunction() {
        if (isChangeFunctionEnabled) {
            binding.btnFlexCos.apply {
                changeText(ARC_COS)
            }
            binding.btnFlexSin.apply {
                changeText(ARC_SIN)

            }
            binding.btnFlexTan.apply {
                changeText(ARC_TAN)

            }
        } else {
            binding.btnFlexCos.apply {
                changeText("cos")

            }
            binding.btnFlexSin.apply {
                changeText("sin")


            }
            binding.btnFlexTan.apply {
                changeText("tan")

            }
        }
    }

    private fun changeFlexDeg() {
        if (isFlexDegEnabled) {
            binding.btnFlexDeg.changeText("rad")
        } else {
            binding.btnFlexDeg.changeText("deg")
        }
    }

    private fun switchToExpandLayout() {
        binding.apply {
            btnE.show()
            btn2nd.show()
            btnFlexDeg.show()
            btnFlexSin.show()
            btnFlexCos.show()
            btnFlexTan.show()
            btnPow.show()
            btnLg.show()
            btnLn.show()
            btnOpenBracket.show()
            btnCloseBracket.show()
            btnSqrt.show()
            btnExponentiation.show()
            btnInverse.show()
            btnPi.show()
            btnSwitch.background = null
        }
    }

}