package com.neko.hiepdph.calculatorvault.ui.main.home.language

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.changeBackPressCallBack
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.utils.supportDisplayLang
import com.neko.hiepdph.calculatorvault.common.utils.supportedLanguages
import com.neko.hiepdph.calculatorvault.databinding.FragmentLanguageBinding
import java.util.*


class FragmentLanguage : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    private var adapterLanguage: AdapterLanguage? = null
    private var currentLanguage = Locale.getDefault().language

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        changeBackPressCallBack {

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar()
        initView()
    }

    private fun initView() {
        initRecyclerView()
    }
    private fun initRecyclerView() {
        val mLanguageList: MutableList<Any> = supportedLanguages().toMutableList()
        val mDisplayLangList: MutableList<Any> = supportDisplayLang().toMutableList()
        handleUnSupportLang(mLanguageList)
        mLanguageList.add(1, "adsApp")
        mDisplayLangList.add(1, "adsApp")
        adapterLanguage = AdapterLanguage(requireContext(), onCLickItem = {
            currentLanguage = it.language
        })
        adapterLanguage?.setData(mLanguageList,mDisplayLangList)
        binding.rcvLanguage.adapter = adapterLanguage
        binding.rcvLanguage.layoutManager = LinearLayoutManager(requireContext())
        adapterLanguage?.setCurrentLanguage(requireContext().config.language)
    }

    private fun initToolBar() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.toolbar_menu_language, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.btn_check -> {
                        applyLanguage()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }
    private fun handleUnSupportLang(mLanguageList: MutableList<Any>) {
        var support = false
        mLanguageList.forEachIndexed { index, item ->
            if (item is Locale) {
                if (item.language == currentLanguage) {
                    support = true
                }
            }
        }
        if (!support) {
            currentLanguage = (mLanguageList[0] as Locale).language
        }
    }

    private fun applyLanguage() {
        requireContext().config.language = currentLanguage
        startActivity(requireActivity().intent)
        requireActivity().finish()
    }


}