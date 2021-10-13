package brite.outdoor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.data.api_entities.response.ListPostUserResponse
import brite.outdoor.databinding.ItemMyPostBinding
import brite.outdoor.utils.*


class AdapterMyPost(
    var listMyPost : ArrayList<ListPostUserData>,
    private val onClickItemListener: OnClickItemListener,
    private val userId: String?,
    private val isBottomNav:Boolean=true
) :
    RecyclerView.Adapter<AdapterMyPost.MyPostViewHolder>() {


    interface OnClickItemListener {
        fun onClickMenu(entityMyPage: ListPostUserData?, position: Int?)
        fun onClickItem(entityMyPage: ListPostUserData?,position: Int)
        fun onClickLike(entityNew: ListPostUserData?, position: Int)
        fun onClickComment(entityNew: ListPostUserData?,position: Int)
        fun onClickShare(entityNew: ListPostUserData?,position: Int)
    }


    fun updateData() {
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterMyPost.MyPostViewHolder {
        return from(parent)
    }

    fun from(parent: ViewGroup): AdapterMyPost.MyPostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMyPostBinding.inflate(layoutInflater, parent, false)
        return MyPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterMyPost.MyPostViewHolder, position: Int) {
        val entityNews = listMyPost[position]
        holder.binData(entityNews,position)

    }

    override fun getItemCount(): Int {
        return listMyPost.size
    }

    inner class MyPostViewHolder(private val binding: ItemMyPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                clRoot.resizeHeight(MyApplication.getInstance().getSizeWithScale(350.0))
                viewFooter.layoutParams.height = MyApplication.getInstance().getSizeWithScale(55.0)
                clTitleAndImg.resizeHeight(MyApplication.getInstance().getSizeWithScale(260.0))
                imgLocation.resizeLayout(MyApplication.getInstance().getSizeWithScale(24.0), MyApplication.getInstance().getSizeWithScale(24.0))
                imgMenu.layoutParams?.width = MyApplication.getInstance().getSizeWithScale(37.0)
                imgMenu.layoutParams?.height = MyApplication.getInstance().getSizeWithScale(49.0)
                clButtonContainer.resizeHeight(MyApplication.getInstance().getSizeWithScale(40.0))
                spBottom.resizeHeight(MyApplication.getInstance().getSizeWithScale(21.0))


                tvMonth.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(18.0))
                tvDay.setTextSizePX(MyApplication.getInstance().getSizeWithScaleFloat(48.0))

            }
        }

        @SuppressLint("SetTextI18n")
        fun binData(entityMyPage: ListPostUserData?,position: Int) {
            binding.apply {
                entityMyPage?.let { nonNullPost ->
                    if (nonNullPost.location_id == null || nonNullPost.location_id == 0) {
                        imgLocation.setImageResource(R.drawable.ic_utensils_main)
                    } else {
                        imgLocation.setImageResource(R.drawable.ic_location)
                    }
                    try {
                        if (nonNullPost.getListContent()?.size?:0 > 0 && (nonNullPost.getListContent()?.get(0)?.listImg?.size?:0 > 0)) {
                            imgPost.loadImageWithCustomCorners(
                                urlImage(nonNullPost.getListContent()?.get(0)?.listImg?.get(0), nonNullPost.url_prefix),1
                            )
                        } else {
                            imgPost.setImageResource(R.drawable.ic_no_image)
                        }
                    } catch (e: Exception) {
                        imgPost.setImageBitmap(null)
                        e.printStackTrace()
                    }
                    nonNullPost.post_likes?.let {
                        setViewInteractive(it, nonNullPost)
                    }
                    nonNullPost.created_time?.let {
                        if (it.isNotEmpty()) {
                            tvDay.setTimeDay(it)
                            tvMonth.setTimeMonth(it)
                        }
                    }

                    tvTitle.text = nonNullPost.title
                    progressBar.visibility = if (nonNullPost.isShowLoading) View.VISIBLE else View.GONE

                    if (isBottomNav){
                         if (position==listMyPost.size-1){
                       viewFooter.visibility=View.VISIBLE
                   }else viewFooter.visibility=View.GONE
                    }
                    if (userId?.isNotEmpty() == true){
                        if (userId.toInt() == entityMyPage.created_id){
                            imgMenu.visibility = View.VISIBLE
                        }else imgMenu.visibility = View.GONE
                    }

                    // listener item
                    clTitleAndImg.setSingleClick {
                        onClickItemListener.onClickItem(
                            getItemAt(bindingAdapterPosition),
                            position = bindingAdapterPosition
                        )
                    }
                    btnLike.setSingleClick {
                        onClickItemListener.onClickLike(
                            getItemAt(bindingAdapterPosition),
                            bindingAdapterPosition
                        )
                    }
                    btnComment.setSingleClick {
                        onClickItemListener.onClickComment(getItemAt(bindingAdapterPosition),bindingAdapterPosition)
                    }
                    btnShare.setSingleClick {
                        onClickItemListener.onClickShare(getItemAt(bindingAdapterPosition),bindingAdapterPosition)
                    }
                    imgMenu.setSingleClick {
                        onClickItemListener.onClickMenu(
                            getItemAt(bindingAdapterPosition),
                            bindingAdapterPosition
                        )
                    }


                }
            }

        }

        private fun setViewInteractive(statusLike: Boolean, listMyPost: ListPostUserData?) {
            binding.apply {
                listMyPost?.let {
                    when (statusLike) {
                        ListPostUserResponse.STATE_LIKE -> {
                            btnLike.apply {
                                setIconAndSize(
                                    R.drawable.ic_liked,
                                    MyApplication.getInstance().getSizeWithScale(22.48),
                                    MyApplication.getInstance().getSizeWithScale(20.0)
                                )
                                setColorText(R.color.colorMain)
                            }
                        }
                        ListPostUserResponse.STATE_UNLIKE -> {
                            btnLike.apply {
                                setIconAndSize(
                                    R.drawable.ic_like,
                                    MyApplication.getInstance().getSizeWithScale(22.48),
                                    MyApplication.getInstance().getSizeWithScale(20.0)
                                )
                                setColorText(R.color.black)
                            }

                        }
                    }

                    btnLike.setLikeCommentShare(listMyPost.like_count)
                    btnComment.setLikeCommentShare(listMyPost.comment_count)
                    btnShare.setLikeCommentShare(listMyPost.share_count)
                    btnShare.apply {
                        if (listMyPost.post_shares==true){
                            btnShare.setIconAndSize(
                                R.drawable.ic_shared,
                                MyApplication.getInstance().getSizeWithScale(20.5),
                                MyApplication.getInstance().getSizeWithScale(20.5)
                            )
                            setColorText(R.color.colorMain)
                        }else{
                            btnShare.setIconAndSize(
                                R.drawable.ic_share,
                                MyApplication.getInstance().getSizeWithScale(20.5),
                                MyApplication.getInstance().getSizeWithScale(20.5)
                            )
                            setColorText(R.color.black)
                        }
                    }
                    btnComment.setIconAndSize(
                        R.drawable.ic_comment,
                        MyApplication.getInstance().getSizeWithScale(20.0),
                        MyApplication.getInstance().getSizeWithScale(20.0)
                    )

                }
            }
        }
    }
    
    private fun getItemAt(position: Int) : ListPostUserData? {
        if (position >= 0 && position < listMyPost.size) {
            return listMyPost[position]
        }
        return null
    }
    fun removeItem(position: Int){
        listMyPost.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,listMyPost.size)
//        notifyDataSetChanged()
    }

}