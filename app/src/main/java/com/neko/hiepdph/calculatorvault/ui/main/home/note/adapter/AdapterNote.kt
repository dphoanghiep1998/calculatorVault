package com.neko.hiepdph.calculatorvault.ui.main.home.note.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.common.extensions.hide
import com.neko.hiepdph.calculatorvault.common.extensions.show
import com.neko.hiepdph.calculatorvault.common.utils.DateTimeUtils
import com.neko.hiepdph.calculatorvault.data.model.NoteModel
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemNoteBinding

class AdapterNote(
    private val onLongClickItem: () -> Unit,
    private val onClickItem: (NoteModel) -> Unit,
    private val onEditItem: (List<Int>) -> Unit,
    private val onSelectAll: (List<Int>) -> Unit,

    private val onUnSelect: () -> Unit,

    ) : RecyclerView.Adapter<AdapterNote.NoteViewHolder>() {
    private var listOfNote: MutableList<NoteModel> = mutableListOf()
    private var dataOfNote: MutableList<NoteModel> = mutableListOf()
    private var listOfId = mutableSetOf<Int>()

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NoteModel>() {

        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun setData(listNote: List<NoteModel>) {
        differ.submitList(listNote)
        listOfNote.clear()
        listOfNote.addAll(listNote)
    }

    fun selectAll() {
        differ.currentList.forEach {
            listOfId.add(it.id)
        }
        onSelectAll.invoke(listOfId.toMutableList())
        notifyDataSetChanged()
    }

    fun unSelectAll() {
        listOfId.clear()
        onUnSelect()
        notifyDataSetChanged()
    }

    companion object {
        var isSwitchView = false
        var editMode = false
    }

    inner class NoteViewHolder(val binding: LayoutItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoteModel) {
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
            binding.tvDate.text = DateTimeUtils.getDateConverted(item.date)
            binding.checkBox.isChecked = item.id in listOfId
            if (editMode) {
                binding.checkBox.show()
            } else {
                binding.checkBox.hide()
            }
            binding.root.setOnLongClickListener {
                editMode = true
                onLongClickItem()
                notifyItemRangeChanged(0, differ.currentList.size)
                return@setOnLongClickListener true
            }
            binding.root.setOnClickListener {
                if (!editMode) {
                    onClickItem.invoke(item)
                } else {
                    binding.checkBox.isChecked = !binding.checkBox.isChecked
                    if (binding.checkBox.isChecked) {
                        listOfId.add(item.id)
                    } else {
                        listOfId.remove(item.id)
                    }
                    onEditItem(listOfId.toMutableList())
                }
            }

            binding.checkBox.setOnClickListener {
                if (binding.checkBox.isChecked) {
                    listOfId.add(item.id)
                } else {
                    listOfId.remove(item.id)
                }
                onEditItem(listOfId.toMutableList())
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding =
            LayoutItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        with(holder) {
            bind(differ.currentList[position])
        }
    }

    fun changeToNormalView() {
        editMode = false
        listOfId.clear()
        notifyDataSetChanged()
    }

    fun filterNote(query: String) {
        if (query.isEmpty()) {
            differ.submitList(listOfNote)
        } else {
            val filterList = listOfNote.filter {
                it.title.lowercase().contains(query) || it.content.lowercase().contains(query)
            }
            differ.submitList(filterList)
        }

    }
}

class NoteDiffCallback(var oldList: List<NoteModel>, var newList: List<NoteModel>) :
    DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].id == oldList[oldItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == (oldList[oldItemPosition])
    }


}
