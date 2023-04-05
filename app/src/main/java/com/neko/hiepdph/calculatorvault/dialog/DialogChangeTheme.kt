package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.databinding.DialogChoseThemeBinding
import java.lang.String


class DialogChangeTheme : DialogFragment() {
    private lateinit var binding: DialogChoseThemeBinding
    private var customEnabled = false
    private var rValue = 0
    private var gValue = 0
    private var bValue = 0
    private var currentColor = 0


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(requireContext().getColor(R.color.transparent)))

        dialog.window!!.setLayout(
            (requireContext().resources.displayMetrics.widthPixels),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogChoseThemeBinding.inflate(inflater, container, false)
        currentColor = AppSharePreference.INSTANCE.getThemeColor(requireContext().getColor(R.color.theme_default))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
        initCheckBox()
        initSeekBar()
        initData()
    }

    private fun initData() {
        rValue = getRGB(currentColor)[0]
        gValue = getRGB(currentColor)[1]
        bValue = getRGB(currentColor)[2]

        binding.seekB.progress = bValue
        binding.seekR.progress = rValue
        binding.seekG.progress = gValue
        updateTheme()
    }

    private fun getRGB(hex: Int): IntArray {
        val r = hex and 0xFF0000 shr 16
        val g = hex and 0xFF00 shr 8
        val b = hex and 0xFF
        return intArrayOf(r, g, b)
    }

    private fun initSeekBar() {
        binding.seekG.max = 255
        binding.seekR.max = 255
        binding.seekB.max = 255

        binding.seekR.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                rValue = p1
                updateTheme()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
        binding.seekG.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                gValue = p1
                updateTheme()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
        binding.seekB.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                bValue = p1
                updateTheme()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    private fun initCheckBox() {
        val groupCheckBox = mutableListOf(
            binding.themeDefault,
            binding.theme01,
            binding.theme02,
            binding.theme03,
            binding.theme04,
            binding.theme05,
            binding.theme06,
            binding.theme07,
            binding.theme08,
            binding.theme09,
            binding.theme10,
            binding.theme11,
            binding.theme12,
            binding.theme13,
            binding.theme14,
            binding.theme15,
        )
        val groupColor = mutableListOf(
            requireContext().getColor(R.color.theme_default),
            requireContext().getColor(R.color.theme_01),
            requireContext().getColor(R.color.theme_02),
            requireContext().getColor(R.color.theme_03),
            requireContext().getColor(R.color.theme_04),
            requireContext().getColor(R.color.theme_05),
            requireContext().getColor(R.color.theme_06),
            requireContext().getColor(R.color.theme_07),
            requireContext().getColor(R.color.theme_08),
            requireContext().getColor(R.color.theme_09),
            requireContext().getColor(R.color.theme_10),
            requireContext().getColor(R.color.theme_11),
            requireContext().getColor(R.color.theme_12),
            requireContext().getColor(R.color.theme_13),
            requireContext().getColor(R.color.theme_14),
            requireContext().getColor(R.color.theme_15),
        )
        groupColor.forEachIndexed { index, item ->
            Log.d("TAG", "currentColor: " + currentColor)
            Log.d("TAG", "item: " + item)
            if (item == currentColor) {
                groupCheckBox[index].isChecked = true
            }
        }

        groupCheckBox.forEachIndexed { index, item ->
            item.setOnClickListener {
                if (item.isChecked) {
                    groupCheckBox.filter {
                        it.isChecked
                    }.forEach { it.isChecked = false }
                    item.isChecked = true
                    AppSharePreference.INSTANCE.setThemeColor(groupColor[index])
                } else {
                    item.isChecked = true
                }
            }
        }
    }

    private fun updateTheme() {
        binding.tvValueR.text = rValue.toString()
        binding.tvValueG.text = gValue.toString()
        binding.tvValueB.text = bValue.toString()
        binding.edtValue.setText(String.format("#%02X%02X%02X", rValue, gValue, bValue))
        binding.themeShowcase.setBackgroundColor(Color.rgb(rValue, gValue, bValue))
        currentColor = Color.rgb(rValue, gValue, bValue)
    }

    private fun initButton() {
        binding.tvCancel.setTextColor(
            AppSharePreference.INSTANCE.getThemeColor(
                requireContext().getColor(
                    R.color.theme_default
                )
            )
        )
        binding.tvOk.setTextColor(
            AppSharePreference.INSTANCE.getThemeColor(
                requireContext().getColor(
                    R.color.theme_default
                )
            )
        )
        binding.tvCustom.setTextColor(
            AppSharePreference.INSTANCE.getThemeColor(
                requireContext().getColor(
                    R.color.theme_default
                )
            )
        )

        binding.containerMain.clickWithDebounce { }

        binding.tvCustom.clickWithDebounce {
            customEnabled = !customEnabled
            if (customEnabled) {
                binding.containerChoose.hide()
                binding.containerCustom.show()
                binding.tvCustom.text = getString(R.string.back)
                binding.tvTitle.text = getString(R.string.custom)
            } else {
                binding.containerChoose.show()
                binding.containerCustom.hide()
                binding.tvCustom.text = getString(R.string.custom)
                binding.tvTitle.text = getString(R.string.choose_theme)
            }

        }
        binding.tvCancel.clickWithDebounce {
            dismiss()
        }
        binding.root.clickWithDebounce {
            dismiss()
        }
        binding.tvOk.clickWithDebounce {
            AppSharePreference.INSTANCE.setThemeColor(currentColor)
            dismiss()
        }
    }
}