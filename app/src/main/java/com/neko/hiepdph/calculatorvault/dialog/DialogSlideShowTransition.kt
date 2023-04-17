package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.config.SlideShowTransition
import com.neko.hiepdph.calculatorvault.databinding.DialogSlideShowIntervalBinding
import com.neko.hiepdph.calculatorvault.databinding.DialogSlideshowTransitionBinding


class DialogSlideShowTransition(
) : DialogFragment() {
    private lateinit var binding: DialogSlideshowTransitionBinding
    private var selectedTransition = 0


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = DialogCallBack(requireContext(), callback)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(requireContext().getColor(R.color.blur)))
        dialog.window!!.setLayout(
            (requireContext().resources.displayMetrics.widthPixels),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogSlideshowTransitionBinding.inflate(inflater, container, false)
        selectedTransition = requireContext().config.slideShowTransition
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
        when(requireContext().config.slideShowTransition){
            SlideShowTransition.ROTATE -> binding.checkboxRotate.isChecked = true
            SlideShowTransition.ZOOM -> binding.checkboxZoom.isChecked = true
            SlideShowTransition.RIGHT -> binding.checkboxRight.isChecked = true
            SlideShowTransition.TOP -> binding.checkboxTop.isChecked = true
            SlideShowTransition.RANDOM -> binding.checkboxRandom.isChecked = true
            SlideShowTransition.LEFT -> binding.checkboxLeft.isChecked = true
            SlideShowTransition.FADE -> binding.checkboxFade.isChecked = true
            SlideShowTransition.BOTTOM -> binding.checkboxBottom.isChecked = true
            SlideShowTransition.NONE -> binding.checkboxNone.isChecked = true
            SlideShowTransition.NATURAL -> binding.checkboxNatural.isChecked = true
        }

    }

    private fun initButton() {
        binding.btnConfirm.clickWithDebounce {
            requireContext().config.slideShowTransition = selectedTransition
            dismiss()
        }
        binding.root.clickWithDebounce {
            dismiss()
        }
        binding.containerMain.setOnClickListener { }

        binding.checkboxRotate.setOnClickListener {
            selectedTransition = SlideShowTransition.ROTATE
        }
        binding.checkboxNatural.setOnClickListener {
            selectedTransition = SlideShowTransition.NATURAL
        }
        binding.checkboxNone.setOnClickListener {
            selectedTransition = SlideShowTransition.NONE
        }
        binding.checkboxRandom.setOnClickListener {
            selectedTransition = SlideShowTransition.RANDOM
        }
        binding.checkboxFade.setOnClickListener {
            selectedTransition = SlideShowTransition.FADE
        }
        binding.checkboxZoom.setOnClickListener {
            selectedTransition = SlideShowTransition.ZOOM
        }
        binding.checkboxLeft.setOnClickListener {
            selectedTransition = SlideShowTransition.LEFT
        }
        binding.checkboxRight.setOnClickListener {
            selectedTransition = SlideShowTransition.RIGHT
        }
        binding.checkboxTop.setOnClickListener {
            selectedTransition = SlideShowTransition.TOP
        }
        binding.checkboxBottom.setOnClickListener {
            selectedTransition = SlideShowTransition.BOTTOM
        }


    }

    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
        }

    }
}