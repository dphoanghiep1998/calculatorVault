package com.neko.hiepdph.calculatorvault.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.neko.hiepdph.calculatorvault.common.extensions.SnackBarType
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.invisible
import com.neko.hiepdph.calculatorvault.common.extensions.popBackStack
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.extensions.showSnackBar
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.DialogProgressBinding
import com.neko.hiepdph.calculatorvault.encryption.CryptoCore
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.sqrt


class DialogProgress(
    private val listItemSelected: List<FileVaultItem> = mutableListOf(),
    private val listOfSourceFile: List<File> = mutableListOf(),
    private val listOfTargetParentFolder: List<File> = mutableListOf(),
    private val action: Action = Action.ENCRYPT,
    private val encryptionMode: Int = EncryptionMode.HIDDEN,
    private val vaultPath: String = "",
    private val onSuccess: (String) -> Unit,
    private val onFailed: (String) -> Unit,
    private val onResult: ((String) -> Unit)? = null

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

            Action.ENCRYPT -> {
                binding.tvTitle.text = getString(R.string.encryption)
                binding.tvStatus.text =
                    String.format(getString(R.string.encrypting), listItemSelected.size.toString())
            }

            Action.DECRYPT -> {
                binding.tvTitle.text = getString(R.string.decryption)
                binding.tvStatus.text =
                    String.format(getString(R.string.decrypting), listItemSelected.size.toString())
            }

            Action.RESTORE -> {
                binding.tvTitle.text = getString(R.string.restoration)
                binding.tvStatus.text =
                    String.format(getString(R.string.restoring), listItemSelected.size.toString())
            }

            Action.DELETE_PERMANENT -> {
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
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.deleteFileVault(listItemSelected.map { it.id }
                                .toMutableList())
                            onSuccess(getString(R.string.delete_success))
                            dismiss()
                        }
                    },
                    onProgress = { },
                    onError = {
                        lifecycleScope.launch(Dispatchers.Main) {
                            onFailed(getString(R.string.delete_success))
                            dismiss()
                        }
                    })
            }

        }
        if (action == Action.DELETE_PERMANENT) {
            viewModel.deleteMultipleFolder(listItemSelected.map { it.recyclerPath }, onSuccess = {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.deleteFileVault(listItemSelected.map { it.id }.toMutableList())
                    onSuccess(getString(R.string.delete_success))
                    dismiss()
                }
            }, onProgress = {
                lifecycleScope.launch(Dispatchers.Main) {
                    setProgressValue(it.toInt())
                }
            }, onError = {
                lifecycleScope.launch(Dispatchers.Main) {
                    onFailed(getString(R.string.delete_success))
                    dismiss()
                }
            })

        }
        if (action == Action.DELETE_All_RECYCLER_BIN_PERMANENT) {
            viewModel.deleteAllRecyclerBin(requireContext().config.recyclerBinFolder.path,
                onResult = { listOfFileDeletedSuccess, listOfFileDeletedFailed ->
                    if (listOfFileDeletedSuccess.size == listOfSourceFile.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.deleteFileVault(listItemSelected.map { it.id })
                            onSuccess(getString(R.string.delete_success))
                            dismiss()
                        }
                    }

                    if (listOfFileDeletedFailed.size == listOfSourceFile.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            onFailed(getString(R.string.delete_failed))
                            dismiss()
                        }
                    } else if (listOfFileDeletedFailed.size > 0) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.deleteFileVault(listItemSelected.filter {
                                it.recyclerPath in listOfFileDeletedSuccess
                            }.map { it.id })
                            onResult?.invoke(
                                String.format(
                                    getString(R.string.delete_success_condition),
                                    listOfFileDeletedSuccess.size
                                ) + " " + String.format(
                                    getString(R.string.delete_failed_condition),
                                    listOfFileDeletedFailed.size
                                )
                            )
                            dismiss()
                        }
                    }

                },
                onProgress = { value -> setProgressValue(value.toInt()) })

        }
        if (action == Action.UNLOCK) {
            viewModel.unLock(requireContext(),
                listOfSourceFile,
                listOfTargetParentFolder,
                listItemSelected.map { it.name },
                progress = { _: File? ->

                },
                onSuccess = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        onSuccess.invoke(
                            String.format(
                                getString(R.string.unlock_sucess), listItemSelected.size.toString()
                            )
                        )
                        dismiss()
                    }
                },
                onError = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        onFailed.invoke(
                            String.format(
                                getString(R.string.unlock_failed), listItemSelected.size.toString()
                            )
                        )

                        dismiss()
                    }
                })
        }
        if (action == Action.ENCRYPT) {
            Log.d("TAG", "doAction: " + encryptionMode)
            val listOfEncryptedString = mutableListOf<String>()
            listItemSelected.let { it ->
                listOfEncryptedString.addAll(it.map {
                    CryptoCore.getSingleInstance().encryptString(Constant.SECRET_KEY, it.name)
                })
            }
            disableCancelable()

            listItemSelected.forEachIndexed { index, item ->
                when (item.fileType) {
                    Constant.TYPE_PICTURE -> {
                        item.thumb =
                            imagePathToBitmap(item.originalPath)?.let { scaleBitmap(it, 512 * 512) }
                    }

                    Constant.TYPE_VIDEOS -> {
                        item.thumb = MediaStoreUtils.getImageThumb(
                            item.mediaStoreId, requireContext()
                        )?.let { scaleBitmap(it, 512 * 512) }
                    }

                    Constant.TYPE_AUDIOS -> {
                        item.thumb = MediaStoreUtils.getThumbnail(item.originalPath)
                            ?.let { scaleBitmap(it, 512 * 512) }
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
                    when (it) {
                        is FileNotFoundException -> {
                            lifecycleScope.launch(Dispatchers.Main) {
                                enableCancelable()
                                showButton()
                                statusFailed()
                                binding.tvStatus.text = getString(R.string.encrypt_failed)
                            }
                        }

                        else -> {
                            enableCancelable()
                            showButton()
                            statusFailed()
                        }
                    }

                },
                encryptionMode
            )
        }
        if (action == Action.DECRYPT) {
            viewModel.decrypt(
                requireContext(),
                listOfSourceFile,
                listOfTargetParentFolder,
                listItemSelected.map { it.name },
                progress = { _: File? ->
                },
                onSuccess = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        enableCancelable()
                        showButton()
                        statusSuccess()
                        dismiss()
                        onSuccess(it[0])

                    }

                },
                onError = {
                    lifecycleScope.launch(Dispatchers.Main) {
                        enableCancelable()
                        showButton()
                        statusFailed()
                        dismiss()
                        onFailed("")
                    }
                },
                encryptMode = encryptionMode
            )
        }
        if (action == Action.RESTORE) {
            viewModel.restoreFile(requireContext(),
                listOfSourceFile,
                listOfTargetParentFolder,
                0L,
                progress = { value: Float, _: File? ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        setProgressValue(value.toInt())
                    }
                },
                onSuccess = {
                    listItemSelected.map {
                        it.isDeleted = false
                        viewModel.updateFileVault(it)
                    }

                    showSnackBar(getString(R.string.restore_successfully), SnackBarType.SUCCESS)
                    dismiss()

                },
                onError = {
                    showSnackBar(getString(R.string.restore_failed), SnackBarType.FAILED)
                    dismiss()

                })
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
        binding.progressLoading.invisible()
        binding.tvProgress.hide()
        binding.imvSuccess.show()
    }

    private fun statusFailed() {
        binding.progressLoading.invisible()
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

    private fun imagePathToBitmap(filePath: String): Bitmap? {
        val image = File(filePath)
        val bmOptions = BitmapFactory.Options()
        return try {
            BitmapFactory.decodeFile(image.path, bmOptions)
        } catch (e: Exception) {
            null
        }
    }

    private fun scaleBitmap(
        input: Bitmap, maxBytes: Long
    ): Bitmap? {
        val currentWidth = input.width
        val currentHeight = input.height
        val currentPixels = currentWidth * currentHeight
        // Get the amount of max pixels:
        // 1 pixel = 4 bytes (R, G, B, A)
        val maxPixels = maxBytes / 4 // Floored
        if (currentPixels <= maxPixels) {
            // Already correct size:
            return input
        }
        // Scaling factor when maintaining aspect ratio is the square root since x and y have a relation:
        val scaleFactor = sqrt(maxPixels / currentPixels.toDouble())
        val newWidthPx = floor(currentWidth * scaleFactor).toInt()
        val newHeightPx = floor(currentHeight * scaleFactor).toInt()
        return Bitmap.createScaledBitmap(input, newWidthPx, newHeightPx, true)
    }


    private val callback = object : BackPressDialogCallBack {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
        }

    }
}