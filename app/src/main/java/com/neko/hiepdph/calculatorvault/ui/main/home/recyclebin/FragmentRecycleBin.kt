package com.neko.hiepdph.calculatorvault.ui.main.home.recyclebin

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.databinding.FragmentRecycleBinBinding
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.recyclebin.adapter.AdapterRecyclerBin
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter.AdapterOtherFolder
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter.AdapterPersistent
import com.neko.hiepdph.calculatorvault.viewmodel.RecyclerBinViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentRecycleBin : Fragment() {
    private var _binding: FragmentRecycleBinBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RecyclerBinViewModel>()

    private var adapterRecycleBin: AdapterRecyclerBin? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initToolBar()
        getData()
    }

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (AdapterPersistent.editMode || AdapterOtherFolder.editMode) {
                    menu.clear()
                    menuInflater.inflate(R.menu.toolbar_menu_recycler_bin_edit_mode, menu)
                    menu[2].actionView?.findViewById<View>(R.id.checkbox)?.setOnClickListener {
                        checkAllItem(menu[0].actionView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked == true)
                    }
                    checkItem()
                } else {
                    menu.clear()
                    menuInflater.inflate(R.menu.toolbar_menu_recycler_bin, menu)
                }

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (AdapterPersistent.editMode || AdapterOtherFolder.editMode) {
                    when (menuItem.itemId) {
                        android.R.id.home -> {
                            adapterRecycleBin?.changeToNormalView()
                            (requireActivity() as ActivityVault).setupActionBar()
                            initToolBar()
                            true
                        }
                        R.id.restore_item -> {

                            true
                        }
                        R.id.delete_item -> {

                            true
                        }
                        else -> false
                    }
                } else {
                    when (menuItem.itemId) {
                        R.id.edit_item -> {

                            true
                        }
                        R.id.delete_item -> {

                            true
                        }
                        else -> false
                    }
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun getData() {
        viewModel.getAllFileChildFromFolder(requireContext().config.recyclerBinFolder.path)
        viewModel.listItemListRecyclerBin.observe(viewLifecycleOwner) {
            it?.let {
                adapterRecycleBin?.setData(it)
            }
        }
    }

    private fun checkItem() {

    }

    private fun checkAllItem(status: Boolean) {
        if (status) {
            adapterRecycleBin?.selectAll()

        } else {
            adapterRecycleBin?.unSelectAll()

        }
    }

    private fun initView() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapterRecycleBin = AdapterRecyclerBin(onEditItem = {},
            onUnSelect = {},
            onSelectAll = {},
            onClickItem = {},
            onLongClickItem = {})

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rcvItems.layoutManager = layoutManager

        binding.rcvItems.adapter = adapterRecycleBin
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}