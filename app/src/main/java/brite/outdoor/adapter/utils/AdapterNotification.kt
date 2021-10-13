package brite.outdoor.adapter.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.adapter.AdapterPeopleInteractive
import brite.outdoor.constants.AppConst
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.data.api_entities.response.ResponseFollowUser
import brite.outdoor.data.api_entities.response.ResponseListPush
import brite.outdoor.databinding.ItemNotificationBinding
import brite.outdoor.databinding.ItemPeopleInteractiveBinding
import brite.outdoor.utils.loadImageWithCustomCorners
import brite.outdoor.utils.resizeLayout
import brite.outdoor.utils.setSingleClick
import brite.outdoor.utils.urlImage

class AdapterNotification(val mContext: Context, var onClickItemListener: OnClickItemListener) : RecyclerView.Adapter<AdapterNotification.NotificationViewHolder>() {
    private var listNotification: ArrayList<ResponseListPush.ListPushData>? = null
    interface OnClickItemListener {
        fun onClickItem(entityNew: ResponseListPush.ListPushData?)
    }
    private var mOnClickItemListener: OnClickItemListener? = onClickItemListener

    fun getListNotification():ArrayList<ResponseListPush.ListPushData>?{
        return listNotification
    }
    fun updateList(list: ArrayList<ResponseListPush.ListPushData>?){
        list?.let {
            this.listNotification = list
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterNotification.NotificationViewHolder {
        return from(parent)
    }
    fun from(parent: ViewGroup): AdapterNotification.NotificationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemNotificationBinding.inflate(layoutInflater, parent, false)
        return NotificationViewHolder(binding)
    }
    override fun onBindViewHolder(holder: AdapterNotification.NotificationViewHolder, position: Int) {
        holder.bind(position)
    }
    fun getItemAt(position: Int):ResponseListPush.ListPushData?{
        if (listNotification?.size?:0 < position) return null
        return listNotification!![position]
    }
    override fun getItemCount(): Int {
        return listNotification?.size ?: 0
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                itemView.setSingleClick { mOnClickItemListener?.onClickItem(getItemAt(bindingAdapterPosition))}
            }
        }
        fun bind(position: Int){
            binding.apply {
                getItemAt(position)?.apply {
                    tvTitle.text = title
                    tvDetail.text = notice_details
                }
            }
        }
    }


    private var scaleValue = 0F
    private fun getScaleValue(): Float {
        if (scaleValue == 0F) {
            scaleValue =
                mContext.resources.displayMetrics.widthPixels * 1f / AppConst.SCREEN_WIDTH_DESIGN
        }
        return scaleValue
    }

    fun getSizeWithScale(sizeDesign: Double): Int {
        return (sizeDesign * getScaleValue()).toInt()
    }

}