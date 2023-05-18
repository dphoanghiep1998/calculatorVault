package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.databinding.DialogRateUsBinding

interface RateCallBack {
    //    fun rateOnStore()
    fun onNegativePressed()
    fun onPositivePressed(star: Int)
}

class DialogRateUs(private val callBack: RateCallBack) : DialogFragment() {
    private lateinit var binding: DialogRateUsBinding
    private var star = 0
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
        binding = DialogRateUsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initFirst()
        initButton()

    }

    private fun initButton() {
        binding.btnRate.clickWithDebounce {
            if (star == 0) {
                Toast.makeText(
                    requireContext(), getString(R.string.please_rate), Toast.LENGTH_SHORT
                ).show()
            } else {
                callBack.onPositivePressed(star)
                dismiss()

            }
        }
        binding.root.clickWithDebounce {
            val time = System.currentTimeMillis()
//            AppSharePreference.INSTANCE.saveTimeToShowRate(time)
            callBack.onNegativePressed()
            dismiss()
        }
        binding.btnLater.clickWithDebounce {
            val time = System.currentTimeMillis()
//            AppSharePreference.INSTANCE.saveTimeToShowRate(time)
            callBack.onNegativePressed()
            dismiss()
        }

    }


    private fun initFirst() {
        binding.root.clickWithDebounce {
            dismiss()
        }
        binding.containerMain.clickWithDebounce {}

        val groupImageStatus = listOf(
            R.drawable.ic_status_1,
            R.drawable.ic_status_2,
            R.drawable.ic_status_3,
            R.drawable.ic_status_4,
            R.drawable.ic_status_5,
        )
        val groupStar =
            listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)

        val textExpressive = listOf(
            R.string.expressive_bad,
            R.string.expressive_bad_a_little,
            R.string.expressive_normal,
            R.string.expressive_good,
            R.string.expressive_best
        )
        groupStar.forEachIndexed { index, item ->
            kotlin.run {
                item.clickWithDebounce {
                    star = index + 1
                    binding.btnLater.visibility = View.GONE
                    binding.tvStatus2.text = getString(textExpressive[index])
                    binding.imvStatus.setImageResource(groupImageStatus[index])
                    when (star) {
                        in 1..3 -> {
                            binding.tvStatus1.text = getString(R.string.oh_no)
                            binding.btnRate.text = getString(R.string.rate_us_button)
                        }
                        4 -> {
                            binding.tvStatus1.text = getString(R.string.so_amazing)
                            binding.btnRate.text = getString(R.string.rate_us_button)
                        }
                        else -> {
                            binding.tvStatus1.text = getString(R.string.we_like_you_too)
                            binding.btnRate.text = getString(R.string.rate_on_google_play)
                        }
                    }
                    binding.tvStatus1.visibility = View.VISIBLE

                    val subStar = groupStar.slice(0..index)
                    if (index < groupStar.size - 1) {
                        val subStarInActive = groupStar.slice(index + 1..4)
                        subStarInActive.forEachIndexed { _, item ->
                            kotlin.run {
                                if (item.id != R.id.star_5) {
                                    item.setImageResource(
                                        R.drawable.ic_star_inactive
                                    )
                                } else {
                                    item.setImageResource(
                                        R.drawable.ic_last_star_inactive
                                    )
                                }

                            }
                        }
                    }
                    subStar.forEachIndexed { index, item ->
                        kotlin.run {
                            if (index == 4) {
                                item.setImageResource(R.drawable.ic_last_star_active)
                            } else {
                                item.setImageResource(
                                    R.drawable.ic_star_active
                                )
                            }

                        }
                    }
                }
            }
        }
    }

}