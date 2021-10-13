package brite.outdoor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.constants.AppConst
import brite.outdoor.data.api_entities.response.ResponseListComment
import brite.outdoor.databinding.ItemReplyCommentBinding
import brite.outdoor.utils.*

class AdapterCommentReply(val context: Context, val userId: Int?, var dataComment: ResponseListComment.ListCommentData?, val urlPrefixAvatar: String?, val urlPrefixComment: String?, var onClickReplyListener: OnClickCommentReplyListener?) :
    RecyclerView.Adapter<AdapterCommentReply.ViewHolder>() {
    private var mContext: Context? = null
    private var arrayListCommentReply: ArrayList<ResponseListComment.ListContentReplyData>? = null
    interface OnClickCommentReplyListener {
        fun onClickReply(comment: ResponseListComment.ListCommentData?, commentReply: ResponseListComment.ListContentReplyData?,position: Int)
        fun onClickSeeMore(threadId: Int?, lastestId: Int?)
        fun onClickDelete(comment: ResponseListComment.ListCommentData?, commentReply: ResponseListComment.ListContentReplyData?, position: Int)
        fun onClickUserName(commentReply: ResponseListComment.ListContentReplyData?)
    }
    private var mOnClickReplyListener: OnClickCommentReplyListener? =null
    init {
        mContext = context
        arrayListCommentReply = dataComment?.post_comments_children
        mOnClickReplyListener = onClickReplyListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterCommentReply.ViewHolder {
        return from(parent)
    }
    fun from(parent: ViewGroup): AdapterCommentReply.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReplyCommentBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: AdapterCommentReply.ViewHolder, position: Int) {
            holder.binData(getItemAt(position), position)
    }

    override fun getItemCount(): Int {
        return if (arrayListCommentReply == null) 0 else arrayListCommentReply!!.size
    }
    private fun getItemAt(position: Int): ResponseListComment.ListContentReplyData? {
        if (position >= 0 && position < arrayListCommentReply?.size!!) {
            return arrayListCommentReply!![position]
        }
        return null
    }
    inner class ViewHolder(private val binding: ItemReplyCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                imgAvatar.layoutParams?.width = getSizeWithScale(46.0)
                imgAvatar.layoutParams?.height = getSizeWithScale(46.0)
                imgComment.layoutParams?.width = getSizeWithScale(188.0)
                imgComment.layoutParams?.height = getSizeWithScale(129.0)
                tvDelete.setOnClickListener {
                    mOnClickReplyListener?.apply {
                        onClickDelete(dataComment,getItemAt(bindingAdapterPosition), position)
                    }
                }
                tvName.setOnClickListener {
                    mOnClickReplyListener?.apply {
                        onClickUserName(getItemAt(bindingAdapterPosition))
                    }
                }
                tvReply.setSingleClick {
                    onClickReplyListener?.onClickReply(dataComment, getItemAt(bindingAdapterPosition),bindingAdapterPosition)
                }
                btnSeeMore.setSingleClick {
                    onClickReplyListener?.onClickSeeMore(dataComment?.id, getItemAt(bindingAdapterPosition)?.id)
                }
                itemView.setSingleClick { AppUtils.hideKeyboard(itemView) }
            }
        }

        @SuppressLint("SetTextI18n")
        fun binData(entityComment: ResponseListComment.ListContentReplyData?, position: Int) {
            binding.apply {
                entityComment?.apply {
                    imgAvatar.loadImageWithCustomCorners(this.avatar_user?.let {
                        urlImage(it, urlPrefixAvatar) }, 100)

                    if (!this.image.isNullOrEmpty() && !urlPrefixComment.isNullOrEmpty()){
                        imgComment.loadImageWithCustomCorners(urlImage(this.image, urlPrefixComment),10)
                        imgComment.visibility = View.VISIBLE
                    }else imgComment.visibility = View.GONE
                    val contentComment = "<font color='blue'>$user_name_reply</font> $comment"
                    tvContentComment.setText(Html.fromHtml(contentComment), TextView.BufferType.SPANNABLE)
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
                    if (arrayListCommentReply?.size != dataComment?.total_children && position == arrayListCommentReply?.size?.minus(1)&& !isSending){
                        btnSeeMore.visibility = View.VISIBLE
                    }else  btnSeeMore.visibility = View.GONE
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
}