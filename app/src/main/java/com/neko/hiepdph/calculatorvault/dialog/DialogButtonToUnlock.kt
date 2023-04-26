package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.config.ButtonToUnlock
import com.neko.hiepdph.calculatorvault.databinding.DialogButtonToUnlockBinding


class DialogButtonToUnlock : DialogFragment() {
    private lateinit var binding: DialogButtonToUnlockBinding
    private var selectedOption = 0


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
        binding = DialogButtonToUnlockBinding.inflate(inflater, container, false)
        selectedOption = requireContext().config.buttonToUnlock
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
        when (requireContext().config.buttonToUnlock) {
            ButtonToUnlock.NONE -> binding.checkboxNone.isChecked = true
            ButtonToUnlock.SHORT_PRESS -> binding.checkboxShortpress.isChecked = true
            ButtonToUnlock.LONG_PRESS -> binding.checkboxLongpress.isChecked = true
        }

    }

    private fun initButton() {
        binding.btnConfirm.clickWithDebounce {
            requireContext().config.buttonToUnlock = selectedOption
            dismiss()
        }
        binding.root.clickWithDebounce {
            dismiss()
        }
        binding.containerMain.setOnClickListener { }

        binding.checkboxNone.setOnClickListener {
            selectedOption = ButtonToUnlock.NONE
        }
        binding.checkboxShortpress.setOnClickListener {
            selectedOption = ButtonToUnlock.SHORT_PRESS
        }
        binding.checkboxLongpress.setOnClickListener {
            selectedOption = ButtonToUnlock.LONG_PRESS
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