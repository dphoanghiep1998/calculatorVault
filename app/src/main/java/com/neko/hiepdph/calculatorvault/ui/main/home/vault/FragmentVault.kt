package com.neko.hiepdph.calculatorvault.ui.main.home.vault

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.enums.Order
import com.neko.hiepdph.calculatorvault.common.enums.Sort
import com.neko.hiepdph.calculatorvault.common.extensions.*

import com.neko.hiepdph.calculatorvault.data.model.VaultFileDirItem
import com.neko.hiepdph.calculatorvault.databinding.FragmentVaultBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutMenuOptionBinding
import com.neko.hiepdph.calculatorvault.dialog.*
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityCalculator
import com.neko.hiepdph.calculatorvault.ui.main.home.language.adapter.AdapterFolder
import com.neko.hiepdph.calculatorvault.viewmodel.VaultViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Pattern

@AndroidEntryPoint
class FragmentVault : Fragment() {
    private var _binding: FragmentVaultBinding? = null
    private val binding get() = _binding!!
    private lateinit var popupWindow: PopupWindow
    private  var adapter: AdapterFolder ?= null
    private val viewModel by activityViewModels<VaultViewModel>()

    companion object {
        var sortType: Sort = Sort.NAME
        var order: Order = Order.ASC
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVaultBinding.inflate(inflater, container, false)
//        changeBackPressCallBack {
//            requireActivity().finishAffinity()
//        }
        initToolBar()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeListFile()
    }
    override fun onResume() {
        super.onResume()
        viewModel.getListFolderInVault(
            requireContext(), requireContext().config.privacyFolder
        )
    }

    private fun observeListFile() {
        viewModel.listFolderInVault.observe(viewLifecycleOwner) {
            adapter?.setData(sortList(it))
            binding?.swipeLayout?.isRefreshing = false
        }
    }


    private fun initView() {
        initRecyclerView()
        initPopupWindow()
        initButton()
        initSwipeView()
    }

    private fun initSwipeView() {
        binding.swipeLayout.setOnRefreshListener {
            observeListFile()
        }
    }

    private fun initToolBar() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.toolbar_menu_vault, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val menuItemView = requireActivity().findViewById<View>(R.id.option)
                return when (menuItem.itemId) {
                    R.id.add_folder -> {
                        showAddFolderDialog()
                        true
                    }
                    R.id.option -> {
                        showOptionDialog(menuItemView)
                        true
                    }
                    R.id.navigate_calculator -> {
                        navigateToCalculator()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun navigateToCalculator() {
        val intent = Intent(requireActivity(), ActivityCalculator::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun initRecyclerView() {
        adapter = AdapterFolder(requireContext(), onItemPress = {
            when (it.type) {
                Constant.TYPE_PICTURE -> {
                    val action = FragmentVaultDirections.actionFragmentVaultToFragmentPersistent(
                        it.type, getString(R.string.pictures), it.path
                    )
                    navigateToPage(R.id.fragmentVault, action)
                }
                Constant.TYPE_VIDEOS -> {
                    val action = FragmentVaultDirections.actionFragmentVaultToFragmentPersistent(
                        it.type, getString(R.string.videos), it.path
                    )
                    navigateToPage(R.id.fragmentVault, action)
                }
                Constant.TYPE_AUDIOS -> {
                    val action = FragmentVaultDirections.actionFragmentVaultToFragmentPersistent(
                        it.type, getString(R.string.audios), it.path
                    )
                    navigateToPage(R.id.fragmentVault, action)
                }
                Constant.TYPE_FILE -> {
                    val action = FragmentVaultDirections.actionFragmentVaultToFragmentPersistent(
                        it.type, getString(R.string.files), it.path
                    )
                    navigateToPage(R.id.fragmentVault, action)
                }
                else -> {
                    val action = FragmentVaultDirections.actionFragmentVaultToFragmentPersistent(
                        it.type, it.name, it.path
                    )
                    navigateToPage(R.id.fragmentVault, action)
                }
            }
        }, onDeletePress = {
            val dialogConfirm = DialogConfirm(onPositiveClicked = {
                viewModel.deleteFolder(it.path, onSuccess = {
                    viewModel.getListFolderInVault(
                        requireContext(), requireContext().config.privacyFolder
                    )
                    lifecycleScope.launch(Dispatchers.Main) {
                        showSnackBar(
                            getString(R.string.delete_success), SnackBarType.SUCCESS
                        )
                    }
                }, onError = {
                    showSnackBar(
                        getString(R.string.delete_failed), SnackBarType.FAILED
                    )
                })
            }, DialogConfirmType.DELETE, it.name)

            dialogConfirm.show(parentFragmentManager, dialogConfirm.tag)

        }, onRenamePress = {
            openRenameDialog(it.mPath)
        })
        binding.rcvFolder.adapter = adapter
        if (!AdapterFolder.isSwitchView) {
            val gridLayoutManager = GridLayoutManager(requireContext(), 1)
            binding.rcvFolder.layoutManager = gridLayoutManager
        } else {
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            binding.rcvFolder.layoutManager = gridLayoutManager
        }

    }

    private fun openRenameDialog(path: String) {
        val dialogRenameFolder = DialogRenameFolder(object : RenameDialogCallBack {
            override fun onPositiveClicked(name: String) {
                val pattern = Pattern.compile("[a-zA-Z\\d_-]+")
                val matcher = pattern.matcher(name)
                if (!matcher.matches()) {
                    showSnackBar(getString(R.string.require_character), SnackBarType.FAILED)
                    return
                } else {
                    viewModel.renameFolder(
                        File(path),
                        name,
                        onSuccess = {
                            viewModel.getListFolderInVault(
                                requireContext(), requireContext().config.privacyFolder
                            )
                            showSnackBar(getString(R.string.rename_successfully), SnackBarType.SUCCESS)
                        },
                        onError = {
                            showSnackBar(
                                getString(R.string.error_rename),
                                SnackBarType.FAILED
                            )
                        }
                    )
                }

            }

        })
        dialogRenameFolder.show(childFragmentManager, dialogRenameFolder.tag)
    }


    private fun initButton() {

    }

    private fun showOptionDialog(menuItemView: View) {
        Log.d("TAG", "showOptionDialog: ")
        popupWindow.showAsDropDown(menuItemView, 0, 0)
    }

    private fun showAddFolderDialog() {
        val dialogAddNewFolder = DialogAddNewFolder(object : AddNewFolderDialogCallBack {
            override fun onPositiveClicked(name: String) {
                viewModel.createFolder(requireContext().config.privacyFolder, name, onSuccess = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        showSnackBar(
                            getString(R.string.create_success), SnackBarType.SUCCESS
                        )
                    }
                    viewModel.getListFolderInVault(
                        requireContext(), requireContext().config.privacyFolder
                    )
                }, onError = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        showSnackBar(
                            getString(R.string.create_failed), SnackBarType.FAILED
                        )
                    }
                })
            }

        })
        dialogAddNewFolder.show(parentFragmentManager, dialogAddNewFolder.tag)
    }

