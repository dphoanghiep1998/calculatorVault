package com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.getFormattedDuration
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemAudiosBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemFileBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPictureBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemVideosBinding


class AdapterListItem(private val onClickItem: (MutableSet<FileVaultItem>) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItem = mutableListOf<FileVaultItem>()
    private var mType: String = Constant.TYPE_PICTURE
    private var listItemSelected = mutableSetOf<FileVaultItem>()

    fun setData(listDataItem: List<FileVaultItem>, type: String) {
        mType = type
        listItem = listDataItem.toMutableList()
        listItemSelected.clear()
        notifyDataSetChanged()
    }

    fun selectAll() {
        listItemSelected.clear()
        listItemSelected.addAll(listItem)
        notifyDataSetChanged()
    }

    fun unSelectAll() {
        listItemSelected.clear()
        notifyDataSetChanged()
    }

    inner class ItemPictureViewHolder(val binding: LayoutItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemVideoViewHolder(val binding: LayoutItemVideosBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemAudioViewHolder(val binding: LayoutItemAudiosBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ItemFileViewHolder(val binding: LayoutItemFileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val binding = LayoutItemPictureBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemPictureViewHolder(binding)
            }
            1 -> {
                val binding = LayoutItemVideosBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemVideoViewHolder(binding)
            }
            2 -> {
                val binding = LayoutItemAudiosBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemAudioViewHolder(binding)
            }

            3 -> {
                val binding = LayoutItemFileBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemFileViewHolder(binding)
            }
            else -> {
                val binding = LayoutItemFileBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemFileViewHolder(binding)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                with(holder as ItemPictureViewHolder) {
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(item.originalPath).apply(requestOptions)
                        .error(R.drawable.ic_error_image).into(binding.imvThumb)
                    binding.checkBox.isChecked = item in listItemSelected

                    binding.root.setOnClickListener {
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listItemSelected.add(item)
                        } else {
                            listItemSelected.remove(item)
                        }
                        onClickItem.invoke(listItemSelected)
                    }
                    binding.checkBox.setOnClickListener {
                        if (binding.checkBox.isChecked) {
                            listItemSelected.add(item)
                        } else {
                            listItemSelected.remove(item)
                        }
                        onClickItem.invoke(listItemSelected)
                    }
                }
            }
            1 -> {
                with(holder as ItemVideoViewHolder) {
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(item.originalPath).apply(requestOptions)
                        .error(R.drawable.ic_error_video).into(binding.imvThumb)
                    binding.tvDuration.text = item.durationLength?.getFormattedDuration()
                    binding.checkBox.isChecked = item in listItemSelected

                    binding.root.setOnClickListener {
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listItemSelected.add(item)
                        } else {
                            listItemSelected.remove(item)
                        }
                        onClickItem.invoke(listItemSelected)
                    }
                    binding.checkBox.setOnClickListener {
                        if (binding.checkBox.isChecked) {
                            listItemSelected.add(item)
                        } else {
                            listItemSelected.remove(item)
                        }
                        onClickItem.invoke(listItemSelected)
                    }
                }
            }
            2 -> {
                with(holder as ItemAudioViewHolder) {
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(loadThumbnail(item.originalPath))
                        .apply(requestOptions).error(R.drawable.ic_error_audio)
                        .into(binding.imvThumb)

                    binding.tvNameAudio.isSelected = true

                    binding.tvNameAudio.text = item.name
                    binding.checkbox.isChecked = item in listItemSelected
                    binding.tvDurationAuthor.text =
                        item.durationLength?.getFormattedDuration() + "-" + item.artist
                    binding.root.setOnClickListener {
                        binding.checkbox.isChecked = !binding.checkbox.isChecked
                        if (binding.checkbox.isChecked) {
                            listItemSelected.add(item)
                        } else {
                            listItemSelected.remove(item)
                        }
                        onClickItem.invoke(listItemSelected)
                    }

                    binding.checkbox.setOnClickListener {
                        if (binding.checkbox.isChecked) {
                            listItemSelected.add(item)
                        } else {
                            listItemSelected.remove(item)
                        }
                        onClickItem.invoke(listItemSelected)
                    }
                }
            }
            3 -> {
                with(holder as ItemFileViewHolder) {
                    val item = listItem[adapterPosition]

                    binding.tvNameDocument.isSelected = true
                    Glide.with(itemView.context).load(getImageForItemFile(item))
                        .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
                    binding.checkbox.isChecked = item in listItemSelected

                    binding.tvNameDocument.text = item.name
                    binding.tvSize.text = item.size.formatSize()
                    binding.root.setOnClickListener {
                        binding.checkbox.isChecked = !binding.checkbox.isChecked
                        if (binding.checkbox.isChecked) {
                            listItemSelected.add(item)
                        } else {
                            listItemSelected.remove(item)
                        }
                        onClickItem.invoke(listItemSelected)
                    }
                    binding.checkbox.setOnClickListener {
                        if (binding.checkbox.isChecked) {
                            listItemSelected.add(item)
                        } else {
                            listItemSelected.remove(item)
                        }
                        onClickItem.invoke(listItemSelected)
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
            else -> 0
        }
    }

    private fun loadThumbnail(path:String): Bitmap? {

        return try {
            val mr = MediaMetadataRetriever()
            mr.setDataSource(path)
            val byte1 = mr.embeddedPicture
            mr.release()

            if (byte1 != null) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = false
                val b = BitmapFactory.decodeByteArray(byte1, 0, byte1.size, options)
                b
            } else null
        } catch (e: java.lang.Exception) {
            null
        }
    }
}