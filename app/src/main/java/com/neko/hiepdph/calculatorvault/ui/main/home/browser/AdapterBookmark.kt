package com.neko.hiepdph.calculatorvault.ui.main.home.browser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.data.database.model.BookmarkModel
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemBookmarkBinding

class AdapterBookmark(
    private val onClickItem: (url: String) -> Unit,
    private val onLongClickItem: (itemId: Int) -> Unit
) : RecyclerView.Adapter<AdapterBookmark.BookmarkViewHolder>() {
    private var listBookmark = mutableListOf<BookmarkModel>()
    fun setData(list: List<BookmarkModel>) {
        listBookmark.clear()
        listBookmark.addAll(list)
        notifyDataSetChanged()
    }

    inner class BookmarkViewHolder(val binding: LayoutItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding =
            LayoutItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listBookmark.size
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        with(holder) {
            val item = listBookmark[adapterPosition]
            if (item.persistent) {
                when (item.url) {
                    Constant.GOOGLE -> binding.imvIcon.setImageResource(R.drawable.ic_google)
                    Constant.QWANT -> binding.imvIcon.setImageResource(R.drawable.ic_qwant)
                    Constant.SEARCH_ENCRYPT -> binding.imvIcon.setImageResource(R.drawable.ic_search_encrypt)
                    Constant.DUCKDUCKGO -> binding.imvIcon.setImageResource(R.drawable.ic_duckduckgo)
                }
            } else {
                Glide.with(itemView.context).load(item.imageIcon).error(R.drawable.ic_global)
                    .into(binding.imvIcon)

            }
            binding.tvName.text = item.name
            binding.root.clickWithDebounce {
                onClickItem.invoke(item.url)
            }
            if (!item.persistent) {
                binding.root.setOnLongClickListener {
                    onLongClickItem.invoke(item.id)
                    return@setOnLongClickListener true
                }
            }

        }
    }
}