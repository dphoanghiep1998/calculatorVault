package com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.getFormattedDuration
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPersistentAudiosBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPersistentFileBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPersistentOtherFileBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPersistentPictureBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPersistentVideosBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutPersistentItemFileOptionMenuBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutPersistentItemOptionMenuBinding

class AdapterPersistentNew(
    private var mType: String = Constant.TYPE_PICTURE,
    private val onClickItem: (FileVaultItem) -> Unit,
    private val onLongClickItem: (List<FileVaultItem>) -> Unit,
    private val onEditItem: (List<FileVaultItem>) -> Unit,
    private val onSelectAll: (List<FileVaultItem>) -> Unit,
    private val onOpenDetail: (FileVaultItem) -> Unit,
    private val onDeleteItem: (FileVaultItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        var editMode = false
        private const val PAYLOAD_CHECK = "PAYLOAD_CHECK"
    }

    private fun showCheckboxAll() {
        Log.d("TAG", "showCheckboxAll: "+differ.currentList.size)
        notifyItemRangeChanged(0, differ.currentList.size, PAYLOAD_CHECK)
    }

    fun selectAll() {
        listOfItemSelected.addAll(differ.currentList)
        onSelectAll.invoke(listOfItemSelected.toMutableList())
        notifyItemRangeChanged(0, differ.currentList.size, PAYLOAD_CHECK)
    }

    fun unSelectAll() {
        listOfItemSelected.clear()
        showCheckboxAll()
    }

    private var listOfItemSelected = mutableSetOf<FileVaultItem>()
    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FileVaultItem>() {

        override fun areItemsTheSame(oldItem: FileVaultItem, newItem: FileVaultItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FileVaultItem, newItem: FileVaultItem): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val binding = LayoutItemPersistentPictureBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

                return ItemPictureViewHolder(binding)
            }

            1 -> {
                val binding = LayoutItemPersistentAudiosBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemAudioViewHolder(binding)
            }

            2 -> {
                val binding = LayoutItemPersistentVideosBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemVideoViewHolder(binding)
            }

            3 -> {
                val binding = LayoutItemPersistentFileBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemFileViewHolder(binding)
            }

            else -> {
                val binding = LayoutItemPersistentOtherFileBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemOtherFileViewHolder(binding)
            }
        }
    }


    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            when (holder.itemViewType) {
                0 -> {
                    with(holder as ItemPictureViewHolder) {
                        bind(differ.currentList[position])
                    }
                }

                1 -> {
                    with(holder as ItemAudioViewHolder) {
                        bind(differ.currentList[position])
                    }
                }

                2 -> {
                    with(holder as ItemVideoViewHolder) {
                        bind(differ.currentList[position])
                    }
                }

                3 -> {
                    with(holder as ItemFileViewHolder) {
                        bind(differ.currentList[position])
                    }
                }

                else -> {
                    with(holder as ItemOtherFileViewHolder) {
                        bind(differ.currentList[position])
                    }
                }
            }

        } else {
            for (payload in payloads) {
                if (payload == PAYLOAD_CHECK) {
                    val item = differ.currentList[position]
                    when (holder.itemViewType) {
                        0 -> {
                            with(holder as ItemPictureViewHolder) {
                                if (editMode) {
                                    binding.checkBox.show()
                                } else {
                                    binding.checkBox.hide()
                                }
                                binding.checkBox.isChecked = item in listOfItemSelected
                            }
                        }

                        1 -> {
                            with(holder as ItemAudioViewHolder) {
                                if (editMode) {
                                    binding.option.hide()
                                    binding.checkBox.show()
                                } else {
                                    binding.option.show()
                                    binding.checkBox.hide()
                                }
                                binding.checkBox.isChecked = item in listOfItemSelected
                            }
                        }

                        2 -> {
                            with(holder as ItemVideoViewHolder) {
                                if (editMode) {
                                    binding.checkBox.show()

                                } else {
                                    binding.checkBox.hide()
                                }
                                binding.checkBox.isChecked = item in listOfItemSelected
                            }
                        }

                        3 -> {
                            with(holder as ItemFileViewHolder) {
                                if (editMode) {
                                    binding.checkBox.show()
                                    binding.option.hide()
                                } else {
                                    binding.checkBox.hide()
                                    binding.option.show()
                                }
                                binding.checkBox.isChecked = item in listOfItemSelected
                            }
                        }

                        else -> {
                            with(holder as ItemOtherFileViewHolder) {
                                if (editMode) {
                                    binding.checkBox.show()
                                    binding.option.hide()
                                } else {
                                    binding.checkBox.hide()
                                    binding.option.show()
                                }
                                binding.checkBox.isChecked = item in listOfItemSelected
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemViewType(position: Int): Int = when (mType) {
        Constant.TYPE_PICTURE -> 0
        Constant.TYPE_AUDIOS -> 1
        Constant.TYPE_VIDEOS -> 2
        Constant.TYPE_FILE -> 3
        else -> 4
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<FileVaultItem>) {
        differ.submitList(list)
    }

    inner class ItemPictureViewHolder(val binding: LayoutItemPersistentPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileVaultItem) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
            if (item.thumb != null) {
                Glide.with(itemView.context).asBitmap().load(item.thumb)
                    .placeholder(R.drawable.ic_error_image).apply(requestOptions)
                    .into(binding.imvThumb)
            } else {
                Glide.with(itemView.context).load(R.drawable.ic_error_image).apply(requestOptions)
                    .into(binding.imvThumb)
            }
            if (editMode) {
                binding.checkBox.show()
            } else {
                binding.checkBox.hide()
            }

            binding.checkBox.isChecked = item in listOfItemSelected

            binding.root.setOnLongClickListener {
                if (editMode) {
                    return@setOnLongClickListener false
                }
                editMode = true
                listOfItemSelected.add(item)
                onLongClickItem(listOfItemSelected.toMutableList())
                showCheckboxAll()
                return@setOnLongClickListener true
            }
            binding.root.setOnClickListener {
                if (!editMode) {
                    onClickItem.invoke(item)
                } else {
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                    if (binding.checkBox.isChecked) {
                        listOfItemSelected.add(item)
                    } else {
                        listOfItemSelected.remove(item)
                    }
                    onEditItem(listOfItemSelected.toMutableList())
                }
            }
            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    listOfItemSelected.add(item)
                } else {
                    listOfItemSelected.remove(item)
                }
                onEditItem(listOfItemSelected.toMutableList())
            }
        }
    }

    inner class ItemVideoViewHolder(val binding: LayoutItemPersistentVideosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileVaultItem) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))

            binding.checkBox.isChecked = item in listOfItemSelected
            if (item.thumb != null) {
                Glide.with(itemView.context).asBitmap().load(item.thumb)
                    .placeholder(R.drawable.ic_error_video).apply(requestOptions)
                    .into(binding.imvThumb)
            } else {
                Glide.with(itemView.context).load(R.drawable.ic_error_video).apply(requestOptions)
                    .into(binding.imvThumb)
            }


            binding.tvDuration.text = item.durationLength?.getFormattedDuration()
            binding.root.setOnLongClickListener {
                if (editMode) {
                    return@setOnLongClickListener false
                }
                editMode = true
                listOfItemSelected.add(item)
                onLongClickItem(listOfItemSelected.toMutableList())
                showCheckboxAll()
                return@setOnLongClickListener true
            }
            binding.root.setOnClickListener {
                if (!editMode) {
                    onClickItem.invoke(item)
                } else {
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                    if (binding.checkBox.isChecked) {
                        listOfItemSelected.add(item)
                    } else {
                        listOfItemSelected.remove(item)
                    }
                    onEditItem(listOfItemSelected.toMutableList())
                }
            }
            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    listOfItemSelected.add(item)
                } else {
                    listOfItemSelected.remove(item)
                }
                onEditItem(listOfItemSelected.toMutableList())
            }
        }
    }

    inner class ItemAudioViewHolder(val binding: LayoutItemPersistentAudiosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: FileVaultItem) {
            binding.tvNameAudio.isSelected = true
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
            if (item.thumb != null) {
                Glide.with(itemView.context).asBitmap().load(item.thumb)
                    .placeholder(R.drawable.ic_error_image).apply(requestOptions)
                    .into(binding.imvThumb)
            } else {
                Glide.with(itemView.context).load(R.drawable.ic_error_image).apply(requestOptions)
                    .into(binding.imvThumb)
            }
            binding.checkBox.isChecked = item in listOfItemSelected
            if (editMode) {
                binding.option.hide()
                binding.checkBox.show()
            } else {
                binding.option.show()
                binding.checkBox.hide()
            }
            binding.tvNameAudio.text = item.name
            binding.tvDurationAuthor.text =
                item.durationLength?.getFormattedDuration().toString() + "-" + item.artist
            binding.option.clickWithDebounce {
                showPopupWindow(
                    itemView.context, binding.option, item, onClickItem, onDeleteItem, onOpenDetail
                )
            }


            binding.root.setOnLongClickListener {
                if (editMode) {
                    return@setOnLongClickListener false
                }
                editMode = true
                listOfItemSelected.add(item)
                onLongClickItem(listOfItemSelected.toMutableList())

                showCheckboxAll()

                return@setOnLongClickListener true
            }
            binding.root.setOnClickListener {
                if (!editMode) {
                    onClickItem.invoke(item)
                } else {
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                    if (binding.checkBox.isChecked) {
                        listOfItemSelected.add(item)
                    } else {
                        listOfItemSelected.remove(item)
                    }
                    onEditItem(listOfItemSelected.toMutableList())
                }
            }

            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    listOfItemSelected.add(item)
                } else {
                    listOfItemSelected.remove(item)
                }
                onEditItem(listOfItemSelected.toMutableList())
            }
        }
    }

    inner class ItemFileViewHolder(val binding: LayoutItemPersistentFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileVaultItem) {
            Glide.with(itemView.context).load(getImageForItemFile(item))
                .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
            binding.tvNameDocument.isSelected = true
            binding.tvNameDocument.text = item.name
            binding.tvSize.text = item.size.formatSize()
            binding.option.clickWithDebounce {
                showPopupWindowFile(
                    itemView.context, binding.option, item, onClickItem, onDeleteItem, onOpenDetail
                )
            }
            if (editMode) {
                binding.option.hide()
                binding.checkBox.show()
            } else {
                binding.option.show()
                binding.checkBox.hide()
            }
            binding.checkBox.isChecked = item in listOfItemSelected

            binding.root.setOnLongClickListener {
                if (editMode) {
                    return@setOnLongClickListener false
                }
                editMode = true
                listOfItemSelected.add(item)
                onLongClickItem(listOfItemSelected.toMutableList())
                showCheckboxAll()

                return@setOnLongClickListener true
            }
            binding.root.setOnClickListener {
                if (!editMode) {
                    onClickItem.invoke(item)
                } else {
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                    if (binding.checkBox.isChecked) {
                        listOfItemSelected.add(item)
                    } else {
                        listOfItemSelected.remove(item)
                    }
                    onEditItem(listOfItemSelected.toMutableList())
                }
            }
            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    listOfItemSelected.add(item)
                } else {
                    listOfItemSelected.remove(item)
                }
                onEditItem(listOfItemSelected.toMutableList())
            }
        }
    }

    inner class ItemOtherFileViewHolder(val binding: LayoutItemPersistentOtherFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileVaultItem) {
            Glide.with(itemView.context).load(getImageForItemFile(item))
                .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
            binding.checkBox.isChecked = item in listOfItemSelected

            binding.root.setOnLongClickListener {
                if (editMode) {
                    return@setOnLongClickListener false
                }
                editMode = true
                listOfItemSelected.add(item)
                onLongClickItem(listOfItemSelected.toMutableList())
                showCheckboxAll()
                return@setOnLongClickListener true
            }
            binding.root.setOnClickListener {
                if (!editMode) {
                    onClickItem.invoke(item)
                } else {
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                    if (binding.checkBox.isChecked) {
                        listOfItemSelected.add(item)
                    } else {
                        listOfItemSelected.remove(item)
                    }
                    onEditItem(listOfItemSelected.toMutableList())
                }
            }
            if (editMode) {
                binding.option.hide()
                binding.checkBox.show()
            } else {
                binding.option.show()
                binding.checkBox.hide()
            }
            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    listOfItemSelected.add(item)
                } else {
                    listOfItemSelected.remove(item)
                }
                onEditItem(listOfItemSelected.toMutableList())
            }
        }
    }

    fun changeToNormalView() {
        editMode = false
        listOfItemSelected.clear()
        notifyItemRangeChanged(0, differ.currentList.size, PAYLOAD_CHECK)
    }

}

