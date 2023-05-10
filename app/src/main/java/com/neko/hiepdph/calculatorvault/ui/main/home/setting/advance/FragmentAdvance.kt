package com.neko.hiepdph.calculatorvault.ui.main.home.setting.advance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neko.hiepdph.calculatorvault.databinding.FragmentAdvanceBinding

class FragmentAdvance : Fragment() {

    private lateinit var binding: FragmentAdvanceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAdvanceBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }


    private fun initView(){
        binding.itemFakePassword.apply {

        }

        binding.itemIntruderSelfie.apply {

        }

        binding.itemProhibitScreenshots.apply {

        }
    }


}