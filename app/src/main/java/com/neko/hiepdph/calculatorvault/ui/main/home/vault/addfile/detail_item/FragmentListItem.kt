package com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item

//import com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item.adapter.AdapterListItem
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
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
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import com.neko.hiepdph.calculatorvault.databinding.FragmentListItemBinding
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item.adapter.AdapterListItem
import com.neko.hiepdph.calculatorvault.viewmodel.ListItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
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
    private var listItemSelected = mutableListOf<ListItem>()
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
        initToolBar()
        initRecycleView()
        initButton()
    }

    private fun initButton() {
        binding.btnMoveToVault.clickWithDebounce {
            viewModel.copyMoveFile(requireContext(),
                listItemSelected.map { File(it.mPath) }.toMutableList(),
                File(args.vaultPath),
                progress = { state: Int, value: Float, currentFile: File? ->
                },
                onSuccess = {
                    val newList = listItemSelected.toMutableList()
                    newList.forEach {
                        Log.d("TAG", "initButton: "+it.mOriginalPath)
                        it.path = args.vaultPath + "/${it.mName}"
                    }
                    val listItemVault = requireContext().config.listItemVault?.toMutableList()
                        ?: mutableListOf()
                    listItemVault.addAll(newList)
                    requireContext().config.listItemVault = listItemVault
                    CoroutineScope(Dispatchers.Main).launch {
                        observeData()
                        adapterListItem?.unSelectAll()
                        listItemSelected.clear()
                        checkListPath()
                        checkCheckBoxAll()
                    }
                }, onError = {
                },requireContext().config.encryptionMode)
        }
    }

    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.toolbar_menu_pick, menu)
                menu[0].actionView?.findViewById<View>(R.id.checkbox)?.setOnClickListener {
                    checkAllItem(menu[0].actionView?.findViewById<CheckBox>(R.id.checkbox)?.isChecked == true)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    else -> false
                }
            }

        })
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

    private fun observeData() {
        if (args.groupItem.type != Constant.TYPE_FILE) {
            viewModel.getItemListFromFolder(
                requireContext(), args.groupItem.folderPath, args.groupItem.type
            )
        } else {
            viewModel.getItemListFromFolder(
                requireContext(), args.groupItem.folderPath, args.groupItem.type, args.fileType
            )
        }
        viewModel.listItemList.observe(viewLifecycleOwner) {
            it?.let {
                adapterListItem?.setData(it, args.groupItem.type)
                sizeList = it.size
                binding.loading.hide()
                if (it.isNotEmpty()) {
                    binding.tvEmpty.hide()
                } else {
                    binding.tvEmpty.show()
//                    popBackStack(R.id.fragmentListItem)
                }

            }
        }
    }

    private fun initRecycleView() {
        adapterListItem = AdapterListItem(onClickItem = {
            listItemSelected.clear()
            listItemSelected.addAll(it)
            checkListPath()
            checkCheckBoxAll()
        })

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
    }
}