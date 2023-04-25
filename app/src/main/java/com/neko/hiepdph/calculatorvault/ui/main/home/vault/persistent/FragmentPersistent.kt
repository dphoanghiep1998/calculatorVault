package com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.data.model.ItemMoved
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import com.neko.hiepdph.calculatorvault.databinding.FragmentPersistentBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogAddFile
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.sharedata.ShareData
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityImageDetail
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter.AdapterOtherFolder
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter.AdapterPersistent
import com.neko.hiepdph.calculatorvault.viewmodel.PersistentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FragmentPersistent : Fragment() {
    private lateinit var binding: FragmentPersistentBinding
    private val args: FragmentPersistentArgs by navArgs()
    private val viewModel by viewModels<PersistentViewModel>()
    private var adapterPersistent: AdapterPersistent? = null
    private var adapterOtherFolder: AdapterOtherFolder? = null
    private var listItemSelected = mutableListOf<ListItem>()
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
    }

    override fun onResume() {
        super.onResume()
        initData()
        resetAllViewAndData()
        (requireActivity() as ActivityVault).setupActionBar()

    }

    private fun resetAllViewAndData() {
        listItemSelected.clear()
        adapterPersistent?.changeToNormalView()
        adapterOtherFolder?.changeToNormalView()
        initToolBar()
    }


    private fun initView() {
        initRecyclerView()
        initButton()
        initData()
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

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                if (AdapterPersistent.editMode || AdapterOtherFolder.editMode) {
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
                return if (AdapterPersistent.editMode || AdapterOtherFolder.editMode) {
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

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun checkItem() {
        val checkbox =
            (requireActivity() as ActivityVault).getToolbar().menu[0].actionView?.findViewById<CheckBox>(
                R.id.checkbox
            )
        checkbox?.isChecked = listItemSelected.size == sizeList && sizeList > 0


    }

    private fun getDataFile() {
        when (args.type) {
            Constant.TYPE_PICTURE -> {
                viewModel.getImageChildFromFolder(args.vaultPath)

                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.picture),
                    getString(R.string.picture_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_picture, 0, 0
                )
            }
            Constant.TYPE_AUDIOS -> {
                viewModel.getAudioChildFromFolder(args.vaultPath)

                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.audio),
                    getString(R.string.audio_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_audio, 0, 0
                )

            }
            Constant.TYPE_VIDEOS -> {
                viewModel.getVideoChildFromFolder(args.vaultPath)
                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.video),
                    getString(R.string.video_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_video, 0, 0
                )

            }
            Constant.TYPE_FILE -> {
                viewModel.getFileChildFromFolder(args.vaultPath)
                binding.tvEmpty.text = String.format(
                    getString(R.string.empty_text),
                    getString(R.string.file),
                    getString(R.string.file_lower)
                )
                binding.tvEmpty.setCompoundDrawablesWithIntrinsicBounds(
                    0, R.drawable.ic_empty_file, 0, 0
                )

            }
            else -> {
                viewModel.getFileChildFromFolder(args.vaultPath)
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

    private fun initData() {
        getDataFile()
        viewModel.listItemListPersistent.observe(viewLifecycleOwner) {
            it?.let {
                if (args.type != Constant.TYPE_ADD_MORE) {
                    adapterPersistent?.setData(it, args.type)
                } else {
                    adapterOtherFolder?.setData(it)
                }
                sizeList = it.size
                binding.loading.hide()
                if (it.isNotEmpty()) {
                    binding.tvEmpty.hide()
                } else {
                    adapterPersistent?.changeToNormalView()
                    adapterOtherFolder?.changeToNormalView()
                    initToolBar()
                    (requireActivity() as ActivityVault).setupActionBar()
                    binding.containerController.hide()
                    binding.tvEmpty.show()
                }
            }
        }
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

    private fun showDialogDelete() {
        if (listItemSelected.isEmpty()) {
            Toast.makeText(
                requireContext(), getString(R.string.select_no_more_than_one), Toast.LENGTH_SHORT
            ).show()
            return
        }
        val name = when (args.type) {
            Constant.TYPE_PICTURE -> getString(R.string.pictures)
            Constant.TYPE_VIDEOS -> getString(R.string.videos)
            Constant.TYPE_FILE -> getString(R.string.files)
            Constant.TYPE_AUDIOS -> getString(R.string.audios)
            else -> getString(R.string.files)
        }
        val confirmDialog = DialogConfirm(onPositiveClicked = {

            if (requireContext().config.moveToRecyclerBin) {
                val listPathRecyclerBin =
                    requireContext().config.listPathRecyclerBin?.toMutableList() ?: mutableListOf()

                listPathRecyclerBin.addAll(listItemSelected.map {
                    ItemMoved(
                        args.vaultPath,
                        requireContext().config.recyclerBinFolder.path + "/${it.name}"
                    )
                })
                requireContext().config.listPathRecyclerBin = listPathRecyclerBin

                CopyFiles.copy(requireContext(),
                    listItemSelected.map { File(it.mPath) }.toMutableList(),
                    requireContext().config.recyclerBinFolder,
                    0L,
                    progress = { _: Int, _: Float, _: File? -> },
                    true,
                    onSuccess = {
                        getDataFile()
                        showSnackBar(
                            getString(R.string.move_to_recycler_bin), SnackBarType.SUCCESS
                        )
                    },

                    onError = {})
            } else {
                viewModel.deleteMultipleFolder(listItemSelected.map { it.mPath }, onSuccess = {
                    getDataFile()
                    listItemSelected.clear()
                    showSnackBar(getString(R.string.delete_success), SnackBarType.SUCCESS)
                }, onError = {
                    showSnackBar(getString(R.string.delete_success), SnackBarType.SUCCESS)
                })
            }
        }, DialogConfirmType.DELETE, name)

        confirmDialog.show(childFragmentManager, confirmDialog.tag)
    }

    private fun share() {
        requireContext().shareFile(listItemSelected.map { it.mPath })
    }

    private fun slideShow() {
        if (listItemSelected.size <= 1) {
            toast(getString(R.string.require_size_more_than_1))
        } else {
            ShareData.getInstance().setListItemImage(listItemSelected)
            val intent = Intent(requireContext(), ActivityImageDetail::class.java)
            startActivity(intent)
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
        lifecycleScope.launch {
            val listFilterItem = requireContext().config.listItemVault?.filter {
                it.path in listItemSelected.map { item -> item.path }
            } ?: mutableListOf()
            CopyFiles.copy(requireContext(),
                listItemSelected.map { File(it.path) }.toMutableList(),
                listFilterItem.map { File(it.originalPath) }.toMutableList(),
                0L,
                progress = { _: Int, _: Float, _: File? -> },
                true,
                onSuccess = {
                    listItemSelected.clear()
                    adapterPersistent?.unSelectAll()
                    adapterOtherFolder?.unSelectAll()
                    getDataFile()
                },
                onError = {
                    Log.d("TAG", "unLockFile: " + it.printStackTrace())
                })
        }
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

            adapterPersistent = AdapterPersistent(onClickItem = {
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
            }, onUnSelect = {
//            unCheckItem()
            }, onEditItem = {
                listItemSelected.clear()
                listItemSelected.addAll(it)
                checkItem()
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
            adapterOtherFolder = AdapterOtherFolder(onClickItem = {
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
            }, onUnSelect = {
//            unCheckItem()
            }, onEditItem = {
                listItemSelected.clear()
                listItemSelected.addAll(it)
                checkItem()
            })
            binding.rcvItemGroup.adapter = adapterOtherFolder
            val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.rcvItemGroup.layoutManager = layoutManager
        }
    }

    private fun handleClickItem(item: ListItem) {
        when (args.type) {
            Constant.TYPE_PICTURE -> {
                val list = mutableListOf<ListItem>()
                list.add(item)
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

    private fun showController() {
        binding.containerController.show()
    }


    override fun onDestroy() {
        AdapterPersistent.editMode = false
        AdapterOtherFolder.editMode = false
        super.onDestroy()
//        _binding = null
    }


}