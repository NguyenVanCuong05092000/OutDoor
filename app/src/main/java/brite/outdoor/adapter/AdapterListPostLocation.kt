package brite.outdoor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.BR
import brite.outdoor.app.MyApplication
import brite.outdoor.data.api_entities.response.ResponseListPostLocation
import brite.outdoor.databinding.ItemListPostLocationBinding
import brite.outdoor.utils.setTextSizePX
import java.lang.Exception

class AdapterListPostLocation(val context: Context,
                              val recyclerView: RecyclerView,
                              val itemClickMenuListener:(ResponseListPostLocation.ListPostLocationData,Int)->Unit={_:ResponseListPostLocation.ListPostLocationData,_:Int ->}
) : RecyclerView.Adapter<AdapterListPostLocation.ListPostLocationViewHolder>() {
    private var arrayListPost = ArrayList<ResponseListPostLocation.ListPostLocationData>()
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

    init {
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
                    if (!isLoading && ((totalItemCount!!) <= (lastVisibleItem!! + visibleThreshold)) && dy > 0 && percentage!! > AdapterNews.SCROLL_PERCENTAGE) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListPostLocationViewHolder {
        val binding = DataBindingUtil.inflate<ItemListPostLocationBinding>(LayoutInflater.from(context), R.layout.item_list_post_location, parent, false)
        return ListPostLocationViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ListPostLocationViewHolder, position: Int) {
        val item = arrayListPost[position]
        bindView(binding = holder.binding, item = item)
        holder.binding.setVariable(BR.item, item)
        holder.binding.executePendingBindings()

    }

    override fun getItemCount(): Int = arrayListPost.size

    private fun bindView(binding: ItemListPostLocationBinding, item: ResponseListPostLocation.ListPostLocationData) {
        binding.apply {
            setViewInteractive(binding,item)
        }
    }

    fun setViewInteractive(binding: ItemListPostLocationBinding, item: ResponseListPostLocation.ListPostLocationData) {
        item.apply {
            if (item.post_shares == true) {
                binding.btnLike.apply {
                    setIconAndSize(
                            R.drawable.ic_liked,
                            MyApplication.getInstance().getSizeWithScale(22.48),
                            MyApplication.getInstance().getSizeWithScale (20.0)
                    )
                    setColorText(R.color.colorMain)
                }
            }else{
                binding.btnLike.apply {
                    setIconAndSize(
                            R.drawable.ic_like,
                            MyApplication.getInstance().getSizeWithScale(22.48),
                            MyApplication.getInstance().getSizeWithScale(20.0)
                    )
                    setColorText(R.color.black)
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

    fun updateList(list: ArrayList<ResponseListPostLocation.ListPostLocationData>) {
        arrayListPost.clear()
        arrayListPost.addAll(list)
        notifyDataSetChanged()
        recyclerView.scrollToPosition(0)
    }
    fun addAllItem(list: ArrayList<ResponseListPostLocation.ListPostLocationData>){
        val oldSize = arrayListPost.size
        arrayListPost.addAll(list)
        notifyItemRangeChanged(oldSize-1,arrayListPost.size)
    }

    fun setLoaded() {
        isLoading = false
    }

    @JvmName("setLoadMore1")
    fun setLoadMore(loadMore: ILoadMore?) {
        this.loadMore = loadMore
    }

    class ListPostLocationViewHolder(val binding: ItemListPostLocationBinding) : RecyclerView.ViewHolder(binding.root) {
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
                btnComment.setSize(13F)
                btnLike.setSize(13F)
                btnShare.setSize(13F)
                viewLine.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(95.0)

                tvMonth.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(18.0))
                tvDay.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(48.0))
            }

        }
    }

    interface ILoadMore {
        fun onLoadMore()
    }
}