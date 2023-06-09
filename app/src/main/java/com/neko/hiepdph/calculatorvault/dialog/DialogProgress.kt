package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.enums.Action
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.popBackStack
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.toByteArray
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.DialogProgressBinding
import com.neko.hiepdph.calculatorvault.encryption.CryptoCore
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.util.Calendar


class DialogProgress(
    private val listItemSelected: List<FileVaultItem> = mutableListOf(),
    private val listOfSourceFile: List<File> = mutableListOf(),
    private val listOfTargetParentFolder: List<File> = mutableListOf(),
    private val action: Action = Action.ENCRYPT,
    private val encryptionMode: Int = EncryptionMode.HIDDEN,
    private val vaultPath: String = "",
    private val onSuccess: (String) -> Unit,
    private val onFailed: (String) -> Unit

) : DialogFragment() {
    private lateinit var binding: DialogProgressBinding
    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = DialogCallBack(requireContext(), callback)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(requireContext().getColor(R.color.blur)))
        dialog.window!!.setLayout(
            (requireContext().resources.displayMetrics.widthPixels),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogProgressBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.progressValue.observe(viewLifecycleOwner) {
                setProgressValue(it.toInt())
            }
        }

    }

    private fun initView() {
        binding.tvTitle.text = ""
        when (action) {
            Action.UNLOCK -> {
                binding.tvTitle.text = getString(R.string.unlock)
                binding.tvStatus.text =
                    String.format(getString(R.string.unlocking), listItemSelected.size.toString())
            }

            Action.DELETE -> {
                binding.tvTitle.text = getString(R.string.delete)
                binding.tvStatus.text =
                    String.format(getString(R.string.deleting), listItemSelected.size.toString())
            }

            else -> {

            }
        }
        initButton()
        doAction()
    }

    private fun doAction() {
        if (action == Action.DELETE) {
            if (requireActivity().config.moveToRecyclerBin) {
                viewModel.copy(requireContext(),
                    listOfSourceFile,
                    listOfTargetParentFolder,
                    progress = { _: File? -> },
                    onSuccess = {
                        lifecycleScope.launch(Dispatchers.Main) {
                            listItemSelected.forEach {
                                val item = it
                                item.isDeleted = true
                                viewModel.updateVaultItem(item)
                            }
                            onSuccess.invoke(getString(R.string.move_to_recycler_bin))
                            dismiss()
                        }

                    },
                    onError = {
                        lifecycleScope.launch(Dispatchers.Main) {
                            onFailed.invoke(getString(R.string.move_to_recycler_bin_failed))
                            dismiss()
                        }

                    })

            } else {
                viewModel.deleteMultipleFolder(listItemSelected.map { it.encryptedPath },
                    onSuccess = {
                        viewModel.deleteFileVault(listItemSelected.map { it.id }.toMutableList())
                        onSuccess(getString(R.string.delete_success))
                        dismiss()
                    },
                    onProgress = { },
                    onError = {
                        onFailed(getString(R.string.delete_success))
                        dismiss()
                    })
            }

        }
        if (action == Action.DELETE_PERMANENT) {
            viewModel.deleteMultipleFolder(listItemSelected.map { it.recyclerPath }, onSuccess = {
                viewModel.deleteFileVault(listItemSelected.map { it.id }.toMutableList())
                onSuccess(getString(R.string.delete_success))
                dismiss()
            }, onProgress = { setProgressValue(it.toInt()) }, onError = {
                onFailed(getString(R.string.delete_success))
                dismiss()
            })

        }
        if (action == Action.DELETE_All_PERMANENT) {
            viewModel.deleteAllRecyclerBin(requireContext().config.recyclerBinFolder.path,
                onSuccess = {
                    viewModel.deleteFileVault(listItemSelected.map { it.id })
                    onSuccess(getString(R.string.delete_success))
                    dismiss()
                },
                onError = {
                    onFailed(getString(R.string.delete_failed))
                    dismiss()
                })

        }
        if (action == Action.UNLOCK) {
            viewModel.decrypt(requireContext(),
                listOfSourceFile,
                listOfTargetParentFolder,
                listItemSelected.map { it.name },
                progress = { _: File? ->

                },
                onSuccess = {
                    onSuccess.invoke(
                        String.format(
                            getString(R.string.unlock_sucess), listItemSelected.size.toString()
                        )
                    )
                    dismiss()

                },
                onError = {
                    onFailed.invoke(
                        String.format(
                            getString(R.string.unlock_failed), listItemSelected.size.toString()
                        )
                    )

                    dismiss()
                })
        }

        if (action == Action.ENCRYPT) {
            val listOfEncryptedString = mutableListOf<String>()
            listItemSelected.let {
                listOfEncryptedString.addAll(it.map {
                    CryptoCore.getInstance(requireContext())
                        .encryptString(Constant.SECRET_KEY, it.name)
                })
            }
            disableCancelable()

            listItemSelected.forEachIndexed { index, item ->
                when (item.fileType) {
                    Constant.TYPE_PICTURE -> {
                        item.thumb = Base64.encodeToString(
                            imageToByteArray(item.originalPath), Base64.DEFAULT
                        )
                    }

                    Constant.TYPE_AUDIOS -> {
                        item.thumb = Base64.encodeToString(
                            MediaStoreUtils.getThumbnail(item.originalPath)?.toByteArray(),
                            Base64.DEFAULT
                        )
                    }
                }

            }
            viewModel.encrypt(
                requireContext(),
                listOfSourceFile,
                listOfTargetParentFolder,
                listOfEncryptedString,
                progress = { _: File? ->

                },
                onSuccess = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        enableCancelable()
                        showButton()
                        statusSuccess()


                        listItemSelected.forEachIndexed { index, item ->
                            item.apply {
                                recyclerPath =
                                    "${requireContext().config.recyclerBinFolder.path}/${listOfEncryptedString[index]}"
                                timeLock = Calendar.getInstance().timeInMillis
                                encryptionType = encryptionMode
                                encryptedPath = "$vaultPath/${
                                    listOfEncryptedString[index]
                                }"
                            }
                            viewModel.insertVaultItem(item)
                        }
                        popBackStack(R.id.fragmentListItem)
                    }

                },
                onError = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        enableCancelable()
                        showButton()
                        statusFailed()
                    }
                },
                requireContext().config.encryptionMode
            )
        }
    }

