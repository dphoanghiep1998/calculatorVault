package com.neko.hiepdph.calculatorvault.ui.main.home.language

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.extensions.clickWithDebounce
import com.neko.hiepdph.calculatorvault.databinding.ItemLanguageNativeAdsBinding
import com.neko.hiepdph.calculatorvault.databinding.LayoutItemLanguageBinding

import java.util.*

class AdapterLanguage(private val context: Context, private val onCLickItem: (Locale) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mRecyclerView: RecyclerView? = null
    private var listDataLang = mutableListOf<Any>()
    private var listDataDisplay = mutableListOf<Any>()
    private val diffCallback = DiffCallback(listDataLang, mutableListOf())

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun setData(mListLang: MutableList<Any>, mListDisplay: MutableList<Any>) {
        diffCallback.newList = mListLang.toMutableList()
        val diffUtil = DiffUtil.calculateDiff(diffCallback, true)
        diffUtil.dispatchUpdatesTo(this)
        listDataLang.clear()
        listDataLang.addAll(mListLang)
        listDataDisplay.clear()
        listDataDisplay.addAll(mListDisplay)
    }

    private fun swap(secondPosition: Int) {
        Collections.swap(listDataLang, 0, secondPosition)
        Collections.swap(listDataDisplay, 0, secondPosition)

        notifyItemMoved(0, secondPosition)
        notifyItemMoved(secondPosition, 0)
        notifyItemChanged(secondPosition)
        notifyItemChanged(0)
//        notifyItemMoved(secondPosition,0)
        mRecyclerView?.smoothScrollToPosition(0)
    }

    fun setCurrentLanguage(language: String) {
        var pos = 0
        var locale = Locale("en")
        listDataLang.forEachIndexed { index, l ->
            if (l is Locale) {
                if (l.language == language) {
                    pos = index
                    locale = l
                }
            }
        }
        swap(pos)
//        notifyItemMoved(pos,0)
//        notifyItemChanged(pos)
        onCLickItem(locale)
    }


    override fun getItemCount(): Int {
        return listDataLang.size
    }

    override fun getItemViewType(position: Int): Int {
        if (listDataLang[position] == "adsApp") {
            return 1
        }
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                with(holder as LanguageViewHolder) {
                    if (this.adapterPosition == 0) {
                        binding.tvCountry.setTextColor(
                            ContextCompat.getColor(
                                context, R.color.primary
                            )
                        )
                        binding.container.background = ContextCompat.getDrawable(
                            context, R.drawable.infor_language_selected
                        )
                    } else {
                        binding.tvCountry.setTextColor(
                            ContextCompat.getColor(
                                context, R.color.neutral_06
                            )
                        )
                        binding.container.background = null
                    }
                    with(listDataLang[position] as Locale) {
                        val display = listDataDisplay[position] as Pair<Int, Int>
                        binding.tvCountry.text = context.getString(display.first)
                        binding.imvFlag.setImageDrawable(
                            ContextCompat.getDrawable(
                                context, display.second
                            )
                        )
                        binding.root.clickWithDebounce {
                            Log.d("TAG", "onBindViewHolder: " + adapterPosition)
                            if (adapterPosition == 0) {
                                return@clickWithDebounce
                            }
                            swap(adapterPosition)
                            onCLickItem(this)
                        }
                    }
                }

            }
            1 -> {
//                with(holder as LanguageNativeAdsViewHolder) {
//                    if (insideNativeAd != null) {
//                        binding.nativeAdMediumView.showShimmer(false)
//                        binding.nativeAdMediumView.setNativeAd(insideNativeAd!!)
//                        binding.nativeAdMediumView.visibility = View.VISIBLE
//                        binding.root.show()
//                    } else {
//                        binding.root.hide()
//                        with(binding.nativeAdMediumView) {
//                            visibility = View.GONE
//                            showShimmer(true)
//                        }
//                    }
//                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = LayoutItemLanguageBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LanguageViewHolder(binding)
            }
            else -> {
                val binding = ItemLanguageNativeAdsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LanguageNativeAdsViewHolder(binding)
            }

        }
    }
//
//    fun insertAds(ads: NativeAd) {
//        insideNativeAd = ads
//        notifyDataSetChanged()
//    }

    inner class LanguageViewHolder(val binding: LayoutItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LanguageNativeAdsViewHolder(val binding: ItemLanguageNativeAdsBinding) :
        RecyclerView.ViewHolder(binding.root)
}