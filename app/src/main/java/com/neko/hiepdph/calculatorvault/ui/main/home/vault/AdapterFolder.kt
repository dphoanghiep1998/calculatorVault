package com.neko.hiepdph.calculatorvault.ui.main.home.vault

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.model.VaultDir
import com.neko.hiepdph.calculatorvault.databinding.ItemHomeGridLayoutBinding
import com.neko.hiepdph.calculatorvault.databinding.ItemHomeListLayoutBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutMenuItemOptionBinding


class AdapterFolder(
    context: Context,
    val onItemPress: (customFolder: VaultDir) -> Unit,
    val onRenamePress: (customFolder: VaultDir) -> Unit,
    val onDeletePress: (customFolder: VaultDir) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    private var popupWindow: PopupWindow? = null
    private var bindingLayout: LayoutMenuItemOptionBinding? = null

    companion object {
        var isSwitchView = false
    }

    private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VaultDir>() {

        override fun areItemsTheSame(oldItem: VaultDir, newItem: VaultDir): Boolean {
            return oldItem.mPath == newItem.mPath
        }

        override fun areContentsTheSame(oldItem: VaultDir, newItem: VaultDir): Boolean {
            return oldItem == newItem
        }


    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    init {
        initPopupWindow(context)
    }

    private val LIST_ITEM = 0
    private val GRID_ITEM = 1

    inner class FolderViewHolderList(val binding: ItemHomeListLayoutBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: VaultDir) {
            binding.imvLogo.setImageResource(getImageLogo(item.type))
            binding.tvName.text = item.mName
            binding.imvOption.visibility =
                if (item.type == Constant.TYPE_ADD_MORE) View.VISIBLE else View.GONE
            binding.tvCount.text =
                item.mChildren.toString() + " " + itemView.context.getString(R.string.item)
            binding.imvOption.clickWithDebounce {
                showOption(binding.imvOption, item, absoluteAdapterPosition)
            }
            binding.root.clickWithDebounce {
                onItemPress.invoke(item)
            }
            binding.tvName.requestFocus()
        }
    }

    inner class FolderViewHolderGrid(val binding: ItemHomeGridLayoutBinding) :
        ViewHolder(binding.root) {
        fun bind(item: VaultDir) {
            binding.imvLogo.setImageResource(getImageLogo(item.type))
            binding.tvName.text = item.mName
            binding.imvOption.visibility =
                if (item.type == Constant.TYPE_ADD_MORE) View.VISIBLE else View.GONE
            binding.tvCount.text = item.mChildren.toString()
            binding.imvOption.clickWithDebounce {
                showOption(binding.imvOption, item, absoluteAdapterPosition)
            }
            binding.root.clickWithDebounce {
                onItemPress.invoke(item)
            }

            binding.tvName.requestFocus()
        }
    }


    fun setData(list: List<VaultDir>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == LIST_ITEM) {
            val binding = ItemHomeListLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            FolderViewHolderList(binding)
        } else {
            val binding = ItemHomeGridLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            FolderViewHolderGrid(binding)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            LIST_ITEM -> {
                with(holder as FolderViewHolderList) {
                    bind(differ.currentList[position])

                }
            }

            GRID_ITEM -> {
                with(holder as FolderViewHolderGrid) {
                    bind(differ.currentList[position])

                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!isSwitchView) {
            LIST_ITEM
        } else {
            GRID_ITEM
        }
    }

    private fun getImageLogo(type: String): Int {
        return when (type) {
            Constant.TYPE_PICTURE -> R.drawable.ic_item_pictures
            Constant.TYPE_VIDEOS -> R.drawable.ic_item_videos
            Constant.TYPE_AUDIOS -> R.drawable.ic_item_audios
            Constant.TYPE_FILE -> R.drawable.ic_item_files
            else -> R.drawable.ic_folder_additional
        }
    }

    private fun initPopupWindow(context: Context) {
        val inflater: LayoutInflater =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
        bindingLayout = LayoutMenuItemOptionBinding.inflate(inflater, null, false)

        popupWindow = PopupWindow(
            bindingLayout?.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
    }

    private fun showOption(view: View, customFolder: VaultDir, position: Int) {
        bindingLayout?.root?.clickWithDebounce {
            popupWindow?.dismiss()
        }
        bindingLayout?.tvDelete?.clickWithDebounce {
            onDeletePress.invoke(customFolder)
            popupWindow?.dismiss()
        }
        bindingLayout?.tvRename?.clickWithDebounce {
            onRenamePress.invoke(customFolder)
            popupWindow?.dismiss()
        }
        val values = IntArray(2)
        view.getLocationInWindow(values)
        val positionOfIcon = values[1]
        val displayMetrics: DisplayMetrics = view.context.resources.displayMetrics
        val height = displayMetrics.heightPixels * 2 / 3
        if (positionOfIcon < height) {
            popupWindow?.showAsDropDown(
                view, 0, -view.height + popupWindow!!.height
            )
        } else {
            popupWindow?.showAsDropDown(
                view, 0, -400
            )
        }
    }
}