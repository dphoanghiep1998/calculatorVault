package com.neko.hiepdph.calculatorvault.common.extensions

import androidx.recyclerview.widget.DiffUtil
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem

class ItemDiffCallback : DiffUtil.ItemCallback<FileVaultItem>() {
    override fun areItemsTheSame(oldItem: FileVaultItem, newItem: FileVaultItem): Boolean {
        return oldItem.mediaStoreId == newItem.mediaStoreId
    }

    override fun areContentsTheSame(oldItem: FileVaultItem, newItem: FileVaultItem): Boolean {
        return oldItem == newItem
    }

}