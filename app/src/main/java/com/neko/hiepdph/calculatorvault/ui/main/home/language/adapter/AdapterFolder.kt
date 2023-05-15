package com.neko.hiepdph.calculatorvault.ui.main.home.language.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.data.model.VaultFileDirItem
import com.neko.hiepdph.calculatorvault.databinding.ItemHomeGridLayoutBinding
import com.neko.hiepdph.calculatorvault.databinding.ItemHomeListLayoutBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutMenuItemOptionBinding


class AdapterFolder(
    context: Context,
    val onItemPress: (customFolder: VaultFileDirItem) -> Unit,
    val onRenamePress: (customFolder: VaultFileDirItem) -> Unit,
    val onDeletePress: (customFolder: VaultFileDirItem) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {
    private var popupWindow: PopupWindow? = null
    private var bindingLayout: LayoutMenuItemOptionBinding? = null

    companion object {
        var isSwitchView = false
    }

    init {
        initPopupWindow(context)
    }

    private val LIST_ITEM = 0
    private val GRID_ITEM = 1

    inner class FolderViewHolderList(val binding: ItemHomeListLayoutBinding) :
        ViewHolder(binding.root)

    inner class FolderViewHolderGrid(val binding: ItemHomeGridLayoutBinding) :
        ViewHolder(binding.root)


    private var listFolder = mutableListOf<VaultFileDirItem>()

    fun setData(list: List<VaultFileDirItem>) {
        listFolder.clear()
        listFolder.addAll(list.toMutableList())
        notifyDataSetChanged()
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
        return listFolder.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customFolder = listFolder[position]
        when (holder.itemViewType) {
            LIST_ITEM -> {
                with(holder as FolderViewHolderList) {
                    binding.imvLogo.setImageResource(getImageLogo(customFolder.type))
                    binding.tvName.text = customFolder.name
                    binding.imvOption.visibility =
                        if (customFolder.type == Constant.TYPE_ADD_MORE) View.VISIBLE else View.GONE
                    binding.tvCount.text =
                        customFolder.mChildren.toString() + " " + itemView.context.getString(R.string.item)
                    binding.imvOption.clickWithDebounce {
                        showOption(binding.imvOption, customFolder, position)
                    }
                    binding.root.clickWithDebounce {
                        onItemPress.invoke(customFolder)
                    }
                }
            }

            GRID_ITEM -> {
                with(holder as FolderViewHolderGrid) {
                    binding.imvLogo.setImageResource(getImageLogo(customFolder.type))
                    binding.tvName.text = customFolder.name
                    binding.imvOption.visibility =
                        if (customFolder.type == Constant.TYPE_ADD_MORE) View.VISIBLE else View.GONE
                    binding.tvCount.text = customFolder.mChildren.toString()
                    binding.imvOption.clickWithDebounce {
                        showOption(binding.imvOption, customFolder,position)
                    }
                    binding.root.clickWithDebounce {
                        onItemPress.invoke(customFolder)
                    }

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

    private fun showOption(view: View, customFolder: VaultFileDirItem, position: Int) {
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
        Log.d("TAG", "showOption: "+positionOfIcon)
        Log.d("TAG", "showOption: "+height)
        if (positionOfIcon < height) {
            popupWindow?.showAsDropDown(
                view,
                0,
                -view.height + popupWindow!!.height
            )
        }else{
            popupWindow?.showAsDropDown(
                view,
                0,
                -400
            )
        }
    }
}