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
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.databinding.DialogProgressBinding


class DialogProgress() : DialogFragment() {
    private lateinit var binding: DialogProgressBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = DialogCallBack(requireContext(), callback)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.setCancelable(false)
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
        binding = DialogProgressBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
    }

    fun setData(title: String, status: String) {
        binding.tvTitle.text = title
        binding.tvStatus.text = status
    }

    fun setProgressValue(value: Int) {
        binding.progressLoading.progress = value
        binding.tvProgress.text = "$value %"
    }

    fun hideButton(){
        binding.btnOk.hide()
    }
    fun statusSuccess(){
        binding.progressLoading.hide()
        binding.tvProgress.hide()
        binding.imvSuccess.show()
    }
    fun statusFailed(){
        binding.progressLoading.hide()
        binding.tvProgress.hide()
        binding.imvFailed.show()
    }

    fun enableCancelable(){
        binding.root.setOnClickListener {
            dismiss()
        }

    }
    fun disableCancelable(){
        binding.root.setOnClickListener {

        }
    }

    fun showButton(){
        binding.btnOk.show()
//        binding.btnTips.show()
    }

    private fun initButton() {
        binding.btnOk.clickWithDebounce {
            dismiss()
        }
        binding.containerMain.setOnClickListener {

        }
    }

    private fun openTips() {

    }

    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
        }

    }
}