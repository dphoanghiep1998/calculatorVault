package com.neko.hiepdph.calculatorvault.ui.main.home.setting.advance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPersistentPictureBinding

class AdapterIntruder(private val onClickItem: (FileVaultItem) -> Unit) :
    RecyclerView.Adapter<AdapterIntruder.IntruderViewHolder>() {
    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FileVaultItem>() {

        override fun areItemsTheSame(oldItem: FileVaultItem, newItem: FileVaultItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FileVaultItem, newItem: FileVaultItem): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)
    fun setData(data: List<FileVaultItem>) {
        differ.submitList(data)
    }

    inner class IntruderViewHolder(val binding: LayoutItemPersistentPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FileVaultItem) {
            binding.checkBox.hide()
            Glide.with(itemView.context).load(item.encryptedPath).error(R.drawable.ic_error_image)
                .into(binding.imvThumb)
            binding.root.clickWithDebounce {
                onClickItem(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntruderViewHolder {
        val binding = LayoutItemPersistentPictureBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return IntruderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: IntruderViewHolder, position: Int) {
        with(holder) {
            bind(differ.currentList[position])
        }
    }
}