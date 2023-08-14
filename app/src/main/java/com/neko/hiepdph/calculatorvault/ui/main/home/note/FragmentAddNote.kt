package com.neko.hiepdph.calculatorvault.ui.main.home.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.hideSoftKeyboard
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.data.model.NoteModel
import com.neko.hiepdph.calculatorvault.databinding.FragmentAddNoteBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class FragmentAddNote : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        initToolBar()
        changeBackPressCallBack()
        return binding.root
    }

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.toolbar_menu_add_note, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.check -> {
                        saveNote()
                        true
                    }

                    else -> false

                }
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }


    private fun saveNote() {
        hideSoftKeyboard(requireActivity(), binding.root)
        if (binding.edtTitle.text.isBlank()) {
            showSnackBar(getString(R.string.title_and_content_required), SnackBarType.FAILED)
            return
        }
        val title = binding.edtTitle.text.toString()
        val content = binding.edtContent.text.toString()
        val date = Calendar.getInstance().timeInMillis
        viewModel.insertNewNote(NoteModel(-1, title, content, date))
        findNavController().popBackStack()
    }

    private fun changeBackPressCallBack() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.edtTitle.text.isNotBlank() || binding.edtContent.text.isNotBlank()) {
                        val dialogConfirm =
                            DialogConfirm(onPositiveClicked = {
                                findNavController().popBackStack()
                            }, DialogConfirmType.BACK_NOTE)
                        dialogConfirm.show(childFragmentManager, dialogConfirm.tag)
                    }else{
                        findNavController().popBackStack()
                    }
                }

            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}