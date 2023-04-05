package com.neko.hiepdph.calculatorvault.ui.main.home.browser

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.changeBackPressCallBack

class FragmentBrowser : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changeBackPressCallBack {
            requireActivity().finishAffinity()
        }
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }



}