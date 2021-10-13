package brite.outdoor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.constants.AppConst
import brite.outdoor.data.api_entities.response.ListPostUserData
import brite.outdoor.data.api_entities.response.ResponseFollowUser
import brite.outdoor.data.api_entities.response.ResponseSearchUser
import brite.outdoor.databinding.ItemPeopleInteractiveBinding
import brite.outdoor.utils.*

class AdapterPeopleInteractive(val mContext: Context,val urlPrefixAvatar: String?, var onClickItemListener: OnClickItemListener) : RecyclerView.Adapter<AdapterPeopleInteractive.PeopleInteractiveViewHolder>() {
    private var listFollowUser: ArrayList<ResponseFollowUser.FollowUserData>? = null
    interface OnClickItemListener {
        fun onClickItem(entityFollowUser: ResponseFollowUser.FollowUserData?)
        fun onClickFollow(entityFollowUser: ResponseFollowUser.FollowUserData?)
    }
    private var mOnClickItemListener: OnClickItemListener? = onClickItemListener

    fun getListFollowUser():ArrayList<ResponseFollowUser.FollowUserData>?{
        return listFollowUser
    }
    fun updateList(list: ArrayList<ResponseFollowUser.FollowUserData>?){
        list?.let {
            this.listFollowUser = list
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPeopleInteractive.PeopleInteractiveViewHolder {
        return from(parent)
    }
    fun from(parent: ViewGroup): PeopleInteractiveViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPeopleInteractiveBinding.inflate(layoutInflater, parent, false)
        return PeopleInteractiveViewHolder(binding)
    }
    override fun onBindViewHolder(holder: AdapterPeopleInteractive.PeopleInteractiveViewHolder, position: Int) {
        holder.bind(position)
    }
    fun getItemAt(position: Int):ResponseFollowUser.FollowUserData?{
        if (listFollowUser?.size?:0 < position) return null
        return listFollowUser!![position]
    }
    override fun getItemCount(): Int {
        return listFollowUser?.size ?: 0
    }

    inner class PeopleInteractiveViewHolder(private val binding: ItemPeopleInteractiveBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                clItemPeopleInteractive.resizeHeight(getSizeWithScale(80.0))
                imgAvatar.resizeLayout(getSizeWithScale(60.0), getSizeWithScale(60.0))
                btnFollow.resizeLayout(getSizeWithScale(110.0), getSizeWithScale(40.0))
                itemView.setSingleClick { mOnClickItemListener?.onClickItem(getItemAt(bindingAdapterPosition))}
                btnFollow.setSingleClick {
                    onClickItemListener.onClickFollow(getItemAt(bindingAdapterPosition))
                }
            }
        }
        fun bind(position: Int){
            binding.apply {
                getItemAt(position)?.apply {
                    tvName.text = username
                    imgAvatar.loadImageWithCustomCorners(urlImage(avatar_user, urlPrefixAvatar), 100)
                    when (followed_flag) {
                        1 -> {
                            btnFollow.setText(R.string.lblUnFollow)
                            btnFollow.isSelected = false
                        }
                        0 -> {
                            btnFollow.setText(R.string.lblFollow)
                            btnFollow.isSelected = false
                        }
                        else -> btnFollow.visibility = View.GONE
                    }
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