package brite.outdoor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.utils.resizeLayout
import brite.outdoor.utils.setSingleClick
import com.bumptech.glide.Glide


class ImagePagerAdapter(val context: Context,
                        val list: ArrayList<ImagePicker>?,
                        val itemClickListener:(ImagePicker)->Unit={_:ImagePicker->}): RecyclerView.Adapter<ImagePagerAdapter.SlideImageViewHolder>() {
    private var listImage :  ArrayList<ImagePicker>? = list
    private var mContext: Context = context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagePagerAdapter.SlideImageViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_page_image, parent, false)
        return SlideImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagePagerAdapter.SlideImageViewHolder, position: Int) {
        val item = listImage?.get(position)
        holder.bindData(position)
        Glide.with(mContext).load(listImage?.get(position)?.uri).into(holder.imgPage)
    }

    override fun getItemCount(): Int {
        return if (listImage == null ) 0 else listImage?.size!!
    }

    inner class SlideImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgPage : ImageView = itemView.findViewById(R.id.imgPage)
        fun bindData(position: Int){
            val item = listImage?.get(position)
            imgPage.setSingleClick {
                item?.let {
                    itemClickListener(it)
                }
            }
        }

    }

}