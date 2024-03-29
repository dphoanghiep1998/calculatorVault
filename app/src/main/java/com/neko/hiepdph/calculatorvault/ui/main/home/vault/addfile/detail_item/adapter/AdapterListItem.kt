package com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
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


class AdapterListItem(
    private val onClickItem: (MutableSet<FileVaultItem>) -> Unit,
    private val onSelectAll: (MutableSet<FileVaultItem>) -> Unit,
    private val type: String = Constant.TYPE_PICTURE
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItemSelected = mutableSetOf<FileVaultItem>()

    companion object {
        const val PAYLOAD_CHECK = "PAYLOAD_CHECK"
    }
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
        listItemSelected.clear()
        listItemSelected.addAll(differ.currentList)
        onSelectAll(listItemSelected)
        notifyItemRangeChanged(0, differ.currentList.size, PAYLOAD_CHECK)
    }

    fun unSelectAll() {
        listItemSelected.clear()
        onSelectAll(listItemSelected)
        notifyItemRangeChanged(0, differ.currentList.size,PAYLOAD_CHECK)
    }

    inner class ItemPictureViewHolder(val binding: LayoutItemPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item:FileVaultItem) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
            binding.checkBox.isChecked = item in listItemSelected
            Glide.with(itemView.context).load(item.originalPath).apply(requestOptions)
                .error(R.drawable.ic_error_image).into(binding.imvThumb)
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

    inner class ItemVideoViewHolder(val binding: LayoutItemVideosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileVaultItem) {
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

    inner class ItemAudioViewHolder(val binding: LayoutItemAudiosBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileVaultItem) {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
            Glide.with(itemView.context).load(loadThumbnail(item.originalPath))
                .apply(requestOptions).error(R.drawable.ic_error_audio).into(binding.imvThumb)
            binding.checkBox.isChecked = item in listItemSelected
            binding.tvNameAudio.isSelected = true
            binding.tvNameAudio.text = item.name
            binding.tvDurationAuthor.text =
                item.durationLength?.getFormattedDuration() + "-" + item.artist
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

    inner class ItemFileViewHolder(val binding: LayoutItemFileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item:FileVaultItem) {
            binding.checkBox.isChecked = item in listItemSelected
            binding.tvNameDocument.isSelected = true
            Glide.with(itemView.context).load(getImageForItemFile(item))
                .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
            binding.tvNameDocument.text = item.name
            binding.tvSize.text = item.size.formatSize()
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

            else-> {
                val binding = LayoutItemFileBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return ItemFileViewHolder(binding)
            }

        }
    }

    override fun getItemViewType(position: Int): Int = when (type) {
        Constant.TYPE_PICTURE -> 0
        Constant.TYPE_VIDEOS -> 1
        Constant.TYPE_AUDIOS -> 2
        Constant.TYPE_FILE -> 3
        else -> 4
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            for (payload in payloads) {

                if (payload == PAYLOAD_CHECK) {
                    val item = differ.currentList[position]
                    when (holder.itemViewType) {
                        0 -> {
                            with(holder as ItemPictureViewHolder) {
                                binding.checkBox.isChecked = item in listItemSelected
                            }
                        }

                        1 -> {
                            with(holder as ItemVideoViewHolder) {
                                binding.checkBox.isChecked = item in listItemSelected
                            }
                        }

                        2 -> {
                            with(holder as ItemAudioViewHolder) {
                                binding.checkBox.isChecked = item in listItemSelected
                            }
                        }

                        3 -> {
                            with(holder as ItemFileViewHolder) {
                                binding.checkBox.isChecked = item in listItemSelected
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                (holder as ItemPictureViewHolder).bind(differ.currentList[position])
            }

            1 -> {
                (holder as ItemVideoViewHolder).bind(differ.currentList[position])

            }

            2 -> {
                (holder as ItemAudioViewHolder).bind(differ.currentList[position])

            }

            3 -> {
                (holder as ItemFileViewHolder).bind(differ.currentList[position])
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

    private fun loadThumbnail(path: String): Bitmap? {

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