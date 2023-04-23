package com.neko.hiepdph.calculatorvault.ui.main.home.recyclebin

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import com.neko.hiepdph.calculatorvault.databinding.FragmentRecycleBinBinding
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityImageDetail
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
    private var listItemSelected:MutableList<ListItem> = mutableListOf()
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
                if (AdapterRecyclerBin.editMode) {
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
                return if (AdapterRecyclerBin.editMode) {
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
        adapterRecycleBin = AdapterRecyclerBin(
            onClickItem = {
            handleClickItem(it)
        }, onLongClickItem = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
            initToolBar()
            editHome()

        }, onSelectAll = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
        }, onUnSelect = {
//            unCheckItem()
        }, onEditItem = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
            checkItem()
        })

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rcvItems.layoutManager = layoutManager

        binding.rcvItems.adapter = adapterRecycleBin
    }


    private fun editHome() {
        val actionBar = (requireActivity() as? ActivityVault)?.supportActionBar
        val exitIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_exit)
        exitIcon?.let {
            actionBar?.setHomeAsUpIndicator(it)
        }
    }

    private fun handleClickItem(it: ListItem) {
        when (it.type) {
            Constant.TYPE_PICTURE -> {
                val list = mutableListOf<ListItem>()
                list.add(it)
                ShareData.getInstance().setListItemImage(list)
                val intent = Intent(requireContext(), ActivityImageDetail::class.java)
                startActivity(intent)
            }
            Constant.TYPE_AUDIOS -> {}
            Constant.TYPE_VIDEOS -> {}
            else -> {

            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}