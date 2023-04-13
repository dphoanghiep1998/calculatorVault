package com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.*
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import com.neko.hiepdph.calculatorvault.databinding.FragmentPersistentBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogAddFile
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter.AdapterPersistent
import com.neko.hiepdph.calculatorvault.viewmodel.PersistentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentPersistent : Fragment() {
    private var _binding: FragmentPersistentBinding? = null
    private val binding get() = _binding!!
    private val args: FragmentPersistentArgs by navArgs()
    private val viewModel by viewModels<PersistentViewModel>()
    private var adapterPersistent: AdapterPersistent? = null
    private var listItemSelected = mutableListOf<ListItem>()
    private var sizeList = 0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("TAG", "onAttach: "+context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersistentBinding.inflate(inflater, container, false)
        (requireActivity() as ActivityVault).getToolbar().title = args.title
        toastLocation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initToolBar()
    }




    private fun initView() {
        initRecyclerView()
        initData()
        initButton()
    }

    private fun checkAllItem(status: Boolean) {
        if (status) {
            adapterPersistent?.selectAll()
        } else {
            adapterPersistent?.unSelectAll()
        }
    }

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (AdapterPersistent.editMode) {
                    menu.clear()
                    menuInflater.inflate(R.menu.toolbar_menu_persistent, menu)
                    menu[0].actionView?.findViewById<View>(R.id.checkbox)?.setOnClickListener {
                        checkAllItem(menu[0].actionView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked == true)
                    }
                } else {
                    menu.clear()
                }

            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (AdapterPersistent.editMode) {
                    when (menuItem.itemId) {
                        android.R.id.home -> {
                            adapterPersistent?.changeToNormalView()
                            binding.containerController.hide()
                            normalHome()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }

            }

        })
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
            Constant.TYPE_PICTURE -> viewModel.getImageChildFromFolder(
                args.vaultPath
            )
            Constant.TYPE_AUDIOS -> viewModel.getAudioChildFromFolder(
                args.vaultPath
            )
            Constant.TYPE_VIDEOS -> viewModel.getVideoChildFromFolder(
                args.vaultPath
            )
            Constant.TYPE_FILE -> viewModel.getFileChildFromFolder(args.vaultPath)
            else -> viewModel.getFileChildFromFolder(args.vaultPath)
        }
    }

    private fun initData() {
        getDataFile()

        viewModel.listItemListPersistent.observe(viewLifecycleOwner) {
            it?.let {
                adapterPersistent?.setData(it, args.type)
                sizeList = it.size
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

        }
        binding.tvSlideshow.clickWithDebounce {

        }
        binding.tvShare.clickWithDebounce {

        }
        binding.tvDelete.clickWithDebounce {

        }

    }


    private fun editHome() {
        val actionBar = (requireActivity() as? ActivityVault)?.supportActionBar
        val exitIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_exit)
        exitIcon?.let {
            actionBar?.setHomeAsUpIndicator(it)
        }
    }

    private fun normalHome() {
        val actionBar = (activity as? ActivityVault)?.supportActionBar
        actionBar?.setHomeAsUpIndicator(0)
        initToolBar()

//        val exitIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_back)
//        exitIcon?.let {
//            actionBar?.setHomeAsUpIndicator(it)
//        }
    }

    private fun initRecyclerView() {
        adapterPersistent = AdapterPersistent(onClickItem = {

        }, onLongClickItem = {
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

    }

    private fun showController() {
        binding.containerController.show()
    }


    override fun onDestroy() {
        AdapterPersistent.editMode = false
        super.onDestroy()
//        _binding = null
    }


}