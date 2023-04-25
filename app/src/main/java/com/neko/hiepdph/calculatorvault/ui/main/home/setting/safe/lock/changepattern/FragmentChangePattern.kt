package com.neko.hiepdph.calculatorvault.ui.main.home.setting.safe.lock.changepattern

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.popBackStack
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.databinding.FragmentChangePatternBinding
import com.neko.hiepdph.calculatorvault.viewmodel.ChangePatternViewModel
import com.takwolf.android.lock9.Lock9View.GestureCallback
import java.util.Arrays


class FragmentChangePattern : Fragment() {
    private var _binding: FragmentChangePatternBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ChangePatternViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePatternBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeState()
    }

    private fun initView() {
        setupLockView()
    }
    private fun observeState(){
        viewModel.currentState.observe(viewLifecycleOwner){
            it?.let {
                if(requireContext().config.patternLock.isEmpty()){
                    when(it){
                        0 -> {
                            binding.tvTitle.text = getString(R.string.draw_a_new_key_pattern)
                        }
                        1->{
                            binding.tvTitle.text = getString(R.string.draw_pattern_again_to_confirm)
                        }
                    }
                }else{
                    when(it){
                        0 -> {
                            binding.tvTitle.text = getString(R.string.draw_current_key)
                        }
                        1->{
                            binding.tvTitle.text = getString(R.string.draw_a_new_key_pattern)
                        }
                        2->{
                            binding.tvTitle.text = getString(R.string.draw_pattern_again_to_confirm)
                        }
                    }
                }
            }
        }
    }

    private fun setupLockView() {
        binding.lock9View.setEnableVibrate(requireContext().config.tactileFeedback)
        binding.lock9View.setHighlighted(requireContext().config.visiblePattern)
        binding.lock9View.setGestureCallback(object :GestureCallback{
            override fun onNodeConnected(numbers: IntArray) {
            }

            override fun onGestureFinished(numbers: IntArray) {
                Log.d("TAG", "onGestureFinished: "+numbers.contentToString())
                checkPattern(numbers)
            }
        })

    }
    private fun checkPattern(pattern:IntArray){
        val state = viewModel.currentState.value
        state?.let {
            if(requireContext().config.patternLock.isEmpty()){
                when(it){
                    0 -> {
                        if(pattern.size < 4){
                            showSnackBar(getString(R.string.pattern_atleast_4_node),SnackBarType.FAILED)
                        }else{
                            viewModel.setState1Pattern(pattern.toMutableList())
                            viewModel.setState(1)
                        }
                    }
                    1->{
                        if(pattern.size <4){
                            showSnackBar(getString(R.string.pattern_atleast_4_node),SnackBarType.FAILED)
                            return
                        }
                        if(!(pattern contentEquals viewModel.state1Pattern.value?.toIntArray())){
                            showSnackBar(getString(R.string.pattern_not_match),SnackBarType.FAILED)
                            return
                        }
                        requireContext().config.patternLock = pattern.toMutableList()
                        showSnackBar(getString(R.string.pattern_lock_set_success),SnackBarType.SUCCESS)
                        popBackStack(R.id.fragmentChangePattern)
                    }
                }
            }else{
                when(it){
                    0 -> {
                        if(pattern.size < 4){
                            showSnackBar(getString(R.string.pattern_atleast_4_node),SnackBarType.FAILED)
                            return
                        }
                        if(!(pattern contentEquals requireContext().config.patternLock.toIntArray())){
                            showSnackBar(getString(R.string.pattern_not_match),SnackBarType.FAILED)
                            return
                        }
                        viewModel.setState(1)
                    }
                    1->{
                        if(pattern.size <4){
                            showSnackBar(getString(R.string.pattern_atleast_4_node),SnackBarType.FAILED)
                            return
                        }
                        viewModel.setState2Pattern(pattern.toMutableList())
                        viewModel.setState(2)
                    }
                    2->{
                        if(pattern.size < 4){
                            showSnackBar(getString(R.string.pattern_atleast_4_node),SnackBarType.FAILED)
                            return
                        }
                        if(!(pattern contentEquals viewModel.state2Pattern.value?.toIntArray())){
                            showSnackBar(getString(R.string.pattern_not_match),SnackBarType.FAILED)
                            return
                        }
                        requireContext().config.patternLock = pattern.toMutableList()
                        showSnackBar(getString(R.string.pattern_lock_set_success),SnackBarType.SUCCESS)
                        popBackStack(R.id.fragmentChangePattern)
                    }
                }
            }
        }

    }


}