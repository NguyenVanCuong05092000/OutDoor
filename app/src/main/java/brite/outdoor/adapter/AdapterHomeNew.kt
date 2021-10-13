package brite.outdoor.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import brite.outdoor.R
import brite.outdoor.BR
import brite.outdoor.app.MyApplication
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.databinding.ItemNewsBinding
import brite.outdoor.utils.setSingleClick
import brite.outdoor.utils.setTextSizePX
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import java.util.concurrent.Executors


class AdapterHomeNew(var context: Context,
                     var onClickItemListener: OnClickItemListener,
                     var recyclerView: RecyclerView,
                     private val isShowViewLine: Boolean = true,
                     private val  playersMap: MutableMap<Int, SimpleExoPlayer>,
                     private val onPlayVideoIfNeed:(id : Int, needPlay : Boolean) -> Unit) :
        ListAdapter<ListPostUserData, AdapterHomeNew.HomeViewHolder>(
                AsyncDifferConfig.Builder<ListPostUserData>(HomeDiffUtilCallBack()).setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build()) {
    private var mContext: Context? = null
    var arrayListNews = ArrayList<ListPostUserData>()
    var isLoading: Boolean = false
    var loadMore: ILoadMore? = null
    var lastVisibleItem: Int? = null
    var totalItemCount: Int? = null
    var visibleThreshold: Int = 1
    var visibleItemCount: Int? = null
    var offset: Int? = null
    var extent: Int? = null
    var range: Int? = null
    var percentage: Int? = null

    //
    private var mFirstVisibleItem = 0
    private var mVisibleItemCount = 0

    companion object {
        const val SCROLL_PERCENTAGE = 98
    }

    interface OnClickItemListener {
        fun onClickMenu(entityNew: ListPostUserData?, position: Int)
        fun onClickItem(entityNew: ListPostUserData?, position: Int)
        fun onClickLike(entityNew: ListPostUserData?,position: Int)
        fun onClickComment(entityNew: ListPostUserData?,position: Int)
        fun onClickShare(entityNew: ListPostUserData?,position: Int)
        fun onClickItemGoToFrmPersonalPage(userId:Int?)
    }

    private var mOnClickItemListener: OnClickItemListener?
    fun releaseAllPlayers(){
        playersMap.map {
            it.value.release()
        }
    }

    // call when item recycled to improve performance
    fun releaseRecycledPlayers(index: Int){
        playersMap[index]?.release()
    }


    override fun onViewRecycled(holder: HomeViewHolder) {
        getItemAt(holder.bindingAdapterPosition)?.let {
            releaseRecycledPlayers(it.id)
        }
        super.onViewRecycled(holder)
    }

    fun getItemAt(position: Int) : ListPostUserData? {
        if (position >= 0 && arrayListNews.size > 0 && arrayListNews.size > position) {
            return arrayListNews[position]
        }
        return null
    }

    init {
        mContext = context
        mOnClickItemListener = onClickItemListener
        recyclerView.setEnabled(false);
        recyclerView.setClickable(false);
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    totalItemCount = linearLayoutManager.itemCount
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    mFirstVisibleItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    val item = getItemAt(mFirstVisibleItem)
                    item?.apply {
                        if (getListContent()?.get(0)?.listImg?.get(0)?.endsWith(".mp4") ==  true) {
                            onPlayVideoIfNeed.invoke(this.id, true)
                        } else {
                            onPlayVideoIfNeed.invoke(this.id, false)
                        }
                    }
                    visibleItemCount = linearLayoutManager.childCount
                    offset = recyclerView.computeVerticalScrollOffset()
                    extent = recyclerView.computeVerticalScrollExtent()
                    range = recyclerView.computeVerticalScrollRange()
                    percentage = ((100.0 * offset!! / (range!!.toFloat() - extent!!)).toInt())
                    if (!isLoading && ((totalItemCount!!) <= (lastVisibleItem!! + visibleThreshold)) && dy > 0 && percentage!! > SCROLL_PERCENTAGE) {
                        if (loadMore != null) {
                            loadMore?.onLoadMore()
                        }

                        isLoading = true

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })

    }

    override fun submitList(list: List<ListPostUserData>?) {
        arrayListNews.clear()
        if (list != null) {
            arrayListNews.addAll(list)
        }
        super.submitList(arrayListNews)
        notifyDataSetChanged()
        this.recyclerView.scrollToPosition(0)

//        notifyDataSetChanged()
    }

    fun addAllItem(list: ArrayList<ListPostUserData>) {
        val oldSize = arrayListNews.size
        arrayListNews.addAll(list)
        try {
            super.submitList(arrayListNews)
            notifyItemRangeChanged(oldSize-1,arrayListNews.size-1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun updateListFollowUnFollow(hashMap: HashMap<Int, Int>) {
        for ((key, value) in hashMap) {
            currentList.forEach {
                if (it.created_id == key) {
                    if (it.state_follow != value) {
                        it.state_follow = value
                    }

                }
            }
        }
    }
    fun removeUnFollow(id:Int){
        val data = arrayListNews.filterNot {
                id==it.created_id
            }
        arrayListNews.clear()
        arrayListNews.addAll(data)
        super.submitList(data)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        arrayListNews.removeAt(position)
        super.submitList(arrayListNews)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, arrayListNews.size)
//        notifyDataSetChanged()
    }

    fun setLoaded() {
        isLoading = false
    }

    @JvmName("setLoadMore1")
    fun setLoadMore(loadMore: ILoadMore?) {
        this.loadMore = loadMore
    }

    interface ILoadMore {
        fun onLoadMore()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = DataBindingUtil.inflate<ItemNewsBinding>(LayoutInflater.from(context), R.layout.item_news, parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = getItem(position)
        holder.binData(holder.binding, item, position)
        holder.binding.setVariable(BR.item, item)
        holder.binding.executePendingBindings()
//            holder.setIsRecyclable(false)
//        if (isShowViewLine) {
//            if (position == 0) {
//                holder.binding.viewLine.visibility = View.VISIBLE
//            } else holder.binding.viewLine.visibility = View.GONE
//
//            if (position == currentList.size.minus(1)) {
//                holder.binding.viewFooter.visibility = View.VISIBLE
//            } else holder.binding.viewFooter.visibility = View.GONE
//        }
    }

    class HomeDiffUtilCallBack : DiffUtil.ItemCallback<ListPostUserData>() {
        override fun areItemsTheSame(oldItem: ListPostUserData, newItem: ListPostUserData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListPostUserData, newItem: ListPostUserData): Boolean {
            return oldItem.id == newItem.id
        }
    }

    inner class HomeViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                clItemNews.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(412.0)
//                clTitleAndImg.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(326.0)
                clTitleAndImg.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(256.0)
                imgAvatar.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(46.0)
                imgAvatar.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(46.0)
                imgMenu.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(37.0)
                imgMenu.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(49.0)
                imgLocation.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(24.0)
                imgLocation.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(24.0)
                btnComment.setSize(13F)
                btnLike.setSize(13F)
                btnShare.setSize(13F)
                viewLine.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(95.0)
                viewFooter.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(35.0)
                tvMonth.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(18.0))
                tvDay.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(48.0))
            }
        }

        fun binData(binding: ItemNewsBinding, item: ListPostUserData, position: Int) {
            binding.apply {
                setViewInteractive(binding, item)
                imgMenu.setSingleClick {
                    mOnClickItemListener?.onClickMenu(entityNew = item, position)
                }
                btnComment.setSingleClick {
                    mOnClickItemListener?.onClickComment(entityNew = item,position)
                }
                btnLike.setSingleClick {
                    mOnClickItemListener?.onClickLike(entityNew = item,position)
                }
                btnShare.setSingleClick {
                    mOnClickItemListener?.onClickShare(entityNew = item,position)
                }
                clTitleAndImg.setSingleClick {
                    mOnClickItemListener?.onClickItem(entityNew = item, position = position)
                }

                tvName.setSingleClick {
                    mOnClickItemListener?.onClickItemGoToFrmPersonalPage(userId = item.created_id)
                }

                if (position==arrayListNews.size-1){
                       viewFooter.visibility=View.VISIBLE
                }else viewFooter.visibility=View.GONE

                if (item.location_id == null || item.location_id == 0) {
                    imgLocation.setImageResource(R.drawable.ic_utensils_main)
                } else {
                    imgLocation.setImageResource(R.drawable.ic_location)
                }
            }
        }

        private fun setViewInteractive(binding: ItemNewsBinding, item: ListPostUserData) {
            binding.apply {
                var urlName : String? = null
                if (item.getListContent()!=null && item.getListContent()!!.size>0
                    && item.getListContent()?.get(0)?.listImg!=null
                    && item.getListContent()?.get(0)?.listImg?.size?:0 >0) {
                    urlName = item.getListContent()?.get(0)?.listImg?.get(0)
                }

                if (!urlName.isNullOrEmpty() && urlName.endsWith(".mp4")) {
                    playView.visibility = View.VISIBLE
                    imgPost.visibility = View.GONE
                    val url = item.url_prefix+urlName

                        val simpleExoPlayer = SimpleExoPlayer.Builder(context).build()
                        simpleExoPlayer.playWhenReady = false
                        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                        playView.setKeepContentOnPlayerReset(true)
                        playView.useController = false
                        val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSourceFactory("Demo")).createMediaSource(Uri.parse(url))
                        simpleExoPlayer.prepare(mediaSource)
                        playView.player = simpleExoPlayer

                        // add player with its index to map
                        if (playersMap.containsKey(item.id))
                            playersMap.remove(item.id)
                        playersMap[item.id] = simpleExoPlayer

                        playView.player!!.addListener(object : Player.EventListener {

                            override fun onPlayerError(error: ExoPlaybackException) {
                                super.onPlayerError(error)
                            }

                            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                                super.onPlayerStateChanged(playWhenReady, playbackState)

                                if (playbackState == Player.STATE_BUFFERING){
//                                    callback.onVideoBuffering(player)
                                    // Buffering..
                                    // set progress bar visible here
                                    // set thumbnail visible
//                                    thumbnail.visibility = View.VISIBLE
//                                    progressbar.visibility = View.VISIBLE
                                }

                                if (playbackState == Player.STATE_READY){
                                    // [PlayerView] has fetched the video duration so this is the block to hide the buffering progress bar
//                                    progressbar.visibility = View.GONE
                                    // set thumbnail gone
//                                    thumbnail.visibility = View.GONE
//                                    callback.onVideoDurationRetrieved(this@loadVideo.player!!.duration, player)
                                }

                                if (playbackState == Player.STATE_READY && simpleExoPlayer.playWhenReady){
                                    // [PlayerView] has started playing/resumed the video
//                                    callback.onStartedPlaying(player)
                                }
                            }
                        })
                    

                } else {
                    playView.visibility = View.GONE
                    imgPost.visibility = View.VISIBLE
                }
                if (item.post_likes == true) {
                    binding.btnLike.apply {
                        setIconAndSize(
                                R.drawable.ic_liked,
                                MyApplication.getInstance().getSizeWithScale(22.48),
                                MyApplication.getInstance().getSizeWithScale(20.0)
                        )
                        setColorText(R.color.colorMain)
                    }

                } else {
                    binding.btnLike.apply {
                        setIconAndSize(
                                R.drawable.ic_like,
                                MyApplication.getInstance().getSizeWithScale(22.48),
                                MyApplication.getInstance().getSizeWithScale(20.0)
                        )
                        setColorText(R.color.black)
                    }

                }
                if (item.post_shares==true){
                    binding.btnShare.apply {
                        setIconAndSize(R.drawable.ic_shared,
                            MyApplication.getInstance().getSizeWithScale(22.48),
                            MyApplication.getInstance().getSizeWithScale(20.0))
                        setColorText(R.color.colorMain)
                    }
                }else{
                    binding.btnShare.apply {
                        setIconAndSize(
                            R.drawable.ic_share,
                            MyApplication.getInstance().getSizeWithScale(22.48),
                            MyApplication.getInstance().getSizeWithScale(20.0)
                        )
                        setColorText(R.color.black)
                    }
                }
            }
            binding.btnComment.setIconAndSize(
                    R.drawable.ic_comment,
                    MyApplication.getInstance().getSizeWithScale(20.0),
                    MyApplication.getInstance().getSizeWithScale(20.0)
            )

        }
    }
}