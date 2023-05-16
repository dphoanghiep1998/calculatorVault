package com.neko.hiepdph.calculatorvault.ui.main.home.setting.advance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemPersistentPictureBinding

class AdapterIntruder(private val onClickItem: (FileVaultItem) -> Unit) :
    RecyclerView.Adapter<AdapterIntruder.IntruderViewHolder>() {
    private var imageData = mutableListOf<FileVaultItem>()


    fun setData(data:List<FileVaultItem>){
        imageData.clear()
        imageData.addAll(data)
        notifyDataSetChanged()
    }

    inner class IntruderViewHolder(val binding: LayoutItemPersistentPictureBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntruderViewHolder {
        val binding = LayoutItemPersistentPictureBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return IntruderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return imageData.size
    }

    override fun onBindViewHolder(holder: IntruderViewHolder, position: Int) {
        with(holder) {
            val item = imageData[absoluteAdapterPosition]
            binding.checkBox.hide()
            Glide.with(itemView.context).load(item.encryptedPath).error(R.drawable.ic_error_image)
                .into(binding.imvThumb)
            binding.root.clickWithDebounce {
                onClickItem(item)
            }
        }
    }
}