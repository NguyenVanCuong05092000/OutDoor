package brite.outdoor.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.ui.widgets.ProgressView
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.MediaViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView



class ImagePagerAdapterMedia(val context: Context,
                             val list: ArrayList<ImagePicker>?,
                             val itemClickListener:(ImagePicker)->Unit={_:ImagePicker->},
                             private val  playersMap: MutableMap<Int, SimpleExoPlayer>,
                             private val viewMap: MutableMap<Int, View>,
                             private val viewPager2: ViewPager2,
                             private val viewModel:MediaViewModel,
                             private val onShowViewCloseIfNeed:(position:Int)->Unit,
                             private val onPlayVideoIfNeed:(id : Int, needPlay : Boolean) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listImage :  ArrayList<ImagePicker>? = list
    private var mContext: Context = context
    private val TYPE_VIEW_VIDEO=1
    private val TYPE_VIEW_IMAGE=2
    init {

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val item = listImage?.get(position)
                if (item?.isVideo==true){
                    onPlayVideoIfNeed.invoke(item.id, true)
                }else{
                    item?.id?.let { onPlayVideoIfNeed.invoke(it,false) }
                }
                onShowViewCloseIfNeed(position)
            }
        })

    }
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType==TYPE_VIEW_VIDEO){
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_media_video, parent, false)
            return SlideImageViewHolder(view)
        }else{
            val view: View = LayoutInflater.from(context).inflate(R.layout.item_media_image, parent, false)
            return MediaImageHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MediaImageHolder){
                holder.apply {
                this.binData(position)
            }
        }else if (holder is SlideImageViewHolder){
            holder.apply {
                this.bindData(position)
            }
        }
    }


    override fun getItemCount(): Int {
        return if (listImage == null ) 0 else listImage?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        if (listImage?.get(position)?.isVideo==true){
            return TYPE_VIEW_VIDEO
        }else{
            return TYPE_VIEW_IMAGE
        }

    }

    inner class MediaImageHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var imgPage : ImageView = itemView.findViewById(R.id.imgPage)
        var progress : ProgressView = itemView.findViewById(R.id.progress)
        val btnOver:AppCompatButton = itemView.findViewById(R.id.btnOver)
        private var mDowX = 0f
        private var mDowY = 0f
        init {
            progress.layoutParams.width = MyApplication.getInstance().getSizeWithScale(40.0)
            progress.layoutParams.height = MyApplication.getInstance().getSizeWithScale(40.0)
			imgPage.layoutParams.height = MyApplication.getInstance().getSizeWithScale(300.0)
            btnOver.setTextSize(TypedValue.COMPLEX_UNIT_PX, MyApplication.getInstance().getSizeWithScaleFloat(10.0))
        }
        fun binData(position: Int){
            val item = listImage?.get(position)
            progress.visibility = View.VISIBLE
            Glide.with(imgPage)
                    .load(item?.uri)
                    .centerCrop()
                    .listener(object :RequestListener<Drawable>{
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            progress.visibility = View.GONE
                            progress.animation=null
                            return false
                        }

                    })
                    .into(imgPage)
            btnOver.setSingleClick {
                closeScreen()
            }
            eventTouchListener()
