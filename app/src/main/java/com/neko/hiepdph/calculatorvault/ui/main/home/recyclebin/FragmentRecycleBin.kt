package com.neko.hiepdph.calculatorvault.ui.main.home.recyclebin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.enums.Action
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.utils.openWith
import com.neko.hiepdph.calculatorvault.config.Status
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.FragmentRecycleBinBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.dialog.DialogDetail
import com.neko.hiepdph.calculatorvault.dialog.DialogProgress
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityImageDetail
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVideoPlayer
import com.neko.hiepdph.calculatorvault.ui.main.home.recyclebin.adapter.AdapterRecyclerBin
import com.neko.hiepdph.calculatorvault.viewmodel.RecyclerBinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FragmentRecycleBin : Fragment() {
    private var _binding: FragmentRecycleBinBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RecyclerBinViewModel>()

    private var adapterRecycleBin: AdapterRecyclerBin? = null
    private var listItemSelected: MutableList<FileVaultItem> = mutableListOf()
    private var sizeList = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecycleBinBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
//        initToolBar()
        getData()
        changeBackPressCallBack {
            requireActivity().finishAffinity()
        }
    }

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (AdapterRecyclerBin.editMode) {
                    menu.clear()
                    menuInflater.inflate(R.menu.toolbar_menu_recycler_bin_edit_mode, menu)
                    menu[2].actionView?.findViewById<View>(R.id.checkbox)?.setOnClickListener {
                        checkAllItem(menu[2].actionView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked == true)
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
                            checkItem()
                            initToolBar()
                            true
                        }

                        R.id.restore_item -> {
                            restore()
                            true
                        }

                        R.id.delete_item -> {
                            deletePermanent()
                            true
                        }

                        else -> false
                    }
                } else {
                    when (menuItem.itemId) {
                        R.id.edit_item -> {
                            changeToEditView()
                            true
                        }

                        R.id.delete_all_item -> {
                            deleteAll()
                            true
                        }

                        else -> false
                    }
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun changeToEditView() {
        if (sizeList == 0) {
            toast(getString(R.string.require_size_more_than_1))
            return
        }
        adapterRecycleBin?.changeToEditView()
        initToolBar()
        editHome()
    }

    private fun getData() {

        viewModel.getAllFileChildFromFolder(requireContext().config.recyclerBinFolder.path)
            .observe(viewLifecycleOwner) {
                it?.let {
                    adapterRecycleBin?.submitList(it)
                    sizeList = it.size
                    if (it.isEmpty()) {
                        binding.tvEmpty.show()
                    } else {
                        binding.tvEmpty.hide()
                    }
                    adapterRecycleBin?.changeToNormalView()
                    initToolBar()
                    (requireActivity() as ActivityVault).setupActionBar()
                }
            }
    }

    private fun checkItem() {
        val checkbox =
            (requireActivity() as ActivityVault).getToolbar().menu[2].actionView?.findViewById<CheckBox>(
                R.id.checkbox
            )
        checkbox?.isChecked = listItemSelected.size == sizeList && sizeList > 0

    }

    private fun checkAllItem(status: Boolean) {
        if (status) {
            adapterRecycleBin?.selectAll()

        } else {
            adapterRecycleBin?.unSelectAll()

        }
    }

    private fun initView() {
        binding.tvEmpty.text = getString(R.string.no_files)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapterRecycleBin = AdapterRecyclerBin(onClickItem = {
            handleClickItem(it)
        }, onLongClickItem = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
            initToolBar()
            editHome()

        }, onSelectAll = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
        }, onEditItem = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
            checkItem()
        }, onDeleteItem = {
            deletePermanent(it)
        }, onDetailItem = {
            openInformationDialog(it)
        }, onRestoreItem = { restore(it) })

        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rcvItems.layoutManager = layoutManager

        binding.rcvItems.adapter = adapterRecycleBin
    }

    private fun openInformationDialog(item: FileVaultItem) {
        Log.d("TAG", "openInformationDialog: ")
        val dialogDetail = DialogDetail(requireContext(), item).onCreateDialog()
        dialogDetail.show()
    }

    private fun editHome() {
        val actionBar = (requireActivity() as? ActivityVault)?.supportActionBar
        val exitIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_exit)
        exitIcon?.let {
            actionBar?.setHomeAsUpIndicator(it)
        }
    }

    private fun handleClickItem(item: FileVaultItem) {
        val list = mutableListOf<FileVaultItem>()
        when (item.fileType) {
            Constant.TYPE_PICTURE -> {
                if (File(item.decodePath).exists()) {
                    list.add(item)
                    ShareData.getInstance().setListItemImage(list)
                    val intent = Intent(requireContext(), ActivityImageDetail::class.java)
                    startActivity(intent)

                } else {
                    val dialogProgress = DialogProgress(
                        listItemSelected = mutableListOf(item),
                        listOfSourceFile = mutableListOf(File(item.encryptedPath)),
                        listOfTargetParentFolder = mutableListOf(requireActivity().config.decryptFolder),
                        onResult = { status, text, valueReturn ->
                            when (status) {
                                Status.SUCCESS -> {
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        showSnackBar(text, SnackBarType.SUCCESS)
                                        if (!valueReturn.isNullOrEmpty()) {
                                            item.decodePath = valueReturn[0]
                                            viewModel.updateFileVault(item)
                                            list.add(item)
                                            ShareData.getInstance().setListItemImage(list)
                                            val intent = Intent(
                                                requireContext(), ActivityImageDetail::class.java
                                            )
                                            startActivity(intent)
                                        }

                                    }

                                }

                                Status.FAILED -> {
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        showSnackBar(text, SnackBarType.FAILED)
                                    }
                                }
                            }
                        },
                        action = Action.DECRYPT,
                        encryptionMode = item.encryptionType

                    )
                    dialogProgress.show(childFragmentManager, dialogProgress.tag)
                }
            }

            Constant.TYPE_AUDIOS -> {
                if (File(item.decodePath).exists()) {
                    item.decodePath.openWith(requireContext(), Constant.TYPE_AUDIOS, null)
                } else {
                    val dialogProgress = DialogProgress(
                        listItemSelected = mutableListOf(item),
                        listOfSourceFile = mutableListOf(File(item.encryptedPath)),
                        listOfTargetParentFolder = mutableListOf(requireActivity().config.decryptFolder),
                        onResult = { status, text, valuesReturn ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                when (status) {
                                    Status.SUCCESS -> {
                                        if (!valuesReturn.isNullOrEmpty()) {
                                            item.decodePath = valuesReturn[0]
                                            viewModel.updateFileVault(item)
                                            item.decodePath.openWith(
                                                requireContext(), Constant.TYPE_AUDIOS, null
                                            )
                                        }

                                    }

                                    Status.FAILED -> {
                                        showSnackBar(text, SnackBarType.FAILED)
                                    }
                                }
                            }
                        },
                        action = Action.DECRYPT,
                        encryptionMode = item.encryptionType

                    )
                    dialogProgress.show(childFragmentManager, dialogProgress.tag)
                }
            }

            Constant.TYPE_VIDEOS -> {
                if (File(item.decodePath).exists()) {
                    if (requireContext().config.playVideoMode) {
                        ShareData.getInstance().setListItemVideo(mutableListOf(item))
                        val intent = Intent(requireContext(), ActivityVideoPlayer::class.java)
                        startActivity(intent)
                    } else {
                        item.decodePath.openWith(
                            requireContext(), Constant.TYPE_VIDEOS, null
                        )
                    }
                } else {
                    val dialogProgress = DialogProgress(
                        listItemSelected = mutableListOf(item),
                        listOfSourceFile = mutableListOf(File(item.encryptedPath)),
                        listOfTargetParentFolder = mutableListOf(requireActivity().config.decryptFolder),
                        onResult = { status, text, valuesReturn ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                when (status) {
                                    Status.SUCCESS -> {
                                        if (!valuesReturn.isNullOrEmpty()) {
                                            item.decodePath = valuesReturn[0]
                                            viewModel.updateFileVault(item)
                                            list.add(item)
                                            if (requireContext().config.playVideoMode) {
                                                ShareData.getInstance().setListItemVideo(list)
                                                val intent = Intent(
                                                    requireContext(),
                                                    ActivityVideoPlayer::class.java
                                                )
                                                startActivity(intent)
                                            } else {
                                                item.decodePath.openWith(
                                                    requireContext(), Constant.TYPE_VIDEOS, null
                                                )
                                            }
                                        }

                                    }

                                    Status.FAILED -> {
                                        showSnackBar(text, SnackBarType.FAILED)
                                    }
                                }
                            }
                        },
                        action = Action.DECRYPT,
                        encryptionMode = item.encryptionType

                    )
                    dialogProgress.show(childFragmentManager, dialogProgress.tag)
                }


            }

            else -> {
                if (File(item.decodePath).exists()) {
                    item.decodePath.openWith(requireContext(), Constant.TYPE_FILE, null)
                } else {
                    val dialogProgress = DialogProgress(
                        listItemSelected = mutableListOf(item),
                        listOfSourceFile = mutableListOf(File(item.encryptedPath)),
                        listOfTargetParentFolder = mutableListOf(requireActivity().config.decryptFolder),
                        onResult = { status, text, valuesReturn ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                when (status) {
                                    Status.SUCCESS -> {
                                        if (!valuesReturn.isNullOrEmpty()) {
                                            item.decodePath = valuesReturn[0]
                                            item.decodePath.openWith(
                                                requireContext(), Constant.TYPE_FILE, null
                                            )
                                        }

                                    }

                                    Status.FAILED -> {
                                        showSnackBar(text, SnackBarType.FAILED)
                                    }
                                }
                            }
                        },
                        action = Action.DECRYPT,
                        encryptionMode = item.encryptionType
                    )
                    dialogProgress.show(childFragmentManager, dialogProgress.tag)
                }


            }
        }
    }

    private fun restore(vaultItem: FileVaultItem) {
        Log.d("TAG", "recyclerPath: " + vaultItem.recyclerPath)
        Log.d("TAG", "encryptedPath: " + vaultItem.encryptedPath)
        Log.d("TAG", "originalPath: " + vaultItem.originalPath)
        val dialogConfirm = DialogConfirm(onPositiveClicked = {
            val dialogProgress = DialogProgress(listItemSelected = mutableListOf(vaultItem),
                mutableListOf(File(vaultItem.recyclerPath)),
                mutableListOf(File(vaultItem.encryptedPath).parentFile),
                action = Action.RESTORE,
                onResult = { status, text, _ ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        when (status) {
                            Status.SUCCESS -> {
                                showSnackBar(
                                    text, SnackBarType.SUCCESS
                                )
                            }

                            Status.FAILED -> {
                                showSnackBar(
                                    text, SnackBarType.FAILED
                                )
                            }
                        }
                    }
                })

            dialogProgress.show(childFragmentManager, dialogProgress.tag)
        }, DialogConfirmType.RESTORE, null)


        dialogConfirm.show(childFragmentManager, dialogConfirm.tag)
    }

    private fun restore() {
        if (listItemSelected.isEmpty()) {
            toast(getString(R.string.require_size_more_than_1))
            return
        }

        val dialogConfirm = DialogConfirm(onPositiveClicked = {
            val dialogProgress = DialogProgress(listItemSelected = listItemSelected,
                listOfSourceFile = listItemSelected.map { File(it.recyclerPath) },
                listOfTargetParentFolder = listItemSelected.map { File(it.recyclerPath).parentFile },
                action = Action.RESTORE,
                onResult = { status, text, _ ->
                    when (status) {
                        Status.SUCCESS -> {
                            showSnackBar(
                                text, SnackBarType.SUCCESS
                            )
                        }

                        Status.WARNING -> {
                            showSnackBar(
                                text, SnackBarType.SUCCESS
                            )
                        }

                        Status.FAILED -> {
                            showSnackBar(
                                text, SnackBarType.FAILED
                            )
                        }
                    }
                })
            dialogProgress.show(childFragmentManager, dialogProgress.tag)

        }, DialogConfirmType.RESTORE, null)

        dialogConfirm.show(childFragmentManager, dialogConfirm.tag)
    }

    private fun deletePermanent(item: FileVaultItem) {
        val dialogConfirm = DialogConfirm(onPositiveClicked = {
            val dialogProgress = DialogProgress(listItemSelected = mutableListOf(item),
                action = Action.DELETE_PERMANENT,
                onResult = { status, text, valuesReturn ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        when (status) {
                            Status.SUCCESS -> {
                                showSnackBar(text, SnackBarType.SUCCESS)
                            }

                            Status.FAILED -> {
                                showSnackBar(text, SnackBarType.FAILED)
                            }
                        }
                    }

                })
            dialogProgress.show(childFragmentManager, dialogProgress.tag)
        }, DialogConfirmType.DELETE, getString(R.string.selected_file))

        dialogConfirm.show(childFragmentManager, dialogConfirm.tag)
    }

    private fun deletePermanent() {
        if (listItemSelected.isEmpty()) {
            toast(getString(R.string.require_size_more_than_1))
            return
        }
        val dialogConfirm = DialogConfirm(onPositiveClicked = {
            Log.d("TAG", "deletePermanent: " + listItemSelected.size)
            val dialogProgress = DialogProgress(listItemSelected = listItemSelected,
                action = Action.DELETE_PERMANENT,
                onResult = { status, text, valuesReturn ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        when (status) {
                            Status.SUCCESS -> {
                                showSnackBar(text, SnackBarType.SUCCESS)
                            }

                            Status.FAILED -> {
                                showSnackBar(text, SnackBarType.FAILED)
                            }
                        }
                    }

                })

            dialogProgress.show(childFragmentManager, dialogProgress.tag)
        }, DialogConfirmType.DELETE, getString(R.string.selected_file))

        dialogConfirm.show(childFragmentManager, dialogConfirm.tag)
    }

    private fun deleteAll() {
        if (sizeList == 0) {
            toast(getString(R.string.nothing_to_delete))
            return
        }
        val dialogConfirm = DialogConfirm(onPositiveClicked = {
            adapterRecycleBin?.selectAll()
            val dialogProgress = DialogProgress(listItemSelected = listItemSelected,
                action = Action.DELETE_All_RECYCLER_BIN_PERMANENT,
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
                    }
                })

            dialogProgress.show(childFragmentManager, dialogProgress.tag)

        }, DialogConfirmType.EMPTY_BIN, null)

        dialogConfirm.show(childFragmentManager, dialogConfirm.tag)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}