//private fun getThumbnail(path: String): Bitmap? {
//    return try {
//        val mr = MediaMetadataRetriever()
//        mr.setDataSource(path)
//        val byte1 = mr.embeddedPicture
//        mr.release()
//        if (byte1 != null) BitmapFactory.decodeByteArray(byte1, 0, byte1.size)
//        else null
//    } catch (e: java.lang.Exception) {
//        null
//    }
//}

private fun getImageForItemFile(item: FileVaultItem): Int {
    return when (item.fileRealType) {
        Constant.TYPE_WORDX -> R.drawable.ic_docx
        Constant.TYPE_WORD -> R.drawable.ic_doc
        Constant.TYPE_CSV -> R.drawable.ic_csv
        Constant.TYPE_PPT -> R.drawable.ic_ppt
        Constant.TYPE_TEXT -> R.drawable.ic_txt
        Constant.TYPE_ZIP -> R.drawable.ic_zip
        Constant.TYPE_PPTX -> R.drawable.ic_pptx
        Constant.TYPE_EXCEL -> R.drawable.ic_excel
        Constant.TYPE_PDF -> R.drawable.ic_pdf
        else -> R.drawable.ic_file_unknow
    }
}


private fun showPopupWindow(
    context: Context,
    view: View,
    item: FileVaultItem,
    onClickItem: (FileVaultItem) -> Unit,
    onDeleteItem: (FileVaultItem) -> Unit,
    onOpenDetail: (FileVaultItem) -> Unit
) {
    val inflater: LayoutInflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
    val bindingLayout = LayoutPersistentItemOptionMenuBinding.inflate(inflater, null, false)

    val popupWindow = PopupWindow(
        bindingLayout.root,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        true
    )
    bindingLayout.root.clickWithDebounce {
        popupWindow.dismiss()
    }
    bindingLayout.containerPlay.clickWithDebounce {
        onClickItem(item)
        popupWindow.dismiss()
    }
    bindingLayout.containerDelete.clickWithDebounce {
        onDeleteItem(item)
        popupWindow.dismiss()
    }
    bindingLayout.containerInfo.clickWithDebounce {
        onOpenDetail(item)
        popupWindow.dismiss()
    }
    val values = IntArray(2)
    view.getLocationInWindow(values)
    val positionOfIcon = values[1]
    val displayMetrics: DisplayMetrics = view.context.resources.displayMetrics
    val height = displayMetrics.heightPixels * 2 / 3

    if (positionOfIcon < height) {
        popupWindow.showAsDropDown(
            view, 0, -view.height + popupWindow.height
        )
    } else {
        popupWindow.showAsDropDown(
            view, 0, -400
        )
    }
}

