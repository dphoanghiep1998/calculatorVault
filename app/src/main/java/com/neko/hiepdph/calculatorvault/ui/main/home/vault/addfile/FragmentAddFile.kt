package com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.navigateToPage
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.toastLocation
import com.neko.hiepdph.calculatorvault.databinding.FragmentAddFileBinding
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.adapter.AdapterGroupItem
import com.neko.hiepdph.calculatorvault.viewmodel.AddFileViewModel


class FragmentAddFile : Fragment() {
    private var _binding: FragmentAddFileBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdapterGroupItem
    private val viewModel by viewModels<AddFileViewModel>()
    private val args: FragmentAddFileArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFileBinding.inflate(inflater, container, false)
        toastLocation()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getDataGroupFile(args.type)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getDataFromArgs()
        observeListGroupData()
        initToolBar()
    }


    private fun getDataFromArgs() {
        (requireActivity() as ActivityVault).getToolbar().title = args.title
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


    private fun initView() {
        initRecyclerView()
    }

    private fun getDataGroupFile(type: String) {
        viewModel.getListGroupItem(requireContext(), type)
    }

    private fun initRecyclerView() {
        adapter = AdapterGroupItem(args.type, onClickFolderItem = { groupItem, fileType ->
            val action = FragmentAddFileDirections.actionFragmentAddFileToFragmentListItem(
                fileType, args.vaultPath, groupItem
            )
            navigateToPage(R.id.fragmentAddFile, action)
        })
        binding.rcvGroupItem.adapter = adapter
        val gridLayoutManager = if (args.type == Constant.TYPE_FILE) {
            GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        } else {
            GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        }
        binding.rcvGroupItem.layoutManager = gridLayoutManager
    }

    private fun observeListGroupData() {
        viewModel.listItemListGroupFile.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
                binding.loading.hide()
                if (it.isNotEmpty()) {
                    binding.tvEmpty.hide()
                } else {
                    binding.tvEmpty.show()
                }
            }
        }

    }


    override fun onDestroy() {
        viewModel.setListItemPersistentData(mutableListOf())
        _binding = null
        super.onDestroy()
    }

}