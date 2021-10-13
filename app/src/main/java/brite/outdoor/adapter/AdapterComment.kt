package brite.outdoor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.constants.AppConst
import brite.outdoor.data.api_entities.response.ResponseListComment
import brite.outdoor.databinding.ItemCommentBinding
import brite.outdoor.utils.*

class AdapterComment(val context: Context,val userId: Int?, val urlPrefixAvatar: String?, var onClickReplyListener: OnClickReplyListener?, onClickCommentReplyListener: AdapterCommentReply.OnClickCommentReplyListener? ) :
    RecyclerView.Adapter<AdapterComment.ViewHolder>() {
    private var mContext: Context
    var arrayListComment: ArrayList<ResponseListComment.ListCommentData> = ArrayList()
    var isLoading:Boolean=false
    private var urlPrefixComment: String? = null

    interface OnClickReplyListener {
        fun onClickReply(comment: ResponseListComment.ListCommentData?,position: Int)
        fun onClickDelete(comment: ResponseListComment.ListCommentData?, position: Int)
        fun onClickUserName(comment: ResponseListComment.ListCommentData?)
    }

    private var mOnClickReplyListener: OnClickReplyListener? =null
    private var mOnClickCommentReplyListener: AdapterCommentReply.OnClickCommentReplyListener? =null
    init {
        mContext = context
        mOnClickReplyListener = onClickReplyListener
        mOnClickCommentReplyListener = onClickCommentReplyListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterComment.ViewHolder {
        return from(parent)
    }
    fun from(parent: ViewGroup): AdapterComment.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCommentBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterComment.ViewHolder, position: Int) {
            holder.binData(getItemAt(position))
    }

    override fun getItemCount(): Int {
        return if (arrayListComment == null) 0 else arrayListComment.size
    }
    fun updateList(list: ArrayList<ResponseListComment.ListCommentData>, urlPrefixComment: String?){
        arrayListComment.clear()
        arrayListComment.addAll(list)
        this.urlPrefixComment = urlPrefixComment
        notifyDataSetChanged()
    }
    private fun getItemAt(position: Int): ResponseListComment.ListCommentData? {
        if (position >= 0 && position < arrayListComment.size) {
            return arrayListComment[position]
        }
        return null
    }
    inner class ViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                imgAvatar.layoutParams?.width = getSizeWithScale(46.0)
                imgAvatar.layoutParams?.height = getSizeWithScale(46.0)
                imgComment.layoutParams?.width = getSizeWithScale(188.0)
                imgComment.layoutParams?.height = getSizeWithScale(129.0)
                tvReply.setOnClickListener {
                    mOnClickReplyListener?.apply {
                        onClickReply(getItemAt(bindingAdapterPosition),bindingAdapterPosition)
                    }
                }
                tvDelete.setOnClickListener {
                    mOnClickReplyListener?.apply {
                        onClickDelete(getItemAt(bindingAdapterPosition), position)
                    }
                }
                tvName.setOnClickListener {
                    mOnClickReplyListener?.apply {
                        onClickUserName(getItemAt(bindingAdapterPosition))
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n", "LogNotTimber")
        fun binData(entityComment: ResponseListComment.ListCommentData?) {
            binding.apply {
                entityComment?.apply {
                    itemView.setSingleClick { AppUtils.hideKeyboard(itemView) }
                    imgAvatar.loadImageWithCustomCorners(this.avatar_user?.let {
                        urlImage(
                            it,
                            urlPrefixAvatar
                        )
                    }, 100)
                    if (!this.image.isNullOrEmpty() && !urlPrefixComment.isNullOrEmpty()){
                        imgComment.loadImageWithCustomCorners(urlImage(this.image, urlPrefixComment),10)
                        imgComment.visibility = View.VISIBLE
                    }else imgComment.visibility = View.GONE
                    tvContentComment.text = comment
//                    tvContentComment.setText(Html.fromHtml(tvContentComment), TextView.BufferType.SPANNABLE)
                    tvDate.text = getDate(modified_time)
                    tvName.text = user_name_created
                    if (userId == entityComment.created_id ){
                        tvDelete.visibility = View.VISIBLE
                    }else{
                        tvDelete.visibility = View.GONE
                    }
                    if (!isSending){
                        tvSending.visibility = View.GONE
                        clDateAndReply.visibility = View.VISIBLE
                    }else{
                        tvSending.visibility = View.VISIBLE
                        clDateAndReply.visibility = View.GONE
                    }
                    if (entityComment.post_comments_children!= null) {
                        val adapterCommentReply = AdapterCommentReply(mContext, userId, entityComment,urlPrefixAvatar, urlPrefixComment, mOnClickCommentReplyListener)
                        rcItemComment.layoutManager = LinearLayoutManager(mContext)
                        rcItemComment.adapter = adapterCommentReply
                    }
                }
            }
        }

    }

    //Start Resize
    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue =
                context.resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
        }
        return scaleValue
    }

    fun getSizeWithScale(sizeDesign: Double): Int {
        return (sizeDesign * getScaleValue()).toInt()
    }
    fun removeItem(position: Int) {
        arrayListComment.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, arrayListComment.size)
//        notifyDataSetChanged()
    }


}