package com.neko.hiepdph.calculatorvault.common.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.customview.CustomPhotoView
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem


class ImagePagerAdapter(val context: Context, private val listImage: MutableList<FileVaultItem>) :
    PagerAdapter() {
    private var listPhotoView: MutableList<CustomPhotoView> = mutableListOf()
    private var mListener: TapViewListener? = null

    init {
        listImage.forEachIndexed { index, _ ->
            val photoView = CustomPhotoView(context)

            Glide.with(context).load(listImage[index].decodePath).error(R.drawable.ic_error_image)
                .centerInside().into(photoView)
            photoView.setOnViewTapListener { view, x, y ->
                mListener?.onTap()
            }
            listPhotoView.add(photoView)
        }

    }

    fun setListener(listener: TapViewListener) {
        this.mListener = listener
    }


    override fun getCount(): Int {
        return listImage.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(
            listPhotoView[position],
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return listPhotoView[position]
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


    fun rotate(position: Int) {
        listPhotoView[position].toggleRotate()
        notifyDataSetChanged()
    }


}

interface TapViewListener {
    fun onTap()
}
