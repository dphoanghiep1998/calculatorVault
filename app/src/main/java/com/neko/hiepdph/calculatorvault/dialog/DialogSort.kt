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
import androidx.fragment.app.FragmentManager
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.enums.Order
import com.neko.hiepdph.calculatorvault.common.enums.Sort
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.databinding.DialogSortBinding


interface SortDialogCallBack {
    fun onPositiveClicked(mSortType: Sort, mOrder: Order)
}

class DialogSort(
    private val callBack: SortDialogCallBack,
    private val sortType: Sort,
    private val sortOrder: Order

) : DialogFragment() {
    private lateinit var binding: DialogSortBinding
    private var dSortType: Sort = sortType
    private var dSortOrder: Order = sortOrder


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = DialogCallBack(requireContext(), callback)
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
        binding = DialogSortBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager?.beginTransaction()
            ft?.add(this, tag)
            ft?.commitAllowingStateLoss()
        } catch (ignored: IllegalStateException) {

        }
    }

    private fun initView() {
        initData()
        initButton()
    }

    private fun initData() {
        when (sortType) {
            Sort.NAME -> {
                binding.checkboxName.isChecked = true
            }
            Sort.SIZE -> {
                binding.checkboxSize.isChecked = true
            }
            Sort.RANDOM -> {
                binding.checkboxRandom.isChecked = true
            }
        }

        when (sortOrder) {
            Order.ASC -> {
                binding.checkboxAscending.isChecked = true
            }
            Order.DES -> {
                binding.checkboxDescending.isChecked = true
            }
        }
    }

    private fun initButton() {
        binding.btnConfirm.clickWithDebounce {
            callBack.onPositiveClicked(dSortType, dSortOrder)
            dismissAllowingStateLoss()
        }
        binding.btnCancel.clickWithDebounce {
            dismissAllowingStateLoss()
        }
        binding.root.clickWithDebounce {
            dismissAllowingStateLoss()
        }
        binding.containerMain.setOnClickListener {

        }

        binding.checkboxAscending.setOnClickListener {
            dSortOrder = Order.ASC
        }
        binding.checkboxDescending.setOnClickListener {
            dSortOrder = Order.DES
        }
        binding.checkboxName.setOnClickListener {
            dSortType = Sort.NAME
        }
        binding.checkboxSize.setOnClickListener {
            dSortType = Sort.SIZE
        }
        binding.checkboxRandom.setOnClickListener {
            dSortType = Sort.RANDOM
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