private fun showPopupWindowFile(
    context: Context,
    view: View,
    item: FileVaultItem,
    onClickItem: (FileVaultItem) -> Unit,
    onDeleteItem: (FileVaultItem) -> Unit,
    onOpenDetail: (FileVaultItem) -> Unit
) {
    val inflater: LayoutInflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
    val bindingLayout = LayoutPersistentItemFileOptionMenuBinding.inflate(inflater, null, false)

    val popupWindow = PopupWindow(
        bindingLayout.root,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        true
    )
    bindingLayout.root.clickWithDebounce {
        popupWindow.dismiss()
    }

    bindingLayout.containerOpen.clickWithDebounce {
        onClickItem(item)
        popupWindow.dismiss()
    }
    bindingLayout.containerDelete.clickWithDebounce {
        onDeleteItem(item)
        popupWindow.dismiss()
    }
    bindingLayout.containerInfo.clickWithDebounce {
        onOpenDetail(item)
        popupWindow.dismiss()
    }
    val values = IntArray(2)
    view.getLocationInWindow(values)
    val positionOfIcon = values[1]
    val displayMetrics: DisplayMetrics = view.context.resources.displayMetrics
    val height = displayMetrics.heightPixels * 2 / 3

    if (positionOfIcon < height) {
        popupWindow.showAsDropDown(
            view, 0, -view.height + popupWindow.height
        )
    } else {
        popupWindow.showAsDropDown(
            view, 0, -400
        )
    }
}

