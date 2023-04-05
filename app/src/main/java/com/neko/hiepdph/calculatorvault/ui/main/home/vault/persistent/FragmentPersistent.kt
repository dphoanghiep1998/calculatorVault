package com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.navigateToPage
import com.neko.hiepdph.calculatorvault.databinding.FragmentPersistentBinding
import com.neko.hiepdph.calculatorvault.dialog.DialogAddFile
import com.neko.hiepdph.calculatorvault.ui.activities.ActivityVault
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter.AdapterPersistent
import com.neko.hiepdph.calculatorvault.viewmodel.PersistentViewModel

enum class PersistentType {
    VIDEOS, AUDIOS, DOCUMENTS, PICTURES;

    companion object {
        @JvmStatic
        fun fromString(value: String): PersistentType {
            return when (value) {
                "VIDEOS" -> VIDEOS
                "AUDIOS" -> AUDIOS
                "DOCUMENTS" -> DOCUMENTS
                "PICTURES" -> PICTURES
                else -> throw IllegalArgumentException("Invalid enum value: $value")
            }
        }
    }
}

class FragmentPersistent : Fragment() {
    private var _binding: FragmentPersistentBinding? = null
    private val binding get() = _binding!!
    private val args: FragmentPersistentArgs by navArgs()
    private val viewModel: PersistentViewModel by viewModels()
    private var adapterPersistent: AdapterPersistent? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersistentBinding.inflate(inflater, container, false)
        (requireActivity() as ActivityVault).supportActionBar?.title = args.title
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        initRecyclerView()
        initData()
        initButton()
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
    }

    private fun initRecyclerView() {
        adapterPersistent = AdapterPersistent(onClickItem = {

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



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}