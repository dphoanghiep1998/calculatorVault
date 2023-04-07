package com.neko.hiepdph.calculatorvault.common.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.graphics.drawable.DrawableCompat
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.databinding.ItemCalculatorBinding
import com.neko.hiepdph.calculatorvault.databinding.ItemPinBinding


class ButtonPin @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding: ItemPinBinding
    private var textButton: String? = null
    private var srcImage: Int = 0


    init {
        binding = ItemPinBinding.inflate(LayoutInflater.from(context), this, false)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonPin)
        textButton = try {
            typedArray.getText(R.styleable.ButtonPin_buttonTextPin).toString()
        } catch (e: Exception) {
            null
        }
        srcImage = typedArray.getResourceId(R.styleable.ButtonPin_srcPin, 0)


        initView()
        addView(binding.root)
        typedArray.recycle()
    }

    fun changeTextColor(color: Int) {
        binding.tvButton.setTextColor(color)
    }

    fun setTint(color: Int) {
        DrawableCompat.setTint(
            binding.imvButton.drawable, color
        )
    }

    fun changeText(text: String) {
        binding.tvButton.text = text
    }

    fun changeImageRes(imageRes: Int) {
        binding.imvButton.setImageResource(imageRes)
    }

    fun hideImageView() {
        binding.imvButton.hide()
    }

    fun showImageView() {
        binding.imvButton.show()
    }

    fun hideTextView() {
        binding.tvButton.hide()
    }

    fun showTextView() {
        binding.tvButton.show()
    }

    private fun initView() {
        binding.root.setBackgroundResource(R.drawable.bg_item_pin)
        if (textButton == null) {
            binding.tvButton.hide()
        } else {
            binding.tvButton.show()
            binding.tvButton.text = textButton
        }

        if (srcImage == 0) {
            binding.imvButton.hide()
        } else {
            binding.imvButton.show()
            binding.imvButton.setImageResource(srcImage)
        }

    }
}