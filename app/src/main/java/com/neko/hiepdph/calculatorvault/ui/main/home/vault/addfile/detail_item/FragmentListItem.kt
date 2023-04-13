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
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.toastLocation
import com.neko.hiepdph.calculatorvault.common.utils.IMoveFile
import com.neko.hiepdph.calculatorvault.databinding.FragmentListItemBinding
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item.adapter.AdapterListItem
import com.neko.hiepdph.calculatorvault.viewmodel.ListItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentListItem : Fragment() {
    private var _binding: FragmentListItemBinding? = null
    private val binding get() = _binding!!


    private val viewModel by viewModels<ListItemViewModel>()
    private var adapterListItem: AdapterListItem? = null
    private val args: FragmentListItemArgs by navArgs()
    private var listPathSelected = mutableListOf<String>()
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
        (requireActivity() as ActivityVault).getToolbar().title = args.groupItem.name

    }

    private fun initView() {
        initToolBar()
        initRecycleView()
        initButton()
    }

    private fun initButton() {
        binding.btnMoveToVault.clickWithDebounce {
            viewModel.copyMoveFile(listPathSelected, args.vaultPath, object : IMoveFile {
                override fun onSuccess() {
                    CoroutineScope(Dispatchers.Main).launch{
                       observeData()
                        adapterListItem?.unSelectAll()
                        listPathSelected.clear()
                        checkListPath()
                        checkCheckBoxAll()
                    }
                }

                override fun onFailed() {
                    Log.d("TAG", "failed: ")
                }

                override fun onDoneWithWarning() {
                }

            })
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

            }
        }
    }

    private fun initRecycleView() {
        adapterListItem = AdapterListItem(onClickItem = {
            listPathSelected.clear()
            listPathSelected.addAll(it)
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
        checkbox?.isChecked = listPathSelected.size == sizeList && sizeList > 0
    }

    private fun checkListPath() {
        if (listPathSelected.isEmpty()) {
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