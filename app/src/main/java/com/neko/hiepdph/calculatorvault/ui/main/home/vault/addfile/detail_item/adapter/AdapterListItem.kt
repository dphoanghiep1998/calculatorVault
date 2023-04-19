package com.neko.hiepdph.calculatorvault.ui.main.home.vault.addfile.detail_item.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.utils.formatSize
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemAudiosBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemFileBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPictureBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemVideosBinding


class AdapterListItem(private val onClickItem: (MutableSet<String>) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listItem = mutableListOf<ListItem>()
    private var mType: String = Constant.TYPE_PICTURE
    private var listPath = mutableSetOf<String>()

    fun setData(listDataItem: List<ListItem>, type: String) {
        mType = type
        listItem = listDataItem.toMutableList()
        notifyDataSetChanged()
    }

    fun selectAll() {
        listItem.forEach {
            listPath.add(it.mPath)
        }
        notifyItemRangeChanged(0, listItem.size)
    }

    fun unSelectAll() {
        listPath.clear()
        notifyItemRangeChanged(0, listItem.size)
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
                    Glide.with(itemView.context).load(item.mPath).apply(requestOptions)
                        .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
                    binding.checkBox.isChecked = item.mPath in listPath
                    binding.root.setOnClickListener {
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listPath.add(item.path)
                        } else {
                            listPath.remove(item.path)
                        }
                        onClickItem.invoke(listPath)
                    }
                    binding.checkBox.setOnClickListener {
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listPath.add(item.path)
                        } else {
                            listPath.remove(item.path)
                        }
                        onClickItem.invoke(listPath)
                    }
                }
            }
            1 -> {
                with(holder as ItemVideoViewHolder) {
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).load(item.path).apply(requestOptions)
                        .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
                    binding.tvDuration.text = item.getDuration(itemView.context).toString()
                    binding.checkBox.isChecked = item.mPath in listPath

                    binding.root.setOnClickListener {
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listPath.add(item.path)
                        } else {
                            listPath.remove(item.path)
                        }
                        onClickItem.invoke(listPath)
                    }
                    binding.checkBox.setOnClickListener {
                        binding.checkBox.isChecked = !binding.checkBox.isChecked
                        if (binding.checkBox.isChecked) {
                            listPath.add(item.path)
                        } else {
                            listPath.remove(item.path)
                        }
                        onClickItem.invoke(listPath)
                    }
                }
            }
            2 -> {
                with(holder as ItemAudioViewHolder) {
                    val item = listItem[adapterPosition]
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(10))
                    Glide.with(itemView.context).asBitmap().load(item.getThumb())
                        .apply(requestOptions).error(R.drawable.ic_file_unknow).into(binding.imvThumb)

                    binding.tvNameAudio.isSelected = true

                    binding.tvNameAudio.text = item.name
                    binding.checkbox.isChecked = item.mPath in listPath
                    binding.tvDurationAuthor.text = item.getDuration(itemView.context).toString()
                    binding.root.setOnClickListener {
                        binding.checkbox.isChecked = !binding.checkbox.isChecked
                        if (binding.checkbox.isChecked) {
                            listPath.add(item.path)
                        } else {
                            listPath.remove(item.path)
                        }
                        onClickItem.invoke(listPath)
                    }

                    binding.checkbox.setOnClickListener {
                        binding.checkbox.isChecked = !binding.checkbox.isChecked
                        if (binding.checkbox.isChecked) {
                            listPath.add(item.path)
                        } else {
                            listPath.remove(item.path)
                        }
                        onClickItem.invoke(listPath)
                    }
                }
            }
            3 -> {
                with(holder as ItemFileViewHolder) {
                    val item = listItem[adapterPosition]

                    binding.tvNameDocument.isSelected = true
                    Glide.with(itemView.context).load(getImageForItemFile(item))
                        .error(R.drawable.ic_file_unknow).into(binding.imvThumb)
                    binding.checkbox.isChecked = item.mPath in listPath

                    binding.tvNameDocument.text = item.name
                    binding.tvSize.text = item.mSize.formatSize()
                    binding.root.setOnClickListener {
                        binding.checkbox.isChecked = !binding.checkbox.isChecked
                        if (binding.checkbox.isChecked) {
                            listPath.add(item.path)
                        } else {
                            listPath.remove(item.path)
                        }
                        onClickItem.invoke(listPath)
                    }
                    binding.checkbox.setOnClickListener {
                        binding.checkbox.isChecked = !binding.checkbox.isChecked
                        if (binding.checkbox.isChecked) {
                            listPath.add(item.path)
                        } else {
                            listPath.remove(item.path)
                        }
                        onClickItem.invoke(listPath)
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
}