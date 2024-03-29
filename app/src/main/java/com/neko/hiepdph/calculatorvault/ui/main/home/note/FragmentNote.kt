package com.neko.hiepdph.calculatorvault.ui.main.home.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.changeBackPressCallBack
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.databinding.FragmentNoteBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.note.adapter.AdapterNote
import com.neko.hiepdph.calculatorvault.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentNote : Fragment() {
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<NoteViewModel>()
    private var adapterNote: AdapterNote? = null
    private var normalMenuProvider: MenuProvider? = null
    private var listIdNoteSelected = mutableListOf<Int>()
    private var sizeNote = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        changeBackPressCallBack {
            requireActivity().finishAffinity()
        }
        val actionBar = (requireActivity() as? ActivityVault)?.supportActionBar
        actionBar?.title = getString(R.string.note)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initToolBar()
        observeNote()
    }

    private fun observeNote() {
        viewModel.getAllNote().observe(viewLifecycleOwner) {
            adapterNote?.setData(it)
            sizeNote = it.size
            binding.loading.hide()
            if (it.isNotEmpty()) {
                binding.tvEmpty.hide()
            } else {
                binding.tvEmpty.show()
            }
            adapterNote?.changeToNormalView()
            (requireActivity() as ActivityVault).setupActionBar()
        }
    }

    private fun initView() {
        binding.tvEmpty.text = String.format(
            getString(R.string.empty_text), getString(R.string.note), getString(R.string.note_lower)
        )
        initRecyclerView()
        initButton()
    }

    private fun initButton() {
        binding.floatingActionButton.clickWithDebounce {
            addNote()
        }
    }

    private fun initRecyclerView() {
        adapterNote = AdapterNote(onClickItem = {
            lifecycleScope.launchWhenResumed {
                val action = FragmentNoteDirections.actionFragmentNoteToFragmentAddNote(it)
                findNavController().navigate(action)
            }
        }, onLongClickItem = {
            initToolBar()
            editHome()
//            (requireActivity() as ActivityVault).getToolbar().setNavigationIcon(R.drawable.ic_exit)
//            (requireActivity() as ActivityVault).getToolbar().setNavigationOnClickListener {
//                adapterNote?.changeToNormalView()
//                initToolBar()
//                (requireActivity() as ActivityVault).setupActionBar()
//            }
        }, onEditItem = {
            listIdNoteSelected.clear()
            listIdNoteSelected.addAll(it)
            checkNote()

        }, onUnSelect = {
            unCheckNote()
        }, onSelectAll = {
            listIdNoteSelected.clear()
            listIdNoteSelected.addAll(it)
        })

        binding.rcvItemNote.adapter = adapterNote

        if (AdapterNote.isSwitchView) {
            val gridLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            binding.rcvItemNote.layoutManager = gridLayoutManager
        } else {
            val linearLayoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.rcvItemNote.layoutManager = linearLayoutManager
        }
    }


    private fun queryNote(query: String) {
        adapterNote?.filterNote(query)
    }

    private fun initToolBar() {
        normalMenuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (AdapterNote.editMode) {
                    menuInflater.inflate(R.menu.toolbar_menu_edit_note, menu)
                    menu[1].actionView?.findViewById<View>(R.id.checkbox)?.setOnClickListener {
                        checkAllNote(menu[1].actionView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked == true)
                    }
                } else {
                    menuInflater.inflate(R.menu.toolbar_menu_note, menu)
                    if (AdapterNote.isSwitchView) {
                        menu[0].icon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_list)
                    } else {
                        menu[0].icon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid_layout)
                    }

                    val actionExpandListener = object : OnActionExpandListener {
                        override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                            return true;
                        }

                        override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                            return true;
                        }

                    }
                    val searchItem = menu.findItem(R.id.search)
                    searchItem.setOnActionExpandListener(actionExpandListener)
                    val searchView = searchItem.actionView as SearchView
                    searchView.queryHint = getString(R.string.search_query)
                    searchView.setOnQueryTextListener(object : OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            query?.let {
                                queryNote(it)
                            }
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            newText?.let {
                                queryNote(it)
                            }
                            return true
                        }

                    })

                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (AdapterNote.editMode) {
                    return when (menuItem.itemId) {
                        R.id.delete -> {
                            deleteNote()
                            true
                        }

                        android.R.id.home -> {
                            adapterNote?.changeToNormalView()
                            (requireActivity() as ActivityVault).setupActionBar()
                            initToolBar()
                            true
                        }

                        else -> false
                    }

                } else {
                    return when (menuItem.itemId) {
                        R.id.layout -> {
                            changeLayout(menuItem)
                            true
                        }

                        else -> false
                    }
                }
            }
        }
        requireActivity().addMenuProvider(
            normalMenuProvider as MenuProvider, viewLifecycleOwner, Lifecycle.State.CREATED
        )
    }

    private fun editHome() {
        val actionBar = (requireActivity() as? ActivityVault)?.supportActionBar
        val exitIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_exit)
        exitIcon?.let {
            actionBar?.setHomeAsUpIndicator(it)
        }

    }

    private fun checkNote() {
        val checkbox =
            (requireActivity() as ActivityVault).getToolbar().menu[1].actionView?.findViewById<CheckBox>(
                R.id.checkbox
            )
        checkbox?.isChecked = listIdNoteSelected.size == sizeNote && sizeNote > 0
    }

    private fun unCheckNote() {
        val checkbox =
            (requireActivity() as ActivityVault).getToolbar().menu[1].actionView?.findViewById<CheckBox>(
                R.id.checkbox
            )
        checkbox?.isChecked = false
    }

    private fun checkAllNote(status: Boolean) {
        if (status) {
            adapterNote?.selectAll()
        } else {
            adapterNote?.unSelectAll()
        }
    }


    private fun changeLayout(item: MenuItem) {
        AdapterNote.isSwitchView = !AdapterNote.isSwitchView
        if (AdapterNote.isSwitchView) {
            val gridLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
            binding.rcvItemNote.layoutManager = gridLayoutManager
            item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_list)
        } else {
            val linearLayoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.rcvItemNote.layoutManager = linearLayoutManager
            item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid_layout)
        }
    }

    private fun deleteNote() {
        if (listIdNoteSelected.isEmpty()) {
            showSnackBar(getString(R.string.nothing_to_delete), SnackBarType.FAILED)
        } else {
            val confirmDialog = DialogConfirm(onPositiveClicked = {
                viewModel.deleteNote(listIdNoteSelected)
            }, DialogConfirmType.DELETE, getString(R.string.this_note))
            confirmDialog.show(childFragmentManager, confirmDialog.tag)
        }
    }

    private fun addNote() {
        val action = FragmentNoteDirections.actionFragmentNoteToFragmentAddNote(null)

        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        AdapterNote.editMode = false
        super.onDestroyView()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}