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
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.config
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.invisible
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.config.Status
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.DialogProgressBinding
import com.neko.hiepdph.calculatorvault.viewmodel.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.sqrt


class DialogProgress(
    private val listItemSelected: List<FileVaultItem>,
    private val listOfSourceFile: List<File> = mutableListOf(),
    private val listOfTargetParentFolder: List<File> = mutableListOf(),
    private val action: Action = Action.ENCRYPT,
    private val encryptionMode: Int = EncryptionMode.HIDDEN,
    private val onResult: (status: Int, text: String, valueReturn: List<FileVaultItem>?) -> Unit

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

            Action.DELETE_All_RECYCLER_BIN_PERMANENT -> {
                binding.tvTitle.text = getString(R.string.empty_recycler_bin)
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
                viewModel.delete(
                    requireContext(),
                    listItemSelected,
                    listOfTargetParentFolder,
                    progress = { _: File? -> },
                    onResult = { listOfFileDeletedSuccess, listOfFileDeletedFailed ->
                        if (listOfFileDeletedSuccess.size == listItemSelected.size) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                listOfFileDeletedSuccess.forEach {
                                    val item = it
                                    item.isDeleted = true
                                    item.modified = Calendar.getInstance().timeInMillis
                                    viewModel.updateVaultItem(item)
                                }
                                onResult.invoke(
                                    Status.SUCCESS,
                                    getString(R.string.move_to_recycler_bin),
                                    listItemSelected
                                )
                                dismiss()
                            }
                        }

                        if (listOfFileDeletedFailed.size == listItemSelected.size) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                onResult(
                                    Status.FAILED,
                                    getString(R.string.move_to_recycler_bin_failed),
                                    listItemSelected
                                )
                                dismiss()
                            }
                        } else if (listOfFileDeletedFailed.size > 0) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                viewModel.deleteFileVault(listOfFileDeletedSuccess.map { it.id })
                                onResult.invoke(
                                    Status.WARNING, String.format(
                                        getString(R.string.move_to_recycler_bin_success_condition),
                                        listOfFileDeletedSuccess.size
                                    ) + " " + String.format(
                                        getString(R.string.move_to_recycler_bin_failed_condition),
                                        listOfFileDeletedFailed.size
                                    ), listItemSelected
                                )
                                dismiss()
                            }
                        }
                    },
                )

            } else {
                viewModel.deleteMultipleFolder(
                    listItemSelected.map { it.encryptedPath },
                    onResult = { listOfFileDeletedSuccess, listOfFileDeletedFailed ->
                        if (listOfFileDeletedSuccess.size == listOfSourceFile.size) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                viewModel.deleteFileVault(listItemSelected.map { it.id }
                                    .toMutableList())
                                onResult.invoke(
                                    Status.SUCCESS,
                                    getString(R.string.delete_success),
                                    listItemSelected
                                )
                                dismiss()
                            }
                        }
                        if (listOfFileDeletedFailed.size == listOfSourceFile.size) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                onResult(
                                    Status.FAILED,
                                    getString(R.string.delete_failed),
                                    listItemSelected
                                )
                                dismiss()
                            }
                        }
                    },

                    onProgress = {

                    },
                )
            }

        }
        if (action == Action.DELETE_PERMANENT) {
            viewModel.deleteMultipleFolder(listItemSelected.map { it.recyclerPath }, onProgress = {

            }, onResult = { listOfFileDeletedSuccess, listOfFileDeletedFailed ->
                if (listOfFileDeletedSuccess.size == listItemSelected.size) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.deleteFileVault(listItemSelected.map { it.id }.toMutableList())
                        onResult.invoke(
                            Status.SUCCESS, getString(R.string.delete_success), listItemSelected
                        )
                        dismiss()
                    }
                }

                if (listOfFileDeletedFailed.size == listItemSelected.size) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        onResult(Status.FAILED, getString(R.string.delete_failed), null)
                        dismiss()
                    }
                } else if (listOfFileDeletedFailed.size > 0) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.deleteFileVault(listItemSelected.filter {
                            it.recyclerPath in listOfFileDeletedSuccess
                        }.map { it.id })
                        onResult.invoke(
                            Status.WARNING, String.format(
                                getString(R.string.delete_success_condition),
                                listOfFileDeletedSuccess.size
                            ) + " " + String.format(
                                getString(R.string.delete_failed_condition),
                                listOfFileDeletedFailed.size
                            ), null
                        )
                        dismiss()
                    }
                }
            })

        }
        if (action == Action.DELETE_All_RECYCLER_BIN_PERMANENT) {
            viewModel.deleteAllRecyclerBin(requireContext().config.recyclerBinFolder.path,
                onResult = { listOfFileDeletedSuccess, listOfFileDeletedFailed ->
                    if (listOfFileDeletedSuccess.size == listItemSelected.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.deleteFileVault(listItemSelected.map { it.id })
                            onResult.invoke(
                                Status.SUCCESS, getString(R.string.delete_success), null
                            )
                            dismiss()
                        }
                    }

                    if (listOfFileDeletedFailed.size == listItemSelected.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            onResult(
                                Status.FAILED, getString(R.string.delete_failed), null
                            )
                            dismiss()
                        }
                    } else if (listOfFileDeletedFailed.size > 0) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.deleteFileVault(listItemSelected.filter {
                                it.recyclerPath in listOfFileDeletedSuccess
                            }.map { it.id })
                            onResult.invoke(
                                Status.WARNING, String.format(
                                    getString(R.string.delete_success_condition),
                                    listOfFileDeletedSuccess.size
                                ) + " " + String.format(
                                    getString(R.string.delete_failed_condition),
                                    listOfFileDeletedFailed.size
                                ), null
                            )
                            dismiss()
                        }
                    }

                },
                onProgress = {

                })

        }
        if (action == Action.UNLOCK) {
            viewModel.unLock(requireContext(),
                listItemSelected,
                listOfTargetParentFolder,
                listItemSelected.map { it.name },
                progress = { _: File? ->

                },
                onResult = { listOfFileSuccess, listOfFileFailed ->
                    if (listOfFileSuccess.size == listOfSourceFile.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.deleteFileVault(listItemSelected.map { it.id })
                            onResult.invoke(
                                Status.SUCCESS, String.format(
                                    getString(R.string.unlock_sucess),
                                    listItemSelected.size.toString()
                                ), listOfFileSuccess
                            )
                            dismiss()
                        }
                    }
                    if (listOfFileFailed.size == listOfSourceFile.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            onResult(
                                Status.FAILED, String.format(
                                    getString(R.string.unlock_failed),
                                    listItemSelected.size.toString()
                                ), listOfFileSuccess
                            )
                            dismiss()
                        }
                    } else if (listOfFileFailed.size > 0) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            viewModel.deleteFileVault(listOfFileSuccess.map { it.id })
                            onResult.invoke(
                                Status.WARNING, String.format(
                                    getString(R.string.delete_success_condition),
                                    listOfFileSuccess.size
                                ) + " " + String.format(
                                    getString(R.string.delete_failed_condition),
                                    listOfFileFailed.size
                                ), listOfFileSuccess
                            )
                            dismiss()
                        }
                    }
                })
        }
        if (action == Action.ENCRYPT) {
            disableCancelable()
            lifecycleScope.launch(Dispatchers.Default) {

                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.encrypt(
                        requireContext(),
                        listItemSelected,
                        listOfTargetParentFolder,
                        listItemSelected.map { it.name },
                        progress = { _: File? ->

                        },
                        onResult = { listOfFileSuccess, listOfFileFailed ->
                            if (listOfFileSuccess.size == listOfSourceFile.size) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    enableCancelable()
                                    showButton()
                                    statusSuccess()
                                    listOfFileSuccess.forEach { item ->
                                        item.apply {
                                            timeLock = Calendar.getInstance().timeInMillis
                                            encryptionType = encryptionMode
                                        }
                                        viewModel.insertVaultItem(item)
                                    }
                                    onResult.invoke(
                                        Status.SUCCESS,
                                        getString(R.string.encrypt_success),
                                        listOfFileSuccess
                                    )
                                }
                            }

                            if (listOfFileFailed.size == listItemSelected.size) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    enableCancelable()
                                    showButton()
                                    statusFailed()
                                    binding.tvStatus.text = getString(R.string.encrypt_failed)
                                    onResult.invoke(
                                        Status.FAILED, getString(R.string.encrypt_failed), null
                                    )
                                }
                            } else if (listOfFileFailed.isNotEmpty()) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    enableCancelable()
                                    showButton()
                                    statusFailed()
                                    listOfFileSuccess.forEach { item ->
                                        item.apply {
                                            timeLock = Calendar.getInstance().timeInMillis
                                            encryptionType = encryptionMode
                                        }
                                        viewModel.insertVaultItem(item)
                                    }
                                    binding.tvStatus.text = getString(R.string.encrypt_failed)
                                    onResult.invoke(
                                        Status.WARNING, String.format(
                                            getString(R.string.encrypt_success_condition),
                                            listOfFileSuccess.size
                                        ) + " " + String.format(
                                            getString(R.string.encrypt_failed_condition),
                                            listOfFileFailed.size
                                        ), listOfFileSuccess
                                    )
                                }
                            }
                        },


                        encryptionMode
                    )
                }
            }


        }
        if (action == Action.DECRYPT) {
            viewModel.decrypt(
                requireContext(),
                listItemSelected,
                listOfTargetParentFolder,
                listItemSelected.map { it.name },
                progress = { _: File? ->
                },
                onResult = { listOfFileVaultSuccess, listOfFileVaultFailed ->
                    if (listOfFileVaultSuccess.size == listItemSelected.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            enableCancelable()
                            showButton()
                            statusSuccess()
                            listOfFileVaultSuccess.map {
                                Log.d("TAG", "doAction: "+it.decodePath)
                                viewModel.updateVaultItem(it)
                            }
                            onResult.invoke(
                                Status.SUCCESS, String.format(
                                    getString(R.string.decrypt_sucess),
                                    listItemSelected.size.toString()
                                ), listOfFileVaultSuccess
                            )
                            dismiss()
                        }
                    }
                    if (listOfFileVaultFailed.size == listItemSelected.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            enableCancelable()
                            showButton()
                            statusFailed()
                            dismiss()
                            onResult(
                                Status.FAILED, String.format(
                                    getString(R.string.decrypt_failed),
                                    listItemSelected.size.toString()
                                ), null
                            )
                            dismiss()
                        }
                    } else if (listOfFileVaultFailed.size > 0) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            listOfFileVaultSuccess.map {
                                viewModel.updateVaultItem(it)
                            }
                            onResult.invoke(
                                Status.WARNING, String.format(
                                    getString(R.string.decrypt_sucess_condition),
                                    listOfFileVaultSuccess.size
                                ) + " " + String.format(
                                    getString(R.string.decrypt_failed_condition),
                                    listOfFileVaultFailed.size
                                ), listOfFileVaultSuccess
                            )
                            dismiss()
                        }
                    }
                },
            )
        }
        if (action == Action.RESTORE) {
            viewModel.restoreFile(requireContext(),
                listItemSelected,
                listOfTargetParentFolder,
                progress = { value: Float, _: File? ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        setProgressValue(value.toInt())
                    }
                },
                onResult = { listOfFileSuccess, listOfFileFailed ->
                    if (listOfFileSuccess.size == listItemSelected.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            listOfFileSuccess.map {
                                it.isDeleted = false
                                viewModel.updateFileVault(it)
                            }
                            onResult(
                                Status.SUCCESS,
                                getString(R.string.restore_successfully),
                                listOfFileSuccess
                            )
                            dismiss()
                        }
                    }
                    if (listOfFileFailed.size == listItemSelected.size) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            onResult(
                                Status.SUCCESS, getString(R.string.restore_failed), null
                            )
                            dismiss()
                        }
                    } else if (listOfFileFailed.size > 0) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            onResult.invoke(
                                Status.WARNING, String.format(
                                    getString(R.string.restore_successfully_condition),
                                    listOfFileSuccess.size
                                ) + " " + String.format(
                                    getString(R.string.restore_failed_condition),
                                    listOfFileFailed.size
                                ), listOfFileSuccess
                            )
                            dismiss()
                        }
                    }
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