//    fun setData(title: String, status: String) {
//        binding.tvTitle.text = title
//        binding.tvStatus.text = status
//    }

    private fun setProgressValue(value: Int) {
        binding.progressLoading.setProgress(value.toFloat())
        binding.tvProgress.text = "$value %"
    }

    fun hideButton() {
        binding.btnOk.hide()
    }

    private fun statusSuccess() {
        binding.progressLoading.hide()
        binding.tvProgress.hide()
        binding.imvSuccess.show()
    }

    private fun statusFailed() {
        binding.progressLoading.hide()
        binding.tvProgress.hide()
        binding.imvFailed.show()
    }

    private fun enableCancelable() {
        binding.root.setOnClickListener {
            dismiss()
        }

    }

    private fun disableCancelable() {
        binding.root.setOnClickListener {

        }
    }

    private fun showButton() {
        binding.btnOk.show()
//        binding.btnTips.show()
    }

    private fun initButton() {
        binding.btnOk.clickWithDebounce {
            dismiss()
        }
        binding.containerMain.setOnClickListener {

        }
    }

    private fun openTips() {

    }

    private fun imageToByteArray(filePath: String): ByteArray {
        val file = File(filePath)
        val byteStream = FileInputStream(file)
        val byteBuffer = ByteArray(file.length().toInt())

        byteStream.read(byteBuffer)
        byteStream.close()

        return byteBuffer
    }


    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
        }

    }
}