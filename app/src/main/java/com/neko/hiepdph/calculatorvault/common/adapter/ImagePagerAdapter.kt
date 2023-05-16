package com.neko.hiepdph.calculatorvault.common.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.customview.CustomPhotoView
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem


interface TapViewListener {
    fun onTap()
}

class ImagePagerAdapter(val context: Context,private val listImage: MutableList<FileVaultItem>) : PagerAdapter() {
    private var listPhotoView: MutableList<CustomPhotoView> = mutableListOf()
    private var mListener: TapViewListener? = null



    fun setListener(listener: TapViewListener) {
        this.mListener = listener
    }


    override fun getCount(): Int {
        return listImage.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = CustomPhotoView(container.context)
        Glide.with(context).load(listImage[position].encryptedPath).centerInside().into(photoView)
        container.addView(
            photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        container.background = ContextCompat.getDrawable(context,R.color.neutral_06)
        photoView.setOnViewTapListener { view, x, y ->
            mListener?.onTap()
        }
        listPhotoView.add(photoView)
        return photoView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

//    override fun getItemPosition(`object`: Any): Int {
//        return POSITION_NONE
//    }


    fun rotate(position: Int) {
        listPhotoView[position].toggleRotate()
        notifyDataSetChanged()
    }


}