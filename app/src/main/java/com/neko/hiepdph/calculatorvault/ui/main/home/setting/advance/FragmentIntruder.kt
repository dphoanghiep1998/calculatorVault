package com.neko.hiepdph.calculatorvault.ui.main.home.setting.advance

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.databinding.FragmentIntruderBinding
import com.neko.hiepdph.calculatorvault.viewmodel.IntruderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentIntruder : Fragment() {
    private lateinit var binding: FragmentIntruderBinding
    private var adapter: AdapterIntruder? = null
    private val viewModel by activityViewModels<IntruderViewModel>()
    private var action: (() -> Unit)? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntruderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
    }

    private fun observeData() {
        viewModel.getItemListFromFolder(requireContext().config.intruderFolder.path).observe(viewLifecycleOwner) {
            it?.let {
                adapter?.setData(it)
            }
        }
    }

    private fun initView() {
        setupView()
        initAction()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        adapter = AdapterIntruder {

        }
        binding.rcvIntruder.adapter = adapter
        binding.rcvIntruder.layoutManager =
            GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
    }

    private fun initAction() {
        binding.itemIntruderSelfie.root.clickWithDebounce {
            checkPermission {
                binding.itemIntruderSelfie.switchChange.isChecked =
                    !binding.itemIntruderSelfie.switchChange.isChecked
                requireContext().config.photoIntruder =
                    binding.itemIntruderSelfie.switchChange.isChecked
            }

        }

        binding.itemIntruderSelfie.switchChange.setOnClickListener {
            if (!binding.itemIntruderSelfie.switchChange.isChecked) {
                checkPermission {
                    requireContext().config.photoIntruder =
                        binding.itemIntruderSelfie.switchChange.isChecked
                }
            }

        }

    }


    private fun setupView() {
        binding.itemIntruderSelfie.apply {
            switchChange.isChecked = requireContext().config.photoIntruder
            imvIcon.setImageResource(R.drawable.ic_intruder_light)
            tvContent.text = getString(R.string.intruder_selfie)
            switchChange.show()
            imvNext.hide()
            tvContent2.text = getString(R.string.intruder_content_2)
        }

    }

    private fun checkPermission(action: () -> Unit) {
        this.action = action
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.CAMERA
                )
            ) {
                checkCameraActivityLauncher(
                    action, Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)
                    )
                )
            } else {
                launcher.launch(Manifest.permission.CAMERA)

            }
        } else {
            action()
        }
    }


    private fun checkCameraActivityLauncher(action: () -> Unit, intent: Intent) {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                action()
            }
        }.launch(intent)
    }


    private val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            action?.invoke()
        }
    }
}