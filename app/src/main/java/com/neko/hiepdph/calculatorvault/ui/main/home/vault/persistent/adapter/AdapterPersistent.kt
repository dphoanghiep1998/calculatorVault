package com.neko.hiepdph.calculatorvault.ui.main.home.vault.persistent.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.enums.Order
import com.neko.hiepdph.calculatorvault.common.enums.Sort
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.data.model.*
import com.neko.hiepdph.calculatorvault.databinding.*
import com.neko.hiepdph.calculatorvault.dialog.DialogSort
import com.neko.hiepdph.calculatorvault.dialog.SortDialogCallBack
import com.neko.hiepdph.calculatorvault.ui.main.home.language.adapter.AdapterFolder
import com.neko.hiepdph.calculatorvault.ui.main.home.vault.FragmentVault


class AdapterPersistent(private val onClickItem: (ListItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItem = mutableListOf<ListItem>()
    private var mType: String = Constant.TYPE_PICTURE

    fun setData(listDataItem: List<ListItem>, type: String) {
        mType = type
        listItem = listDataItem.toMutableList()
        Log.d("TAG", "setData: " + listItem.size)
        notifyDataSetChanged()
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
                    Glide.with(itemView.context).load(item.mPath).apply(requestOptions)
                        .error(R.drawable.ic_delete).into(binding.imvThumb)
                }
            }
            1 -> {
                with(holder as ItemVideoViewHolder) {
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(item.mPath).apply(requestOptions)
                        .error(R.drawable.ic_delete).into(binding.imvThumb)
                    binding.tvDuration.text = item.getDuration(itemView.context).toString()
                }
            }
            2 -> {
                with(holder as ItemAudioViewHolder) {
                    binding.tvNameAudio.isSelected = true
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).asBitmap().load(getThumbnail(item.mPath))
                        .apply(requestOptions).error(R.drawable.ic_delete).into(binding.imvThumb)
                    binding.tvNameAudio.text = item.mName
                    binding.tvDurationAuthor.text =
                        item.getDuration(itemView.context).toString() + " - " + item.getArtist(
                            itemView.context
                        )
                    binding.option.clickWithDebounce {
                        showPopupWindow(itemView.context, binding.option)
                    }
                }
            }
            3 -> {
                with(holder as ItemFileViewHolder) {
                    val item = listItem[adapterPosition]
                    Glide.with(itemView.context).load(getImageForItemFile(item))
                        .error(R.drawable.ic_delete).into(binding.imvThumb)
                    binding.tvNameDocument.text = item.mName
                    binding.tvSize.text = item.mSize.formatSize()
                    binding.option.clickWithDebounce {
                        showPopupWindowFile(itemView.context, binding.option)
                    }
                }
            }
            4 -> {
                with(holder as ItemOtherFileViewHolder) {
                    val item = listItem[adapterPosition]
                    Glide.with(itemView.context).load(getImageForItemFile(item))
                        .error(R.drawable.ic_delete).into(binding.imvThumb)
                }
            }
        }
    }
}

private fun getImageForItemFile(item: ListItem): Int {
    return when (item.type) {
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

private fun showPopupWindow(context: Context, view: View) {
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

    bindingLayout.root.clickWithDebounce {
        popupWindow.dismiss()
    }
    bindingLayout.containerPlay.clickWithDebounce {
        popupWindow.dismiss()
    }
    bindingLayout.containerDelete.clickWithDebounce {
        popupWindow.dismiss()
    }
    bindingLayout.containerInfo.clickWithDebounce {
        popupWindow.dismiss()
    }
    popupWindow.showAsDropDown(view)
}

private fun showPopupWindowFile(context: Context, view: View) {
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

    bindingLayout.root.clickWithDebounce {
        popupWindow.dismiss()
    }
    bindingLayout.containerOpen.clickWithDebounce {
        popupWindow.dismiss()
    }
    bindingLayout.containerDelete.clickWithDebounce {
        popupWindow.dismiss()
    }
    bindingLayout.containerInfo.clickWithDebounce {
        popupWindow.dismiss()
    }
    popupWindow.showAsDropDown(view)


}