package brite.outdoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.BR
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.databinding.ItemNewsBinding
import brite.outdoor.utils.setSingleClick
import brite.outdoor.utils.setTextSizePX


class AdapterNews(val context: Context, var onClickItemListener: OnClickItemListener, var recyclerView:RecyclerView, private val isShowViewLine :Boolean=true) : RecyclerView.Adapter<AdapterNews.ViewHolder>() {
    private var mContext: Context? = null
    var arrayListNews = ArrayList<ListPostUserData>()
    var isLoading:Boolean=false
    var loadMore:ILoadMore?=null
    var lastVisibleItem:Int?=null
    var totalItemCount:Int?=null
    var visibleThreshold:Int=1
    var visibleItemCount:Int?=null
    var offset:Int?=null
    var extent:Int?=null
    var range:Int?=null
    var percentage:Int?=null
    companion object{
        const val SCROLL_PERCENTAGE=95
    }

    interface OnClickItemListener {
        fun onClickMenu(entityNew: ListPostUserData?,position: Int)
        fun onClickItem(entityNew: ListPostUserData?,position: Int)
        fun onClickLike(entityNew: ListPostUserData?)
        fun onClickComment(entityNew: ListPostUserData?)
        fun onClickShare(entityNew: ListPostUserData?)
    }
    private var mOnClickItemListener: OnClickItemListener?

    init {
        mContext = context
        mOnClickItemListener = onClickItemListener

        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    totalItemCount = linearLayoutManager.itemCount
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    visibleItemCount=linearLayoutManager.childCount
                    offset = recyclerView.computeVerticalScrollOffset()
                    extent = recyclerView.computeVerticalScrollExtent()
                    range = recyclerView.computeVerticalScrollRange()
                    percentage = ((100.0 * offset!! / (range!!.toFloat() - extent!!)).toInt())
                    if(!isLoading && ((totalItemCount!!) <= (lastVisibleItem!! + visibleThreshold)) && dy>0  && percentage!! > SCROLL_PERCENTAGE)
                    {
                        if(loadMore != null){
                            loadMore?.onLoadMore()
                        }

                        isLoading = true

                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
        })

    }
    fun updateList(list: ArrayList<ListPostUserData>){
        arrayListNews.clear()
        arrayListNews.addAll(list)
        notifyDataSetChanged()
        this.recyclerView.scrollToPosition(0)
    }
    fun addAllItem(list: ArrayList<ListPostUserData>){
       val oldSize = arrayListNews.size
       arrayListNews.addAll(list)
       notifyItemRangeChanged(oldSize-1,arrayListNews.size)
   }
    fun updateListFollowUnFollow(hashMap: HashMap<Int,Int>){
        for((key,value) in hashMap){
            arrayListNews.forEach {
                if (it.created_id==key){
                    if (it.state_follow!=value) {
                        it.state_follow=value
                    }

                }
            }
        }
    }
    fun removeUnFollow(id:Int){
      val data = arrayListNews.filterNot {
          id==it.created_id
      }
        (data as ArrayList<ListPostUserData>).apply {
            updateList(this)
        }

    }
    fun clearData(){
        arrayListNews.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterNews.ViewHolder {
            val binding = DataBindingUtil.inflate<ItemNewsBinding>(LayoutInflater.from(context),R.layout.item_news,parent,false)
           return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterNews.ViewHolder, position: Int) {

            val entityNews = arrayListNews[position]
            holder.binData(holder.binding,entityNews,position)
            holder.binding.setVariable(BR.item, entityNews)
            holder.binding.executePendingBindings()
//            holder.setIsRecyclable(false)
            if (isShowViewLine){
                if (position == 0){
                    holder.binding.viewLine.visibility = View.VISIBLE
                }else holder.binding.viewLine.visibility = View.GONE

                if (position == arrayListNews.size.minus(1)){
                    holder.binding.viewFooter.visibility = View.VISIBLE
                }else holder.binding.viewFooter.visibility = View.GONE
            }



    }

    override fun getItemCount(): Int {
     return arrayListNews.size
    }

    inner class ViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                clItemNews.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(351.0)
                clItemNews.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(412.0)
                clTitleAndImg.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(326.0)
                clTitleAndImg.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(256.0)
                imgAvatar.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(46.0)
                imgAvatar.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(46.0)
                imgMenu.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(23.0)
                imgMenu.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(28.0)
                imgLocation.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(24.0)
                imgLocation.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(24.0)
                btnComment.setSize(13F)
                btnLike.setSize(13F)
                btnShare.setSize(13F)
                viewLine.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(95.0)

                tvMonth.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(18.0))
                tvDay.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(48.0))
            }
        }


        fun binData(binding: ItemNewsBinding,item: ListPostUserData,position: Int) {
                binding.apply {
                    setViewInteractive(binding,item)
                    imgMenu.setOnClickListener {
                        mOnClickItemListener?.onClickMenu(entityNew = item,position)
                    }
                    btnComment.setSingleClick {
                        mOnClickItemListener?.onClickComment(entityNew = item)
                    }
                    btnLike.setSingleClick {
                        mOnClickItemListener?.onClickLike(entityNew = item)
                    }
                    btnShare.setSingleClick {
                        mOnClickItemListener?.onClickShare(entityNew = item)
                    }
                    clTitleAndImg.setSingleClick {
                        mOnClickItemListener?.onClickItem(entityNew = item,position=position)
                    }
                    if (item.location_id == null || item.location_id == 0) {
                        imgLocation.setImageResource(R.drawable.ic_utensils_main)
                    } else {
                        imgLocation.setImageResource(R.drawable.ic_location)
                    }

                }

        }

        fun setViewInteractive(binding: ItemNewsBinding,item: ListPostUserData) {

            binding.apply {
                if (item.post_likes == true) {
                    binding.btnLike.apply {
                        setIconAndSize(
                            R.drawable.ic_liked,
                                MyApplication.getInstance().getSizeWithScale(22.48),
                                MyApplication.getInstance().getSizeWithScale (20.0)
                        )
                        setColorText(R.color.colorMain)
                    }

                } else {
                    binding.btnLike.apply {
                        setIconAndSize(
                            R.drawable.ic_like,
                                MyApplication.getInstance().getSizeWithScale(22.48),
                                MyApplication.getInstance().getSizeWithScale (20.0)
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
            binding.btnShare.setIconAndSize(
                    R.drawable.ic_share,
                    MyApplication.getInstance().getSizeWithScale(20.5),
                    MyApplication.getInstance().getSizeWithScale(20.5)
            )
        }
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

    fun removeItem(position: Int){
        arrayListNews.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,arrayListNews.size)
//        notifyDataSetChanged()
    }

}