package com.neko.hiepdph.calculatorvault.ui.main.home.browser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.changeBackPressCallBack
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.data.database.model.BookmarkModel
import com.neko.hiepdph.calculatorvault.databinding.FragmentBrowserBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutMenuBrowserOptionBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirm
import com.neko.hiepdph.calculatorvault.dialog.DialogConfirmType
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityBrowser
import com.neko.hiepdph.calculatorvault.viewmodel.BrowserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentBrowser : Fragment() {
    private var _binding: FragmentBrowserBinding? = null
    private val binding get() = _binding!!
    private lateinit var popupWindow: PopupWindow
    private var adapter: AdapterBookmark? = null
    private val viewModel by viewModels<BrowserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        changeBackPressCallBack {
            requireActivity().finishAffinity()
        }
        _binding = FragmentBrowserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        initView()
        changeBackPressCallBack {
            requireActivity().finishAffinity()
        }
        observeListBookmark()
    }


    private fun observeListBookmark() {
        viewModel.getListBookmark().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                createPersistentBookmark()
            } else {
                adapter?.setData(it)
            }
        }
    }

    private fun createPersistentBookmark() {
        val listBookmark = mutableListOf(
            BookmarkModel(
                -1, getString(R.string.google), Constant.GOOGLE, "", true
            ),
            BookmarkModel(
                -1, getString(R.string.ducduckgo), Constant.DUCKDUCKGO, "", true
            ),
            BookmarkModel(
                -1, getString(R.string.search_encrypt), Constant.SEARCH_ENCRYPT, "", true
            ),
            BookmarkModel(-1, getString(R.string.qwant), Constant.QWANT, "", true),
        )
        for (item in listBookmark) {
            viewModel.insertBookmark(item)
        }
    }


    private fun initToolBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.toolbar_menu_browser, menu)

                val actionExpandListener = object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem): Boolean {

                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {

                        return true
                    }

                }
                val searchItem = menu.findItem(R.id.search)
                searchItem.setOnActionExpandListener(actionExpandListener)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = getString(R.string.search_query)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let {
                            val intent = Intent(requireContext(), ActivityBrowser::class.java)
                            intent.putExtra(Constant.KEY_URL, Constant.GOOGLE+"?q=$it")
                            startActivity(intent)
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let {}
                        return true
                    }

                })

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val menuItemView = requireActivity().findViewById<View>(R.id.option)
                return when (menuItem.itemId) {
                    R.id.option -> {
                        showPopupWindow(menuItemView)
                        true
                    }
                    else -> false
                }

            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun initPopupWindow() {
        val inflater: LayoutInflater =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
        val bindingLayout = LayoutMenuBrowserOptionBinding.inflate(inflater, null, false)

        popupWindow = PopupWindow(
            bindingLayout.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        bindingLayout.root.clickWithDebounce {
            popupWindow.dismiss()
        }
        bindingLayout.tvClearCache.clickWithDebounce {
            clearCache()
        }
    }

    private fun showPopupWindow(menuItem: View) {
        popupWindow.showAsDropDown(menuItem)

    }

    private fun clearCache() {

    }


    private fun initView() {
        initPopupWindow()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = AdapterBookmark(onClickItem = {
            val intent = Intent(requireContext(), ActivityBrowser::class.java)
            intent.putExtra(Constant.KEY_URL, it)
            startActivity(intent)
        }, onLongClickItem = {
            showDialogConfirmDelete(it)
        })
        binding.rcvBookmark.adapter = adapter
        val layoutManager = GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false)
        binding.rcvBookmark.layoutManager = layoutManager
    }

    private fun showDialogConfirmDelete(id: Int) {
        val confirmDialog = DialogConfirm(onPositiveClicked= {
                viewModel.deleteBookmark(id)
        }, DialogConfirmType.DELETE, getString(R.string.this_bookmark))

        confirmDialog.show(childFragmentManager, confirmDialog.tag)
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}