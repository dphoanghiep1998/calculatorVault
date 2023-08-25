package com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.enums.Action
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.popBackStack
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.common.extensions.toastLocation
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.config.Status
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.FragmentListItemBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogAskEncryptionMode
import com.neko.hiepdph.calculatorvault.dialog.DialogProgress
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item.adapter.AdapterListItem
import com.neko.hiepdph.calculatorvault.viewmodel.ListItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FragmentListItem : Fragment() {
    private var _binding: FragmentListItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ListItemViewModel>()
    private var adapterListItem: AdapterListItem? = null
    private val args: FragmentListItemArgs by navArgs()
    private var listItemSelected = mutableListOf<FileVaultItem>()
    private var sizeList = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemBinding.inflate(inflater, container, false)
        toastLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
        setupTitle()
        getData()
    }

    private fun setupTitle() {
        if (args.groupItem.type != Constant.TYPE_FILE) {
            (requireActivity() as ActivityVault).getToolbar().title = args.groupItem.name
        } else {
            when (args.fileType) {
                Constant.TYPE_EXCEL -> (requireActivity() as ActivityVault).getToolbar().title =
                    getString(R.string.excel)

                Constant.TYPE_ZIP -> (requireActivity() as ActivityVault).getToolbar().title =
                    getString(R.string.zip)

                Constant.TYPE_TEXT -> (requireActivity() as ActivityVault).getToolbar().title =
                    getString(R.string.text)

                Constant.TYPE_PPT -> (requireActivity() as ActivityVault).getToolbar().title =
                    getString(R.string.ppt)

                Constant.TYPE_WORD -> (requireActivity() as ActivityVault).getToolbar().title =
                    getString(R.string.word)

                Constant.TYPE_CSV -> (requireActivity() as ActivityVault).getToolbar().title =
                    getString(R.string.csv)

                Constant.TYPE_PDF -> (requireActivity() as ActivityVault).getToolbar().title =
                    getString(R.string.pdf)

                else -> (requireActivity() as ActivityVault).getToolbar().title =
                    getString(R.string.other_file)
            }
        }
    }

    private fun initView() {
        initRecycleView()
        initButton()
    }

    private fun initButton() {

        binding.btnMoveToVault.clickWithDebounce {
            if (requireContext().config.encryptionMode != EncryptionMode.ALWAYS_ASK) {
                val dialogProgress = DialogProgress(listItemSelected,
                    listItemSelected.map { File(it.originalPath) },
                    listItemSelected.map { File(args.vaultPath) },
                    Action.ENCRYPT,
                    requireContext().config.encryptionMode,
                    onResult = { status, text, valuesReturn ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            when (status) {
                                Status.SUCCESS -> {
                                    showSnackBar(text, SnackBarType.SUCCESS)
                                }

                                Status.WARNING -> {
                                    showSnackBar(text, SnackBarType.WARNING)
                                }

                                Status.FAILED -> {
                                    showSnackBar(text, SnackBarType.FAILED)
                                }
                            }
                            popBackStack(R.id.fragmentListItem)
                        }

                    })
                dialogProgress.show(childFragmentManager, dialogProgress.tag)
            } else {
                val dialogAskEncryptionMode = DialogAskEncryptionMode(onPressPositive = { enMode ->
                    val dialogProgress = DialogProgress(listItemSelected,
                        listItemSelected.map { File(it.originalPath) },
                        listItemSelected.map { File(args.vaultPath) },
                        Action.ENCRYPT,
                        encryptionMode = enMode,
                        onResult = { status, text, valuesReturn ->
                            when (status) {
                                Status.SUCCESS -> {
                                    showSnackBar(text, SnackBarType.SUCCESS)
                                }

                                Status.WARNING -> {
                                    showSnackBar(text, SnackBarType.WARNING)
                                }

                                Status.FAILED -> {
                                    showSnackBar(text, SnackBarType.FAILED)
                                }
                            }
                            popBackStack(R.id.fragmentListItem)
                        })
                    dialogProgress.show(childFragmentManager, dialogProgress.tag)
                })
                dialogAskEncryptionMode.show(childFragmentManager, dialogAskEncryptionMode.tag)
            }
        }
    }

    private fun initToolBar(size: Int?) {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                Log.d("TAG", "onCreateMenu: " + size)
                if (size != null && size > 0) {
                    menuInflater.inflate(R.menu.toolbar_menu_pick, menu)
                    menu[0].actionView?.findViewById<View>(R.id.checkbox)?.setOnClickListener {
                        checkAllItem(menu[0].actionView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked == true)
                    }
                }

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun checkAllItem(status: Boolean) {
        if (status) {
            adapterListItem?.selectAll()

            binding.btnMoveToVault.apply {
                setBackgroundResource(R.drawable.bg_primary_corner_10)
                isEnabled = true
            }
        } else {
            adapterListItem?.unSelectAll()

            binding.btnMoveToVault.apply {
                setBackgroundResource(R.drawable.bg_neu04_corner_10)
                isEnabled = false
            }
        }
    }

    private fun getData() {
        if (args.groupItem.type != Constant.TYPE_FILE) {
            viewModel.getItemListFromFolder(
                requireContext(), args.groupItem.folderPath, args.groupItem.type
            )
        } else {
            viewModel.getItemListFromFolder(
                requireContext(), args.groupItem.folderPath, args.groupItem.type, args.fileType
            )
        }
        listItemSelected.clear()
    }

    private fun observeData() {
        viewModel.listItemList.observe(viewLifecycleOwner) {
            it?.let {
                adapterListItem?.submitList(it)
                sizeList = it.size
                binding.loading.hide()
                if (it.isNotEmpty()) {
                    binding.tvEmpty.hide()
                } else {
                    binding.tvEmpty.show()
                }
            }
            initToolBar(it?.size)
        }
    }

    private fun initRecycleView() {
        adapterListItem = AdapterListItem(onClickItem = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
            checkListPath()
            checkCheckBoxAll()
        }, onSelectAll = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
        }, args.groupItem.type)

        binding.rcvGroupItem.adapter = adapterListItem
        when (args.groupItem.type) {
            Constant.TYPE_PICTURE, Constant.TYPE_VIDEOS -> {
                val gridLayoutManager =
                    GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
                binding.rcvGroupItem.layoutManager = gridLayoutManager
            }

            Constant.TYPE_AUDIOS, Constant.TYPE_FILE -> {
                val linearLayoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                binding.rcvGroupItem.layoutManager = linearLayoutManager
            }

        }
    }

    private fun checkCheckBoxAll() {
        val checkbox =
            (requireActivity() as ActivityVault).getToolbar().menu[0].actionView?.findViewById<CheckBox>(
                R.id.checkbox
            )
        checkbox?.isChecked = listItemSelected.size == sizeList && sizeList > 0
    }

    private fun checkListPath() {
        if (listItemSelected.isEmpty()) {
            binding.btnMoveToVault.apply {
                setBackgroundResource(R.drawable.bg_neu04_corner_10)
                isEnabled = false
            }

        } else {
            binding.btnMoveToVault.apply {
                setBackgroundResource(R.drawable.bg_primary_corner_10)
                isEnabled = true
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.setListItemData(null)
    }
}