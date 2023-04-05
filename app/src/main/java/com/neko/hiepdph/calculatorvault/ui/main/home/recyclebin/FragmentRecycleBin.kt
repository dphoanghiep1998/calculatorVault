package com.neko.hiepdph.calculatorvault.ui.main.home.recyclebin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.databinding.FragmentRecycleBinBinding


class FragmentRecycleBin : Fragment() {
    private var _binding: FragmentRecycleBinBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


}