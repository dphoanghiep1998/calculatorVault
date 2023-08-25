package com.neko.hiepdph.calculatorvault.ui.main.home.recyclebin.adapter

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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.ItemDiffCallback
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.*

class AdapterRecyclerBin(
    private val onClickItem: (FileVaultItem) -> Unit,
    private val onLongClickItem: (List<FileVaultItem>) -> Unit,
    private val onEditItem: (List<FileVaultItem>) -> Unit,
    private val onSelectAll: (List<FileVaultItem>) -> Unit,
    private val onRestoreItem: (FileVaultItem) -> Unit,
    private val onDeleteItem: (FileVaultItem) -> Unit,
    private val onDetailItem: (FileVaultItem) -> Unit,

    ) : ListAdapter<FileVaultItem, AdapterRecyclerBin.ItemFileViewHolder>(ItemDiffCallback()) {
    private var listItem = mutableListOf<FileVaultItem>()
    private var listOfItemSelected = mutableSetOf<FileVaultItem>()


    override fun submitList(list: MutableList<FileVaultItem>?) {
        super.submitList(list)
        listItem = list?.toMutableList() ?: mutableListOf()

    }


    @SuppressLint("NotifyDataSetChanged")
    fun selectAll() {
        listItem.forEach {
            listOfItemSelected.add(it)
        }
        onSelectAll.invoke(listOfItemSelected.toMutableList())
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun unSelectAll() {
        listOfItemSelected.clear()
        onSelectAll.invoke(listOfItemSelected.toMutableList())
        notifyItemRangeChanged(0, listItem.size, PAYLOAD_CHECK)
    }


    companion object {
        var editMode = false
        private const val PAYLOAD_CHECK = "PAYLOAD_CHECK"

    }

    fun changeToEditView() {
        editMode = true
        notifyItemRangeChanged(0, listItem.size, PAYLOAD_CHECK)
    }


    inner class ItemFileViewHolder(val binding: LayoutItemRecyclerBinFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = listItem[position]

            binding.tvNameDocument.isSelected = true
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
            binding.checkBox.isChecked = item in listOfItemSelected
            when (item.fileType) {
                Constant.TYPE_PICTURE -> {
                    Glide.with(itemView.context).load(item.thumb).centerCrop()
                        .apply(requestOptions).error(R.drawable.ic_error_image)
                        .into(binding.imvThumb)

                    binding.option.clickWithDebounce {
                        showPopupWindow(itemView.context, binding.option, item)
                    }
                }

                Constant.TYPE_AUDIOS -> {
                    Glide.with(itemView.context).asBitmap().load(item.thumb)
                        .apply(requestOptions).centerCrop().error(R.drawable.ic_error_audio)
                        .into(binding.imvThumb)

                    binding.option.clickWithDebounce {
                        showPopupWindow(itemView.context, binding.option, item)
                    }
                }

                Constant.TYPE_VIDEOS -> {
                    Glide.with(itemView.context).load(item.thumb).centerCrop()
                        .apply(requestOptions).error(R.drawable.ic_error_video)
                        .into(binding.imvThumb)

                    binding.option.clickWithDebounce {
                        showPopupWindow(itemView.context, binding.option, item)
                    }
                }

                else -> {
                    Glide.with(itemView.context).load(
                        getImageForItemFile(
                            item
                        )
                    ).error(R.drawable.ic_file_unknow).apply(requestOptions).into(binding.imvThumb)

                    binding.option.clickWithDebounce {
                        showPopupWindow(
                            itemView.context, binding.option, item
                        )
                    }
                }
            }
            binding.checkBox.isChecked = item in listOfItemSelected


            if (editMode) {
                binding.checkBox.show()
                binding.option.hide()
            } else {
                binding.checkBox.hide()
                binding.option.show()
            }

            binding.tvNameDocument.text = item.name

            binding.tvTime.text = item.size.formatSize()


            binding.root.setOnLongClickListener {
                if (editMode) {
                    return@setOnLongClickListener false
                }
                editMode = true
                listOfItemSelected.add(item)
                onLongClickItem(listOfItemSelected.toMutableList())
                notifyItemRangeChanged(0, listItem.size, PAYLOAD_CHECK)

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFileViewHolder {
        val binding = LayoutItemRecyclerBinFileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemFileViewHolder(binding)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeToNormalView() {
        editMode = false
        listOfItemSelected.clear()
        onSelectAll.invoke(listOfItemSelected.toMutableList())

        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int = when (listItem[position].fileType) {
        Constant.TYPE_PICTURE -> 0
        Constant.TYPE_VIDEOS -> 1
        Constant.TYPE_AUDIOS -> 2
        else -> 3
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(
        holder: ItemFileViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val item = getItem(position)
            holder.binding.checkBox.isChecked = item in listOfItemSelected
            if (editMode) {
                holder.binding.checkBox.show()
                holder.binding.option.hide()
            } else {
                holder.binding.checkBox.hide()
                holder.binding.option.show()
            }
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemFileViewHolder, position: Int) {
        holder.bind(position)
    }


    private fun showPopupWindow(context: Context, view: View, item: FileVaultItem) {
        val inflater: LayoutInflater =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
        val bindingLayout = LayoutRecyclerbinOptionMenuBinding.inflate(inflater, null, false)

        val popupWindow = PopupWindow(
            bindingLayout.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        bindingLayout.root.clickWithDebounce {
            popupWindow.dismiss()
        }
        bindingLayout.containerRestore.clickWithDebounce {
            onRestoreItem(item)
            popupWindow.dismiss()
        }
        bindingLayout.containerDelete.clickWithDebounce {
            onDeleteItem(item)
            popupWindow.dismiss()
        }
        bindingLayout.containerInfo.clickWithDebounce {
            onDetailItem(item)
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