    private fun initPopupWindow() {
        val inflater: LayoutInflater =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
        val bindingLayout = LayoutMenuOptionBinding.inflate(inflater, null, false)

        popupWindow = PopupWindow(
            bindingLayout.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        bindingLayout.root.clickWithDebounce {
            popupWindow.dismiss()
        }
        bindingLayout.tvSort.clickWithDebounce {
            val dialogSort = DialogSort(callBack = object : SortDialogCallBack {
                override fun onPositiveClicked(mSortType: Sort, mOrder: Order) {
                    sortType = mSortType
                    order = mOrder
                    viewModel.selfListFolderInVaultPostValue()
                }
            }, sortType, order)
            dialogSort.show(parentFragmentManager, dialogSort.tag)
            popupWindow.dismiss()
        }
        if (!AdapterFolder.isSwitchView) {
            bindingLayout.tvList.setText(R.string.grid)
            bindingLayout.tvList.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_grid_layout
                ), null, null, null
            )
        } else {
            bindingLayout.tvList.setText(R.string.list)
            bindingLayout.tvList.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.ic_list
                ), null, null, null
            )
        }

        bindingLayout.tvList.clickWithDebounce {
            AdapterFolder.isSwitchView = !AdapterFolder.isSwitchView
            if (!AdapterFolder.isSwitchView) {
                bindingLayout.tvList.setText(R.string.grid)
                bindingLayout.tvList.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_grid_layout
                    ), null, null, null
                )
            } else {
                bindingLayout.tvList.setText(R.string.list)
                bindingLayout.tvList.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_list
                    ), null, null, null
                )
            }
            changeLayoutRecyclerView()
            popupWindow.dismiss()
        }
    }

    private fun changeLayoutRecyclerView() {
        if (!AdapterFolder.isSwitchView) {
            val gridLayoutManager = GridLayoutManager(requireContext(), 1)
            binding.rcvFolder.layoutManager = gridLayoutManager
        } else {
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            binding.rcvFolder.layoutManager = gridLayoutManager
        }
    }

    private fun sortList(mList: MutableList<VaultFileDirItem>): MutableList<VaultFileDirItem> {
        when (order) {
            Order.ASC -> {
                when (sortType) {
                    Sort.RANDOM -> {
                        mList.shuffle()
                    }
                    Sort.NAME -> {
                        mList.sortBy { it.name }
                    }
                    Sort.SIZE -> {
                        mList.sortBy { it.mChildren }
                    }
                }
            }
            Order.DES -> {
                when (sortType) {
                    Sort.RANDOM -> {
                        mList.shuffle()
                    }
                    Sort.NAME -> {
                        mList.sortByDescending { it.name }
                    }
                    Sort.SIZE -> {
                        mList.sortByDescending { it.mChildren }
                    }
                }
            }
        }
        return mList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}