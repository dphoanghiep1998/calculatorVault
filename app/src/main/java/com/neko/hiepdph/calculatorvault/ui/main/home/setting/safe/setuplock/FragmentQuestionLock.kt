package com.neko.hiepdph.calculatorvault.ui.main.home.setting.safe.setuplock

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.calculatorvault.CustomApplication
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.changeBackPressCallBack
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.navigateToPage
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.common.utils.EMPTY
import com.neko.hiepdph.calculatorvault.databinding.FragmentQuestionLockBinding
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault


class FragmentQuestionLock : Fragment() {
    private var _binding: FragmentQuestionLockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionLockBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initToolBar()
        if (!requireContext().config.isSetupPasswordDone) {
            (requireActivity() as ActivityVault).getToolbar().hide()
            binding.tvTile.show()
            changeBackPressCallBack { }
        } else {
            (requireActivity() as ActivityVault).getToolbar().show()
            binding.tvTile.hide()

        }
    }

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun hideSoftKeyboard(activity: Activity, view: View) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus?.windowToken, 0
            )
        }
        view.clearFocus()
    }

    private fun initView() {
        binding.root.setOnClickListener {
            hideSoftKeyboard(requireActivity(), binding.root)
        }
        initDropdown()
        initData()
    }

    private fun initData() {
        binding.edtAnswer.setText(requireContext().config.securityAnswer)
        if (requireContext().config.securityQuestion != String.EMPTY) {
            binding.question.setText(requireContext().config.securityQuestion)
        } else {
            binding.question.setText(getString(R.string.lock_ask_1))
        }
        binding.edtAnswer.setText(requireContext().config.securityAnswer)
    }

    private fun initDropdown() {
        binding.question.dismissDropDown()

        val arrayQuestion = arrayOf(
            getString(R.string.lock_ask_1),
            getString(R.string.lock_ask_2),
            getString(R.string.lock_ask_3),
            getString(R.string.lock_ask_4),
        )
        lifecycleScope.launchWhenResumed {
            binding.question.setSimpleItems(arrayQuestion)
        }


        binding.btnConfim.clickWithDebounce {
            if (binding.edtAnswer.text.isBlank()) {
                showSnackBar(getString(R.string.require_answer), SnackBarType.FAILED)
                return@clickWithDebounce
            }
            requireContext().config.securityQuestion = binding.question.text.toString()
            requireContext().config.securityAnswer = binding.edtAnswer.text.toString()
            if (!requireContext().config.isSetupPasswordDone) {
                requireContext().config.isSetupPasswordDone = true
                (requireActivity().application as CustomApplication).authority = true
                showSnackBar(getString(R.string.question_setup_successfully), SnackBarType.SUCCESS)
                navigateToPage(R.id.fragmentQuestionLock, R.id.fragmentVault)
            } else {
                showSnackBar(getString(R.string.change_question_lock_successfully), SnackBarType.SUCCESS)
                findNavController().popBackStack()

            }
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}