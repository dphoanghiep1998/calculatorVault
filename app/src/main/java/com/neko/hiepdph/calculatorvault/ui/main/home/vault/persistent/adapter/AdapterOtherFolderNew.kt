package com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.DisplayMetrics
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
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPersistentFileBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutPersistentItemFileOptionMenuBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutPersistentItemOptionMenuBinding


class AdapterOtherFolderNew(
    private val onClickItem: (FileVaultItem, Int) -> Unit,
    private val onLongClickItem: (List<FileVaultItem>) -> Unit,
    private val onEditItem: (List<FileVaultItem>) -> Unit,
    private val onSelectAll: (List<FileVaultItem>) -> Unit,
    private val onOpenDetail: (FileVaultItem) -> Unit,
    private val onDeleteItem: (FileVaultItem) -> Unit

) : RecyclerView.Adapter<AdapterOtherFolderNew.ItemFileViewHolder>() {
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

    fun submitList(list: MutableList<FileVaultItem>?) {
        differ.submitList(list)
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


    companion object {
        var editMode = false
        private const val PAYLOAD_CHECK = "PAYLOAD_CHECK"

    }

    private fun showCheckboxAll() {
        notifyItemRangeChanged(0, differ.currentList.size, PAYLOAD_CHECK)
    }


    inner class ItemFileViewHolder(val binding: LayoutItemPersistentFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileVaultItem) {
            binding.tvNameDocument.isSelected = true
            when (item.fileType) {
                Constant.TYPE_PICTURE -> {
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(item.encryptedPath).apply(requestOptions)
                        .error(R.drawable.ic_error_image).into(binding.imvThumb)
                    binding.checkBox.isChecked = item in listOfItemSelected

                    binding.option.clickWithDebounce {
                        showPopupWindowFile(
                            itemView.context,
                            binding.option,
                            item,
                            onClickItem,
                            onDeleteItem,
                            onOpenDetail,
                            absoluteAdapterPosition
                        )
                    }
                }

                Constant.TYPE_AUDIOS -> {
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).asBitmap().load(getThumbnail(item.encryptedPath))
                        .apply(requestOptions).error(R.drawable.ic_error_audio)
                        .into(binding.imvThumb)

                    binding.option.clickWithDebounce {
                        showPopupWindow(
                            itemView.context,
                            binding.option,
                            item,
                            onClickItem,
                            onDeleteItem,
                            onOpenDetail,
                            absoluteAdapterPosition
                        )
                    }
                }

                Constant.TYPE_VIDEOS -> {
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(item.encryptedPath).apply(requestOptions)
                        .error(R.drawable.ic_error_video).into(binding.imvThumb)

                    binding.option.clickWithDebounce {
                        showPopupWindowFile(
                            itemView.context,
                            binding.option,
                            item,
                            onClickItem,
                            onDeleteItem,
                            onOpenDetail,
                            absoluteAdapterPosition
                        )
                    }
                }

                else -> {
                    Glide.with(itemView.context).load(
                        getImageForItemFile(
                            item
                        )
                    ).error(R.drawable.ic_file_unknow).into(binding.imvThumb)

                    binding.option.clickWithDebounce {
                        showPopupWindowFile(
                            itemView.context,
                            binding.option,
                            item,
                            onClickItem,
                            onDeleteItem,
                            onOpenDetail,
                            absoluteAdapterPosition
                        )
                    }
                }
            }
            binding.tvNameDocument.text = item.name
            binding.tvSize.text = item.size.formatSize()
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
                    onClickItem.invoke(item, absoluteAdapterPosition)
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFileViewHolder {
        val binding = LayoutItemPersistentFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemFileViewHolder(binding)

    }

    fun changeToNormalView() {
        editMode = false
        listOfItemSelected.clear()
        notifyItemRangeChanged(0, differ.currentList.size, PAYLOAD_CHECK)
    }


    override fun getItemViewType(position: Int): Int =
        when (differ.currentList[position].fileType) {
            Constant.TYPE_PICTURE -> 0
            Constant.TYPE_VIDEOS -> 1
            Constant.TYPE_AUDIOS -> 2
            else -> 3
        }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemFileViewHolder, position: Int) {
        with(holder) {
            bind(differ.currentList[position])
        }
    }

    override fun onBindViewHolder(
        holder: ItemFileViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            for (payload in payloads) {
                if (payload == PAYLOAD_CHECK) {
                    val item = differ.currentList[position]
                    with(holder) {
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

private fun getThumbnail(path: String): Bitmap? {
    return try {
        val mr = MediaMetadataRetriever()
        mr.setDataSource(path)
        val byte1 = mr.embeddedPicture
        mr.release()
        if (byte1 != null) BitmapFactory.decodeByteArray(byte1, 0, byte1.size)
        else null
    } catch (e: java.lang.Exception) {
        null
    }
}
fun showPopupWindowFile(
    context: Context,
    view: View,
    item: FileVaultItem,
    onClickItem: (FileVaultItem, Int) -> Unit,
    onDeleteItem: (FileVaultItem) -> Unit,
    onOpenDetail: (FileVaultItem) -> Unit,
    index: Int
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
        onClickItem(item, index)
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



