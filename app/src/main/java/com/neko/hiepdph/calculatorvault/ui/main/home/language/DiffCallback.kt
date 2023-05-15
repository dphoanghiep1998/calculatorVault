package com.neko.hiepdph.calculatorvault.ui.main.home.language

import androidx.recyclerview.widget.DiffUtil

import java.util.Locale

class DiffCallback(
    private val oldList: List<Any>, var newList: List<Any>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem is Locale && newItem is Locale && oldItem.language == newItem.language
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem


    }
}