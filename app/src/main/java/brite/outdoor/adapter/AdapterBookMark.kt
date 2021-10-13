package brite.outdoor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.constants.AppConst
import brite.outdoor.data.api_entities.response.ResponseListBookMark
import brite.outdoor.databinding.ItemBookMarkBinding
import brite.outdoor.utils.*
import brite.outdoor.utils.loadImageWithCustomCorners

class AdapterBookMark(
    val context: Context,
    var listBookmark: ArrayList<ResponseListBookMark.ListBookMarkData>,
    var mOnClickItemListener: OnClickItemListener?
) : RecyclerView.Adapter<AdapterBookMark.BookmarkViewHolder>() {
    private var mContext: Context? = null

    private var popupWindow: PopupWindow? = null
    private var popupUnSave: CardView? = null

    interface OnClickItemListener {
        fun onClickUnBook(entitySavedPost: ResponseListBookMark.ListBookMarkData?, position: Int)
        fun onClickItem(entitySavedPost: ResponseListBookMark.ListBookMarkData?, position: Int)
        fun onClickShare(entitySavedPost: ResponseListBookMark.ListBookMarkData?,position: Int)
        fun onClickMenu(entitySavedPost: ResponseListBookMark.ListBookMarkData?,position: Int)

    }

    private var onClickItemListener: OnClickItemListener? = null


    fun updateData() {
        notifyDataSetChanged()
    }

    init {
        mContext = context
        onClickItemListener = mOnClickItemListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterBookMark.BookmarkViewHolder {

        return from(parent)
    }

    override fun onBindViewHolder(holder: AdapterBookMark.BookmarkViewHolder, position: Int) {
        holder.binData(getItemAt(position),position)
    }

    override fun getItemCount(): Int {
        return listBookmark.size
    }

    fun from(parent: ViewGroup): AdapterBookMark.BookmarkViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBookMarkBinding.inflate(layoutInflater, parent, false)
        return BookmarkViewHolder(binding)
    }

    private fun getItemAt(position: Int): ResponseListBookMark.ListBookMarkData? {
        if (position >= 0 && position < listBookmark.size) {
            return listBookmark[position]
        }
        return null
    }

    inner class BookmarkViewHolder(private val binding: ItemBookMarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                val sizeIcon = MyApplication.getInstance().getSizeWithScale(30.0)
                val tsTitle = MyApplication.getInstance().getSizeWithScaleFloat(18.0)
                val tsDesc = MyApplication.getInstance().getSizeWithScaleFloat(13.0)

                viewFooter.layoutParams.height = MyApplication.getInstance().getSizeWithScale(65.0)

                btnUnBookMark.resizeLayout(sizeIcon,sizeIcon)
                btnShare.resizeLayout(sizeIcon,sizeIcon)
                btnMenu.resizeLayout(sizeIcon,sizeIcon)

               clContent.resizeLayout(MyApplication.getInstance().getSizeWithScale(375.0), MyApplication.getInstance().getSizeWithScale(130.0))

//                clImgPost.resizeLayout(MyApplication.getInstance().getSizeWithScale(170.0),MyApplication.getInstance().getSizeWithScale(122.0))
                imgLocation.resizeLayout(MyApplication.getInstance().getSizeWithScale(20.0), MyApplication.getInstance().getSizeWithScale(20.0))
                tvTitlePost.setTextSizePX(tsTitle)
                tvDate.setTextSizePX(tsDesc)
                tvNamePostedBy.setTextSizePX(tsDesc)


                btnShare.setOnClickListener {
                    onClickItemListener?.apply {
                        onClickShare(getItemAt(bindingAdapterPosition),bindingAdapterPosition)
                    }
                }
                btnUnBookMark.setOnClickListener {
                    onClickItemListener?.apply {
                        onClickUnBook(getItemAt(bindingAdapterPosition), position)
                    }
                }
                btnMenu.setOnClickListener {
                    onClickItemListener?.apply {
                        onClickMenu(getItemAt(bindingAdapterPosition),bindingAdapterPosition)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun binData(entitySavedPost: ResponseListBookMark.ListBookMarkData?,position: Int) {
            binding.apply {
                try {
                    entitySavedPost?.let {
                        if (it.location_id == null || it.location_id == 0) {
                            imgLocation.setImageResource(R.drawable.ic_utensils_main)
                        } else {
                            imgLocation.setImageResource(R.drawable.ic_location)
                        }
                        if (it.getListContent()?.get(0)?.listImg?.size!! > 0)
                            tvDate.text = getDate(entitySavedPost.created_time)
                        tvNamePostedBy.text = it.user_name_created
                        tvTitlePost.text = it.title
                        clRoot.setOnClickListener {
                            mOnClickItemListener?.apply {
                                onClickItem(entitySavedPost, position = bindingAdapterPosition)
                            }
                        }
                        if (it.getListContent()?.size?:0 > 0 && (it.getListContent()?.get(0)?.listImg?.size?:0 > 0)) {
                            imgPost.loadImageUrl(
                                urlImage(it.getListContent()?.get(0)?.listImg?.get(0), it.url_prefix)
                            )
                        } else {
                            imgPost.setImageResource(R.drawable.ic_no_image)
                        }

                        progressBar.visibility = if (it.isShowLoading) View.VISIBLE else View.GONE

                        if(it.post_shares==true){
                            btnShare.setImageResource(R.drawable.img_shared)
                        }else{
                            btnShare.setImageResource(R.drawable.img_share)
                        }
                    }
                } catch (e: Exception) {
                    imgPost.setImageResource(R.drawable.ic_no_image)
                    e.printStackTrace()
                }
                // show view viewFooter bottom item
                if (listBookmark.size>2){
                    if (position==listBookmark.size-1){
                        viewFooter.visibility=View.VISIBLE
                    }else viewFooter.visibility=View.GONE
                }

            }
        }
    }

    fun removeItem(position: Int) {
        listBookmark.removeAt(position)
        notifyItemRemoved(position)
    }

}