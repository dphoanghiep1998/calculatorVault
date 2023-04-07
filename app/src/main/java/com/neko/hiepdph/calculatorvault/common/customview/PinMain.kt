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
import com.neko.hiepdph.calculatorvault.databinding.PinViewBinding

interface PinFunction {
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

    fun onPressButtonCheck()
    fun onPressButtonBackspace()


}


class PinMain @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: PinViewBinding
    private var callback: PinFunction? = null


    init {
        binding = PinViewBinding.inflate(LayoutInflater.from(context), this, false)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PinMain)
        initView()

        addView(binding.root)
        typedArray.recycle()
    }


    private fun initView() {

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

        binding.btnDelete.setOnClickListener {
            callback?.onPressButtonBackspace()
        }

        binding.btnCheck.setOnClickListener {
            callback?.onPressButtonCheck()
        }


    }

    fun setCalculatorMainCallback(callback: PinFunction) {
        this.callback = callback
    }


}