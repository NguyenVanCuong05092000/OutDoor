package brite.outdoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import brite.outdoor.R
import brite.outdoor.adapter.utils.AdapterNotification
import brite.outdoor.data.api_entities.response.ResponseNotification
import brite.outdoor.databinding.ItemNotificationBinding
import java.util.concurrent.Executors
import brite.outdoor.BR
import brite.outdoor.app.MyApplication
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.utils.setSingleClick

class AdapterNotificationHome(val context:Context,val recyclerView: RecyclerView,var onClickItemListener:OnClickItemListener) : ListAdapter<ResponseNotification.ListNotificationData, AdapterNotificationHome.NotificationViewHolder>(
        AsyncDifferConfig.Builder<ResponseNotification.ListNotificationData>(NotificationDiffUtilCallBack()).setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build()) {

    var arrayList = ArrayList<ResponseNotification.ListNotificationData>()
    private var url_prefix_avatar:String?=null

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

    companion object {
        const val SCROLL_PERCENTAGE = 98
    }
    interface OnClickItemListener{
        fun onClickItem(entity:ResponseNotification.ListNotificationData?,position: Int)
    }
    private var mOnClickItemListener : OnClickItemListener?=null
    fun setUrlPrefixAvatar(url_prefix_avatar:String?){
        this.url_prefix_avatar=url_prefix_avatar
    }
    init {

        mOnClickItemListener=onClickItemListener

        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    totalItemCount = linearLayoutManager.itemCount
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
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

    override fun submitList(list: List<ResponseNotification.ListNotificationData>?) {
       arrayList.clear()
        list?.let {
            arrayList.addAll(it)
        }
        super.submitList(list)
        notifyDataSetChanged()
        this.recyclerView.scrollToPosition(0)
    }
    fun addAllItem(list: ArrayList<ResponseNotification.ListNotificationData>) {
        val oldSize = arrayList.size
        arrayList.addAll(list)
        try {
            super.submitList(arrayList)
            notifyItemRangeChanged(oldSize-1,arrayList.size-1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    interface ILoadMore {
        fun onLoadMore()
    }
    fun setLoaded() {
        isLoading = false
    }

    @JvmName("setLoadMore1")
    fun setLoadMore(loadMore: ILoadMore?) {
        this.loadMore = loadMore
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = DataBindingUtil.inflate<ItemNotificationBinding>(LayoutInflater.from(context), R.layout.item_notification,parent,false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
       val item = getItem(position)
        holder.binData(holder.binding,item,position)
        holder.binding.setVariable(BR.item,item)
        url_prefix_avatar?.let {
            holder.binding.setVariable(BR.urlAvatar,it)
        }
        holder.binding.executePendingBindings()
    }

    class NotificationDiffUtilCallBack : DiffUtil.ItemCallback<ResponseNotification.ListNotificationData>() {
        override fun areItemsTheSame(oldItem:ResponseNotification.ListNotificationData, newItem: ResponseNotification.ListNotificationData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResponseNotification.ListNotificationData, newItem: ResponseNotification.ListNotificationData): Boolean {
            return oldItem.id == newItem.id
        }

    }

    inner class NotificationViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {


        init {
            binding.apply {
                imgAvatar.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(51.0)
                imgAvatar.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(51.0)
                viewFooter.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(75.0)
            }
        }
        fun binData(binding: ItemNotificationBinding, item: ResponseNotification.ListNotificationData?, position: Int) {
            binding.apply {
                root.setSingleClick {
                    mOnClickItemListener?.onClickItem(item,position)
                }
                if (position==arrayList.size-1){
                    viewFooter.visibility= View.VISIBLE
                }else viewFooter.visibility= View.GONE
            }
        }
    }


}