//            onListenerViewPager(position)
            if(viewPager2.currentItem==position) showBtnClose()
            if (viewMap.containsKey(position))
                viewMap.remove(position)
            viewMap[position]=btnOver
        }
        @SuppressLint("ClickableViewAccessibility")
        private fun eventTouchListener(){
                itemView.setOnTouchListener { v, event ->
                    when (event!!.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mDowX = event.x
                            mDowY = event.y

                        }
                        MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                            val y = event.y
                            val yDelta = y - mDowY
                            val x = event.x
                            val xDelta = x - mDowX
                            if (xDelta == 0f) showBtnClose()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val y = event.y
                            val yDelta = y - mDowY
                            val x = event.x
                            val xDelta = x - mDowX
                            if (yDelta > 200) {

                            }
                            if (yDelta < -100) closeScreen()
                        }

                    }
                    true
                }
        }
        private fun closeScreen(){
            val activity = context as Activity
            activity.finish()
        }
        private fun showBtnClose(){
            if (!btnOver.isVisible){
                btnOver.visibility=View.VISIBLE
                btnOver.animation = AnimationUtils.loadAnimation(context,R.anim.to_top)

                Handler(Looper.getMainLooper()).postDelayed({
                    if (btnOver.isVisible) {
                        btnOver.visibility=View.GONE
                        btnOver.animation = AnimationUtils.loadAnimation(context,R.anim.out_top)
                    }
                }, 2000L)
            }
        }

    }

    inner class SlideImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var clPlayVideo : ConstraintLayout = itemView.findViewById(R.id.clPlayVideo)
        val playerView : PlayerView = itemView.findViewById(R.id.playerView)
        val progress:ProgressView = itemView.findViewById(R.id.progress)
        val playerFullScreen : PlayerView = itemView.findViewById(R.id.playerViewFullScreen)
        val btnOver:AppCompatButton = itemView.findViewById(R.id.btnOver)
        private var mDowX = 0f
        private var mDowY = 0f



        init {
            playerView.layoutParams.height= MyApplication.getInstance().getSizeWithScale(450.0)
            progress.layoutParams.width = MyApplication.getInstance().getSizeWithScale(40.0)
            progress.layoutParams.height = MyApplication.getInstance().getSizeWithScale(40.0)
            btnOver.setTextSize(TypedValue.COMPLEX_UNIT_PX, MyApplication.getInstance().getSizeWithScaleFloat(10.0))
        }
        fun bindData(position: Int){
            val item = listImage?.get(position)
            playVideo(item,position)
            eventTouchListener()
//            onListenerViewPager(position)
            btnOver.setSingleClick {
                closeScreen()
            }
            if(viewPager2.currentItem==position) showBtnClose()
            if (viewMap.containsKey(position))
                viewMap.remove(position)
                viewMap[position]=btnOver
        }
        fun playVideo(item: ImagePicker?,position: Int){
            MediaPlayer.initialize(context)
            MediaPlayer.exoPlayer?.preparePlayer(playerView,playerFullScreen, true,progress,context,viewPager2,position = position)
            item?.uri?.let { MediaPlayer.exoPlayer?.setSource(context, it) }
            if (viewPager2.currentItem==position){
                MediaPlayer.exoPlayer?.playWhenReady=true
                viewModel.currentPlayingVideo = Pair(item?.id!!,  MediaPlayer.exoPlayer!!)
            }
            if (playersMap.containsKey(item?.id)){

                //remove item exoplay had in playerMap and release
                playersMap.get(item?.id)?.playWhenReady=false
                playersMap.get(item?.id)?.release()
                playersMap.remove(item?.id)

            }

             MediaPlayer.exoPlayer?.let {
                 playersMap[item!!.id] = it
             }

        }
        @SuppressLint("ClickableViewAccessibility")
        private fun eventTouchListener(){
            if (playerView.visibility==View.VISIBLE){
                itemView.setOnTouchListener { v, event ->
                    when (event!!.action) {
                        MotionEvent.ACTION_DOWN -> {
                            mDowX = event.x
                            mDowY = event.y
                        }
                        MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                            val y = event.y
                            val yDelta = y - mDowY
                            val x = event.x
                            val xDelta = x - mDowX
                            if (xDelta == 0f) showBtnClose()
                        }
                        MotionEvent.ACTION_MOVE -> {
                            val y = event.y
                            val yDelta = y - mDowY
                            val x = event.x
                            val xDelta = x - mDowX
                            if (yDelta > 200) {

                            }
                            if (yDelta < -100) closeScreen()
                        }

                    }
                    true
                }
            }

        }
        private fun closeScreen(){
            val activity = context as Activity
            activity.finish()
        }
        private fun showBtnClose(){
            if (!btnOver.isVisible){
                btnOver.visibility=View.VISIBLE
                btnOver.animation = AnimationUtils.loadAnimation(context,R.anim.to_top)

                Handler(Looper.getMainLooper()).postDelayed({
                    if (btnOver.isVisible) {
                        btnOver.visibility=View.GONE
                        btnOver.animation = AnimationUtils.loadAnimation(context,R.anim.out_top)
                    }
                }, 2000L)
            }
        }
    }

}
