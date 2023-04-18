package com.neko.hiepdph.calculatorvault.common.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.neko.hiepdph.calculatorvault.common.customview.CustomPhotoView
import com.neko.hiepdph.calculatorvault.data.model.ListItem

class ImagePagerAdapter(val context: Context) : PagerAdapter() {
    private var listImage: List<ListItem> = mutableListOf()
    private var listPhotoView :MutableList<CustomPhotoView> = mutableListOf()

    fun setData(mListImage: List<ListItem>) {
        listImage = mListImage
        notifyDataSetChanged()
    }


    override fun getCount(): Int {
        return listImage.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = CustomPhotoView(container.context)
        Glide.with(context).load(listImage[position].path).centerCrop().into(photoView)
        container.addView(
            photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        listPhotoView.add(photoView)
        return photoView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun rotate(position: Int) {
        Log.d("TAG", "rotate: "+position)
        listPhotoView[position].toggleRotate()
        notifyDataSetChanged()
    }
}