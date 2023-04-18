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
import com.neko.hiepdph.calculatorvault.common.utils.DateTimeUtils
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.databinding.DialogDetailBinding


class DialogDetail(
   private var name: String? = null,
   private var size: Long? = null,
   private var resolution: String? = null,
   private var time: Long? = null,
   private var timeLock: Long? = null,
   private var path:String?=null
) : DialogFragment() {
    private lateinit var binding: DialogDetailBinding


    private constructor(builder: Builder) : this(
        builder.name,
        builder.size,
        builder.resolution,
        builder.time,
        builder.timeLock,
        builder.path
    )

    companion object {
        inline fun dialogDetailConfig(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var name: String? = null
        var size: Long? = null
        var resolution: String? = null
        var time: Long? = null
        var timeLock: Long? = null
        var path:String?=null

        fun build() = DialogDetail(this)

    }

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
        binding = DialogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (time == null) {
            binding.tvTime.hide()
            binding.tvTimeValue.hide()
        } else {
            binding.tvTimeValue.text = DateTimeUtils.getDateConverted(time!!)
        }

        if (name == null) {
            binding.tvName.hide()
            binding.tvNameValue.hide()
        } else {
            binding.tvNameValue.text = name
        }

        if (size == null) {
            binding.tvSize.hide()
            binding.tvSizeValue.hide()
        } else {
            binding.tvSizeValue.text = size!!.formatSize()
        }

        if (resolution == null) {
            binding.tvResolution.hide()
            binding.tvResolutionValue.hide()
        } else {
            binding.tvResolutionValue.text = "12x12"
        }

        if (timeLock == null) {
            binding.tvTimeLock.hide()
            binding.tvTimeLockValue.hide()
        } else {
            binding.tvTimeLockValue.text = DateTimeUtils.getDateConverted(timeLock!!)
        }

        if (path == null) {
            binding.tvPath.hide()
            binding.tvPathValue.hide()
        } else {
            binding.tvPathValue.text = path
        }



        initButton()
    }

    private fun initButton() {


        binding.btnConfirm.clickWithDebounce {
            dismiss()
        }
        binding.root.clickWithDebounce {
            dismiss()
        }

        binding.containerMain.setOnClickListener { }
    }

    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
        }

    }
}