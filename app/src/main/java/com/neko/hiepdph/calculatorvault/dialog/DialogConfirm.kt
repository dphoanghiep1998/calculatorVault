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
import com.neko.hiepdph.calculatorvault.databinding.DialogConfirmBinding


enum class DialogConfirmType(
    val title: Int,
    val content: Int,
    val imageRes: Int,
    val negativeText: Int,
    val positiveText: Int
) {
    DELETE(
        R.string.delete,
        R.string.delete_instruction,
        R.drawable.ic_delete,
        R.string.cancel,
        R.string.yes
    ),
    DELETE_PERMANENT(
        R.string.delete,
        R.string.delete_permanent_instruction,
        R.drawable.ic_delete,
        R.string.cancel,
        R.string.yes
    ),
    EMPTY_BIN(
        R.string.delete,
        R.string.empty_bin_instruction,
        R.drawable.ic_delete,
        R.string.no,
        R.string.yes
    ),
    RESTORE(
        R.string.restore,
        R.string.restore_instruction,
        R.drawable.ic_delete,
        R.string.cancel,
        R.string.yes
    ),
    UNLOCK(
        R.string.unlock,
        R.string.unlock_instruction,
        R.drawable.ic_unlock,
        R.string.cancel,
        R.string.yes
    ),
    TIP(
        R.string.important_tip,
        R.string.tips_instruction,
        R.drawable.ic_unlock,
        R.string.never_show,
        R.string.go_it
    ),
    FORGOT_PASSWORD(
        R.string.forgot_password,
        R.string.confirm_your_question,
        R.drawable.ic_forgot_password,
        R.string.cancel,
        R.string.custom_ok
    ),
    TIP_HIDE_APP(
        R.string.tips,
        R.string.tips_hide_app_instruction,
        R.drawable.ic_tips,
        R.string.cancel,
        R.string.custom_ok
    ),
    BACK_NOTE(
        R.string.note, R.string.back_note_tip, R.drawable.ic_info, R.string.cancel, R.string.yes
    ),
    BACK_BROWSER(
        R.string.exit,
        R.string.quit_browser,
        R.drawable.ic_info,
        R.string.cancel,
        R.string.custom_ok
    ),
    PROHIBIT_TITLE(
        R.string.prohibit_press_title,
        R.string.prohibit_press_content,
        R.drawable.ic_tips,
        R.string.cancel,
        R.string.custom_ok
    )


}


class DialogConfirm(
    private val onPositiveClicked: () -> Unit,
    private val dialogType: DialogConfirmType,
    val name: String? = null,
    private val onNegativeClicked: (() -> Unit)? = null
) : DialogFragment() {
    private lateinit var binding: DialogConfirmBinding


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
        binding = DialogConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (dialogType.imageRes != 0) {
            binding.imvTitle.setImageResource(dialogType.imageRes)
        } else {
            binding.imvTitle.hide()
        }
        binding.tvTitle.text = requireContext().getText(dialogType.title)
        binding.tvInstruction.text = requireContext().getString(dialogType.content, name)
        binding.btnCancel.text = requireContext().getText(dialogType.negativeText)
        binding.btnConfirm.text = requireContext().getText(dialogType.positiveText)
        initButton()
    }

    private fun initButton() {
        binding.btnConfirm.clickWithDebounce {
            onPositiveClicked()
            dismiss()
        }
        binding.btnCancel.clickWithDebounce {
            onNegativeClicked?.invoke()
            dismiss()
        }
        binding.root.clickWithDebounce {
            onNegativeClicked?.invoke()
            dismiss()
        }
        binding.containerMain.clickWithDebounce {

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