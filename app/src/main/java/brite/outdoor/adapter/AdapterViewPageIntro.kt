package brite.outdoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import brite.outdoor.R
import brite.outdoor.ui.activities.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import java.util.*


class AdapterViewPageIntro(val activity: MainActivity) : PagerAdapter() {
    private val layoutInflater: LayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val mImageIds = intArrayOf(
        R.drawable.gt1,
        R.drawable.intro_2,
        R.drawable.intro_3,
        R.drawable.intro_4,
        R.drawable.intro_5
    )

    override fun getCount(): Int {
        return mImageIds.size ?: 0
    }

    private var widthImage : Int = activity.getSizeWithScale(375.0)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }
    var factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = layoutInflater.inflate(R.layout.item_intro, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.imgViewPageLogin)
        imageView.layoutParams.width = widthImage
        when(position) {
            0 -> {
                imageView.layoutParams.height = activity.getSizeWithScale(812.0)
                Glide.with(activity).asGif().placeholder(R.drawable.intro_1).load(mImageIds[position]).into(imageView)
            }
            1,2 -> {
                imageView.layoutParams.height = activity.getSizeWithScale(812.0)
                Glide.with(activity).load(mImageIds[position]).into(imageView)
            }
            3,4 -> {
                imageView.layoutParams.height = activity.getSizeWithScale(766.0)
                Glide.with(activity).load(mImageIds[position]).into(imageView)
            }
        }
        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}