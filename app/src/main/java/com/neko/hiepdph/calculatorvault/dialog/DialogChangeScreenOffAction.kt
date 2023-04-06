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
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.config.LockType
import com.neko.hiepdph.calculatorvault.config.ScreenOffAction
import com.neko.hiepdph.calculatorvault.databinding.DialogLockTypeBinding
import com.neko.hiepdph.calculatorvault.databinding.DialogScreenOffActionBinding

class DialogChangeScreenOffAction : DialogFragment() {
    private lateinit var binding: DialogScreenOffActionBinding
    private var currentScreenOffAction = 0

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
        binding = DialogScreenOffActionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initData()
        initButton()
    }

    private fun initData() {
        currentScreenOffAction = requireContext().config.lockType
        when (requireContext().config.lockType) {
            ScreenOffAction.NOACTION -> binding.checkboxNoAction.isChecked = true
            ScreenOffAction.LOCKAGAIN -> binding.checkboxLockAgain.isChecked = true
            ScreenOffAction.GOTOHOMESCREEN -> binding.checkboxGoToHomescreen.isChecked = true
        }
    }

    private fun initButton() {
        binding.root.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.containerMain.setOnClickListener {

        }
        binding.checkboxNoAction.setOnClickListener {
            currentScreenOffAction = ScreenOffAction.NOACTION
        }
        binding.checkboxLockAgain.setOnClickListener {
            currentScreenOffAction = ScreenOffAction.LOCKAGAIN
        }
        binding.checkboxGoToHomescreen.setOnClickListener {
            currentScreenOffAction = ScreenOffAction.GOTOHOMESCREEN
        }
        binding.btnConfirm.clickWithDebounce {
            requireContext().config.screenOffAction = currentScreenOffAction
            findNavController().popBackStack()
        }
    }
}