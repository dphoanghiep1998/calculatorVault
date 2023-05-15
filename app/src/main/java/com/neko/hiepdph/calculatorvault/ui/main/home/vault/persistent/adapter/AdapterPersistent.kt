package com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
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
import com.neko.hiepdph.calculatorvault.data.model.*
import com.neko.hiepdph.calculatorvault.databinding.*


class AdapterPersistent(
    private val onClickItem: (ListItem) -> Unit,
    private val onLongClickItem: (List<ListItem>) -> Unit,
    private val onEditItem: (List<ListItem>) -> Unit,
    private val onSelectAll: (List<ListItem>) -> Unit,
    private val onUnSelect: () -> Unit,
    private val onOpenDetail: (ListItem) -> Unit,
    private val onDeleteItem: (ListItem) -> Unit

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItem = mutableListOf<ListItem>()
    private var mType: String = Constant.TYPE_PICTURE
    private var listOfItemSelected = mutableSetOf<ListItem>()

    fun setData(listDataItem: List<ListItem>, type: String) {
        mType = type
        listItem = listDataItem.toMutableList()
        notifyDataSetChanged()
    }

    fun selectAll() {
        listItem.forEach {
            listOfItemSelected.add(it)
        }
        onSelectAll.invoke(listOfItemSelected.toMutableList())
        notifyDataSetChanged()
    }

    fun unSelectAll() {
        listOfItemSelected.clear()
        onUnSelect()
        notifyDataSetChanged()
    }


    companion object {
        var editMode = false
    }

    inner class ItemPictureViewHolder(val binding: LayoutItemPersistentPictureBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemVideoViewHolder(val binding: LayoutItemPersistentVideosBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemAudioViewHolder(val binding: LayoutItemPersistentAudiosBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemFileViewHolder(val binding: LayoutItemPersistentFileBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemOtherFileViewHolder(val binding: LayoutItemPersistentOtherFileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val binding = LayoutItemPersistentPictureBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemPictureViewHolder(binding)
            }
            1 -> {
                val binding = LayoutItemPersistentVideosBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemVideoViewHolder(binding)
            }
            2 -> {
                val binding = LayoutItemPersistentAudiosBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemAudioViewHolder(binding)
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

    fun changeToNormalView() {
        editMode = false
        listOfItemSelected.clear()
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int = when (mType) {
        Constant.TYPE_PICTURE -> 0
        Constant.TYPE_VIDEOS -> 1
        Constant.TYPE_AUDIOS -> 2
        Constant.TYPE_FILE -> 3
        else -> 4
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                with(holder as ItemPictureViewHolder) {
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(item.mPath)
                        .placeholder(R.drawable.ic_error_image).apply(requestOptions)
                        .into(binding.imvThumb)

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

                        notifyDataSetChanged()
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
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listOfItemSelected.add(item)
                        } else {
                            listOfItemSelected.remove(item)
                        }
                        onEditItem(listOfItemSelected.toMutableList())
                    }
                }
            }
            1 -> {
                with(holder as ItemVideoViewHolder) {
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(item.mPath).apply(requestOptions)
                        .error(R.drawable.ic_error_video).into(binding.imvThumb)

                    binding.checkBox.isChecked = item in listOfItemSelected

                    if (editMode) {
                        binding.checkBox.show()
                    } else {
                        binding.checkBox.hide()
                    }
                    binding.tvDuration.text = item.getDuration(itemView.context).toString()

                    binding.root.setOnLongClickListener {
                        if (editMode) {
                            return@setOnLongClickListener false
                        }
                        editMode = true
                        listOfItemSelected.add(item)
                        onLongClickItem(listOfItemSelected.toMutableList())

                        notifyDataSetChanged()
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
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listOfItemSelected.add(item)
                        } else {
                            listOfItemSelected.remove(item)
                        }
                        onEditItem(listOfItemSelected.toMutableList())
                    }
                }
            }
            2 -> {
                with(holder as ItemAudioViewHolder) {
                    binding.tvNameAudio.isSelected = true
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).asBitmap().load(item.thumb).apply(requestOptions)
                        .error(R.drawable.ic_error_audio).into(binding.imvThumb)
                    binding.checkBox.isChecked = item in listOfItemSelected

                    if (editMode) {
                        binding.option.hide()
                        binding.checkBox.show()
                    } else {
                        binding.option.show()
                        binding.checkBox.hide()
                    }
                    binding.tvNameAudio.text = item.mName
                    binding.tvDurationAuthor.text =
                        item.getDuration(itemView.context).toString() + " - " + item.getArtist(
                            itemView.context
                        )
                    binding.option.clickWithDebounce {
                        showPopupWindow(
                            itemView.context,
                            binding.option,
                            item,
                            onClickItem,
                            onDeleteItem,
                            onOpenDetail
                        )
                    }


                    binding.root.setOnLongClickListener {
                        if (editMode) {
                            return@setOnLongClickListener false
                        }
                        editMode = true
                        listOfItemSelected.add(item)
                        onLongClickItem(listOfItemSelected.toMutableList())

                        notifyDataSetChanged()

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
            3 -> {
                with(holder as ItemFileViewHolder) {
                    val item = listItem[adapterPosition]
                    Glide.with(itemView.context).load(getImageForItemFile(item))
                        .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
                    binding.checkBox.isChecked = item in listOfItemSelected
                    binding.tvNameDocument.isSelected = true

                    if (editMode) {
                        binding.checkBox.show()
                        binding.option.hide()
                    } else {
                        binding.checkBox.hide()
                        binding.option.show()
                    }
                    binding.tvNameDocument.text = item.mName
                    binding.tvSize.text = item.mSize.formatSize()
                    binding.option.clickWithDebounce {
                        showPopupWindowFile(
                            itemView.context,
                            binding.option,
                            item,
                            onClickItem,
                            onDeleteItem,
                            onOpenDetail
                        )
                    }

                    binding.root.setOnLongClickListener {
                        if (editMode) {
                            return@setOnLongClickListener false
                        }
                        editMode = true
                        listOfItemSelected.add(item)
                        onLongClickItem(listOfItemSelected.toMutableList())

                        notifyDataSetChanged()

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
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listOfItemSelected.add(item)
                        } else {
                            listOfItemSelected.remove(item)
                        }
                        onEditItem(listOfItemSelected.toMutableList())
                    }
                }
            }
            4 -> {
                with(holder as ItemOtherFileViewHolder) {
                    val item = listItem[adapterPosition]
                    Glide.with(itemView.context).load(getImageForItemFile(item))
                        .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
                    binding.checkBox.isChecked = item in listOfItemSelected

                    if (editMode) {
                        binding.checkBox.show()
                    } else {
                        binding.checkBox.hide()
                    }
                    binding.root.setOnLongClickListener {
                        if (editMode) {
                            return@setOnLongClickListener false
                        }
                        editMode = true
                        listOfItemSelected.add(item)
                        onLongClickItem(listOfItemSelected.toMutableList())

                        onLongClickItem(listOfItemSelected.toMutableList())
                        notifyDataSetChanged()
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
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listOfItemSelected.add(item)
                        } else {
                            listOfItemSelected.remove(item)
                        }
                        onEditItem(listOfItemSelected.toMutableList())
                    }
                }


            }
        }
    }
}


private fun getImageForItemFile(item: ListItem): Int {
    return when (item.realType) {
        Constant.TYPE_WORDX -> R.drawable.ic_docx
        Constant.TYPE_WORD -> R.drawable.ic_doc
        Constant.TYPE_CSV -> R.drawable.ic_csv
        Constant.TYPE_PPT -> R.drawable.ic_ppt
        Constant.TYPE_TEXT -> R.drawable.ic_txt
        Constant.TYPE_ZIP -> R.drawable.ic_zip
        Constant.TYPE_PPTX -> R.drawable.ic_pptx
        Constant.TYPE_EXCEL -> R.drawable.ic_excel
        Constant.TYPE_PDF -> R.drawable.ic_pdf
        else -> 0
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

private fun showPopupWindow(
    context: Context, view: View,
    item: ListItem,
    onClickItem: (ListItem) -> Unit,
    onDeleteItem: (ListItem) -> Unit,
    onOpenDetail: (ListItem) -> Unit
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
            view,
            0,
            -view.height + popupWindow.height
        )
    }else{
        popupWindow.showAsDropDown(
            view,
            0,
            -400
        )
    }
}

private fun showPopupWindowFile(
    context: Context,
    view: View,
    item: ListItem,
    onClickItem: (ListItem) -> Unit,
    onDeleteItem: (ListItem) -> Unit,
    onOpenDetail: (ListItem) -> Unit
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
            view,
            0,
            -view.height + popupWindow.height
        )
    }else{
        popupWindow.showAsDropDown(
            view,
            0,
            -400
        )
    }


}