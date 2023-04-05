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
import com.neko.hiepdph.calculatorvault.databinding.DialogConfirmBinding
import com.neko.hiepdph.calculatorvault.databinding.DialogPermissionBinding
import com.neko.hiepdph.calculatorvault.dialog.BackPressDialogCallBack
import com.neko.hiepdph.calculatorvault.dialog.DialogCallBack


interface PermissionDialogCallBack {
    fun onPositiveClicked()
    fun onNegative()
}

class DialogPermission(
    private val callBack: PermissionDialogCallBack,
) : DialogFragment() {
    private lateinit var binding: DialogPermissionBinding


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
        binding = DialogPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
    }

    private fun initButton() {
        binding.btnAllow.clickWithDebounce {
            callBack.onPositiveClicked()
            dismiss()
        }
        binding.root.clickWithDebounce {
//            callBack.onNegative()
//            dismiss()
        }
    }
}

private val callback = object : BackPressDialogCallBack {
    override fun shouldInterceptBackPress(): Boolean {
        return true
    }

    override fun onBackPressIntercepted() {
    }

}