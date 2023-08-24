package com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.neko.hiepdph.calculatorvault.CustomApplication
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.enums.Action
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.navigateToPage
import com.neko.hiepdph.calculatorvault.common.extensions.shareFile
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.common.extensions.toast
import com.neko.hiepdph.calculatorvault.common.extensions.toastLocation
import com.neko.hiepdph.calculatorvault.common.utils.openWith
import com.neko.hiepdph.calculatorvault.config.Status
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.FragmentPersistentBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogAddFile
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.dialog.DialogDetail
import com.neko.hiepdph.calculatorvault.dialog.DialogProgress
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityImageDetail
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVideoPlayer
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter.AdapterOtherFolderNew
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter.AdapterPersistentNew
import com.neko.hiepdph.calculatorvault.viewmodel.PersistentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FragmentPersistent : Fragment() {
    private lateinit var binding: FragmentPersistentBinding
    private val args: FragmentPersistentArgs by navArgs()
    private val viewModel by viewModels<PersistentViewModel>()
    private var adapterPersistent: AdapterPersistentNew? = null
    private var adapterOtherFolder: AdapterOtherFolderNew? = null
    private var listItemSelected = mutableListOf<FileVaultItem>()
    private var sizeList = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersistentBinding.inflate(inflater, container, false)
        (requireActivity() as ActivityVault).getToolbar().title = args.title
        toastLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    override fun onResume() {
        super.onResume()
        resetAllViewAndData()
        (requireActivity() as ActivityVault).setupActionBar()
    }

    private fun resetAllViewAndData() {
        listItemSelected.clear()
        adapterPersistent?.changeToNormalView()
        adapterOtherFolder?.changeToNormalView()
        initToolBar()
        binding.containerController.hide()
    }


    private fun initView() {
        initTitle()
        initRecyclerView()
        initButton()
        resetAllViewAndData()
    }


    private fun checkAllItem(status: Boolean) {
        if (status) {
            adapterPersistent?.selectAll()
            adapterOtherFolder?.selectAll()
        } else {
            adapterPersistent?.unSelectAll()
            adapterOtherFolder?.unSelectAll()
        }
    }

    private fun initTitle() {
        when (args.type) {
            Constant.TYPE_PICTURE -> {
                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.picture),
                    getString(R.string.picture_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_picture, 0, 0
                )
                binding.tvSlideshow.show()

            }

            Constant.TYPE_AUDIOS -> {
                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.audio),
                    getString(R.string.audio_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_audio, 0, 0
                )
                binding.tvSlideshow.hide()

            }

            Constant.TYPE_VIDEOS -> {
                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.video),
                    getString(R.string.video_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_video, 0, 0
                )
                binding.tvSlideshow.hide()
            }

            Constant.TYPE_FILE -> {
                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.file),
                    getString(R.string.file_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_file, 0, 0
                )
                binding.tvSlideshow.hide()
            }

            else -> {
                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.file),
                    getString(R.string.file_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_file, 0, 0
                )
            }
        }
    }

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (AdapterPersistentNew.editMode || AdapterOtherFolderNew.editMode) {
                    menu.clear()
                    menuInflater.inflate(R.menu.toolbar_menu_persistent, menu)
                    menu[0].actionView?.findViewById<View>(R.id.checkbox)?.setOnClickListener {
                        checkAllItem(menu[0].actionView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked == true)
                    }
                    checkItem()
                } else {
                    menu.clear()
                }

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (AdapterPersistentNew.editMode || AdapterOtherFolderNew.editMode) {
                    when (menuItem.itemId) {
                        android.R.id.home -> {
                            adapterPersistent?.changeToNormalView()
                            adapterOtherFolder?.changeToNormalView()
                            binding.containerController.hide()
                            (requireActivity() as ActivityVault).setupActionBar()
                            initToolBar()
                            true
                        }

                        else -> false
                    }
                } else {
                    false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun checkItem() {
        val checkbox =
            (requireActivity() as ActivityVault).getToolbar().menu[0].actionView?.findViewById<CheckBox>(
                R.id.checkbox
            )
        checkbox?.isChecked = listItemSelected.size == sizeList && sizeList > 0
    }

    private fun getDataFile() {
        viewModel.getAllFileFromFolderEncrypted(args.vaultPath).observe(viewLifecycleOwner) {
            it?.let {
                if (args.type != Constant.TYPE_ADD_MORE) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapterPersistent?.submitList(it)
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapterOtherFolder?.submitList(it)
                    }
                }
                sizeList = it.size
                binding.loading.hide()
                if (it.isNotEmpty()) {
                    binding.tvEmpty.hide()
                } else {
                    binding.tvEmpty.show()
                }
            }
        }
    }


    private fun initData() {
        getDataFile()

    }

    private fun initButton() {
        binding.floatingActionButton.setOnClickListener {
            val name = when (args.type) {
                Constant.TYPE_PICTURE -> getString(R.string.library)
                Constant.TYPE_AUDIOS -> getString(R.string.audios_album)
                Constant.TYPE_VIDEOS -> getString(R.string.library)
                Constant.TYPE_FILE -> getString(R.string.files)
                else -> args.title
            }

            if (args.type == Constant.TYPE_ADD_MORE) {
                val dialogFloatingButton = DialogAddFile(onClickPicture = {
                    val action =
                        FragmentPersistentDirections.actionFragmentPersistentToFragmentAddFile(
                            Constant.TYPE_PICTURE, getString(R.string.library), args.vaultPath
                        )
                    navigateToPage(R.id.fragmentPersistent, action)
                }, onClickAudio = {
                    val action =
                        FragmentPersistentDirections.actionFragmentPersistentToFragmentAddFile(
                            Constant.TYPE_AUDIOS, getString(R.string.audios_album), args.vaultPath
                        )
                    navigateToPage(R.id.fragmentPersistent, action)
                }, onClickVideo = {
                    val action =
                        FragmentPersistentDirections.actionFragmentPersistentToFragmentAddFile(
                            Constant.TYPE_VIDEOS, getString(R.string.library), args.vaultPath
                        )
                    navigateToPage(R.id.fragmentPersistent, action)
                }, onClickFile = {
                    val action =
                        FragmentPersistentDirections.actionFragmentPersistentToFragmentAddFile(
                            Constant.TYPE_FILE, getString(R.string.files), args.vaultPath
                        )
                    navigateToPage(R.id.fragmentPersistent, action)
                })
                dialogFloatingButton.show(childFragmentManager, dialogFloatingButton.tag)
            } else {
                val action = FragmentPersistentDirections.actionFragmentPersistentToFragmentAddFile(
                    args.type, name, args.vaultPath
                )
                navigateToPage(R.id.fragmentPersistent, action)
            }
        }

        binding.tvUnlock.clickWithDebounce {
            if (listItemSelected.isNotEmpty()) {
                showDialogUnlock()
            } else {
                toast(getString(R.string.require_size_more_than_1))
            }
        }
        binding.tvSlideshow.clickWithDebounce {
            slideShow()
        }
        binding.tvShare.clickWithDebounce {
            share()
        }
        binding.tvDelete.clickWithDebounce {
            showDialogDelete()
        }

    }

    private fun showDialogDelete(item: FileVaultItem) {

        val confirmDialog = DialogConfirm(onPositiveClicked = {

            val dialogProgress = DialogProgress(listItemSelected = mutableListOf(item),
                listOfSourceFile = mutableListOf(File(item.encryptedPath)),
                listOfTargetParentFolder = mutableListOf(requireActivity().config.recyclerBinFolder),
                action = Action.DELETE,
                onResult = { status, text, _ ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        normalView()
                        if (status == Status.SUCCESS) {
                            showSnackBar(text, SnackBarType.SUCCESS)
                        }
                        if (status == Status.FAILED) {
                            showSnackBar(text, SnackBarType.FAILED)
                        }

                        if (status == Status.WARNING) {
                            showSnackBar(text, SnackBarType.WARNING)
                        }
                    }

                })



            dialogProgress.show(childFragmentManager, dialogProgress.tag)
        }, DialogConfirmType.DELETE, item.name)

        confirmDialog.show(childFragmentManager, confirmDialog.tag)
    }

    private fun showDialogDelete() {
        if (listItemSelected.isEmpty()) {
            Toast.makeText(
                requireContext(), getString(R.string.require_size_more_than_1), Toast.LENGTH_SHORT
            ).show()
            return
        }
        Log.d("TAG", "showDialogDelete: " + listItemSelected.size)
        val name = when (args.type) {
            Constant.TYPE_PICTURE -> getString(R.string.pictures)
            Constant.TYPE_VIDEOS -> getString(R.string.videos)
            Constant.TYPE_FILE -> getString(R.string.files)
            Constant.TYPE_AUDIOS -> getString(R.string.audios)
            else -> getString(R.string.files)
        }
        val confirmDialog = DialogConfirm(
            onPositiveClicked = {
                val dialogProgress = DialogProgress(listItemSelected = listItemSelected,
                    listOfSourceFile = listItemSelected.map { File(it.encryptedPath) },
                    listOfTargetParentFolder = listItemSelected.map { requireContext().config.recyclerBinFolder },
                    action = Action.DELETE,
                    onResult = { status, text, _ ->
                        lifecycleScope.launch(Dispatchers.Main) {
                            normalView()
                            when (status) {
                                Status.SUCCESS -> {
                                    showSnackBar(text, SnackBarType.SUCCESS)
                                }

                                Status.FAILED -> {
                                    showSnackBar(text, SnackBarType.FAILED)
                                }

                                Status.WARNING -> {
                                    showSnackBar(text, SnackBarType.WARNING)
                                }
                            }
                        }

                    })

                dialogProgress.show(childFragmentManager, dialogProgress.tag)
            },
            dialogType = if (!requireContext().config.moveToRecyclerBin) DialogConfirmType.DELETE_PERMANENT else DialogConfirmType.DELETE,
            name
        )

        confirmDialog.show(childFragmentManager, confirmDialog.tag)
    }

    private fun share() {
        val dialogProgress = DialogProgress(listItemSelected,
            listItemSelected.map { File(it.encryptedPath) }.toMutableList(),
            listItemSelected.map { requireContext().config.decryptFolder }.toMutableList(),
            action = Action.DECRYPT,
            onResult = { status, text, valueReturn ->
                lifecycleScope.launch(Dispatchers.Main) {
                    if (status == Status.SUCCESS) {
                        valueReturn?.let {
                            requireContext().shareFile(it.map { path -> path })
                        }
                    }
                    if (status == Status.FAILED) {
                        requireActivity().showSnackBar(text, SnackBarType.FAILED)
                    }

                    if (status == Status.WARNING) {
                        requireActivity().showSnackBar(text, SnackBarType.FAILED)
                    }
                }

            })

        dialogProgress.show(childFragmentManager, dialogProgress.tag)


    }


    private fun slideShow() {
        if (listItemSelected.isEmpty()) {
            toast(getString(R.string.require_size_more_than_1))
        } else {
            val list = mutableListOf<FileVaultItem>()
            val dialogProgress = DialogProgress(
                listItemSelected = listItemSelected,
                listOfSourceFile = listItemSelected.map { File(it.encryptedPath) },
                listOfTargetParentFolder = listItemSelected.map { requireActivity().config.decryptFolder },
                onResult = { status, text, valueReturn ->
                    when (status) {
                        Status.SUCCESS -> {

                            lifecycleScope.launch(Dispatchers.Main) {
                                showSnackBar(text, SnackBarType.SUCCESS)
                                if (!valueReturn.isNullOrEmpty()) {
                                    listItemSelected.map { item ->
                                        item.decodePath = valueReturn[0]
                                        viewModel.updateFileVault(item)
                                        list.add(item)
                                    }
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
            )
            dialogProgress.show(childFragmentManager, dialogProgress.tag)
        }

    }

    private fun showDialogUnlock() {
        val name = when (args.type) {
            Constant.TYPE_PICTURE -> getString(R.string.pictures)
            Constant.TYPE_VIDEOS -> getString(R.string.videos)
            Constant.TYPE_FILE -> getString(R.string.files)
            Constant.TYPE_AUDIOS -> getString(R.string.audios)
            else -> getString(R.string.files)
        }
        val confirmDialog = DialogConfirm(onPositiveClicked = {
            unLockFile()
        }, DialogConfirmType.UNLOCK, name)

        confirmDialog.show(childFragmentManager, confirmDialog.tag)
    }

    private fun unLockFile() {
        val dialogProgress = DialogProgress(listItemSelected,
            listItemSelected.map { File(it.encryptedPath) },
            listItemSelected.map { File(it.originalPath).parentFile } as List<File>,
            Action.UNLOCK,
            onResult = { status, text, _ ->
                lifecycleScope.launch(Dispatchers.Main) {
                    normalView()
                    when (status) {
                        Status.SUCCESS -> {
                            showSnackBar(
                                String.format(text), SnackBarType.SUCCESS
                            )
                        }

                        Status.FAILED -> {
                            showSnackBar((text), SnackBarType.FAILED)
                        }

                        Status.WARNING -> {
                            showSnackBar((text), SnackBarType.WARNING)
                        }
                    }
                }

            })


        dialogProgress.show(childFragmentManager, dialogProgress.tag)

    }

    private fun normalView() {
        listItemSelected.clear()
        adapterPersistent?.unSelectAll()
        adapterOtherFolder?.unSelectAll()
        adapterPersistent?.changeToNormalView()
        adapterOtherFolder?.changeToNormalView()
        initToolBar()
        (requireActivity() as ActivityVault).setupActionBar()
        binding.containerController.hide()
    }


    private fun editHome() {
        val actionBar = (requireActivity() as? ActivityVault)?.supportActionBar
        val exitIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_exit)
        exitIcon?.let {
            actionBar?.setHomeAsUpIndicator(it)
        }

    }


    private fun initRecyclerView() {
        if (args.type != Constant.TYPE_ADD_MORE) {
            adapterPersistent = AdapterPersistentNew(args.type, onClickItem = {
                handleClickItem(it)
            }, onLongClickItem = {
                listItemSelected.clear()
                listItemSelected.addAll(it)
                initToolBar()
                editHome()
                showController()

            }, onSelectAll = {
                listItemSelected.clear()
                listItemSelected.addAll(it)
            }, onEditItem = {
                listItemSelected.clear()
                listItemSelected.addAll(it)
                checkItem()
            }, onDeleteItem = {
                showDialogDelete(it)
            }, onOpenDetail = {
                openInformationDialog(it)
            })
            binding.rcvItemGroup.adapter = adapterPersistent

            val gridLayoutManager = when (args.type) {
                Constant.TYPE_PICTURE, Constant.TYPE_VIDEOS -> GridLayoutManager(
                    requireContext(), 4, RecyclerView.VERTICAL, false
                )

                Constant.TYPE_AUDIOS, Constant.TYPE_FILE -> LinearLayoutManager(
                    requireContext(), RecyclerView.VERTICAL, false
                )

                else -> LinearLayoutManager(
                    requireContext(), RecyclerView.VERTICAL, false
                )

            }
            binding.rcvItemGroup.layoutManager = gridLayoutManager
        } else {
            adapterOtherFolder = AdapterOtherFolderNew(onClickItem = {
                handleClickItem(it)
            }, onLongClickItem = {
                listItemSelected.clear()
                listItemSelected.addAll(it)
                initToolBar()
                editHome()
                showController()

            }, onSelectAll = {
                listItemSelected.clear()
                listItemSelected.addAll(it)
            }, onEditItem = {
                listItemSelected.clear()
                listItemSelected.addAll(it)
                checkItem()
            }, onDeleteItem = {
                showDialogDelete(it)
            }, onOpenDetail = {
                openInformationDialog(it)
            })
            binding.rcvItemGroup.adapter = adapterOtherFolder
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.rcvItemGroup.layoutManager = layoutManager
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
                CustomApplication.app.resumeFromApp = true
                ((requireActivity() as ActivityVault).application as CustomApplication).authority
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
                        CustomApplication.app.resumeFromApp = true

                        item.decodePath.openWith(
                            requireContext(), Constant.TYPE_VIDEOS, null
                        )
                    }
                } else {
                    CustomApplication.app.resumeFromApp = true

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
                CustomApplication.app.resumeFromApp = true
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


    private fun showController() {
        when (args.type) {
            Constant.TYPE_PICTURE -> {
                binding.tvSlideshow.show()

            }

            Constant.TYPE_VIDEOS -> {
                binding.tvSlideshow.hide()
            }
        }
        binding.containerController.show()
    }

    private fun openInformationDialog(item: FileVaultItem) {
        val dialogDetail = DialogDetail(requireContext(), item).onCreateDialog()
        dialogDetail.show()
    }


    override fun onDestroy() {
        AdapterPersistentNew.editMode = false
        AdapterOtherFolderNew.editMode = false
        super.onDestroy()
//        _binding = null
    }


}