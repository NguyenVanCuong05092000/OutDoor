package brite.outdoor.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.data.api_entities.response.ListPostUserResponse
import brite.outdoor.data.entities.PostContentEntity
import brite.outdoor.databinding.ItemContentDetailBinding
import brite.outdoor.databinding.ItemPushPostContentBinding
import brite.outdoor.utils.resizeHeight
import brite.outdoor.utils.resizeLayout
import brite.outdoor.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.layour_image_picked_preview.view.*

class AdapterContentDetail(
    val mContent: Context,
    val heightButton: Int,
    val sizeTextContent: Float,
    val onClickImageListener: OnClickImageListener
) :
    RecyclerView.Adapter<AdapterContentDetail.ContentDetailViewHolder>() {
    interface OnClickImageListener {
        fun onClick(entityNew: PostContentEntity?,position: Int)
    }
    private var listContent : ArrayList<PostContentEntity>? = null
    fun getListContent():ArrayList<PostContentEntity>?{
        return listContent
    }
    fun updateList(list: ArrayList<PostContentEntity>?) {
        list?.let {
            this.listContent = list
            notifyDataSetChanged()
        }

    }

    fun from(parent: ViewGroup): ContentDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemContentDetailBinding.inflate(layoutInflater, parent, false)
        return ContentDetailViewHolder(binding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentDetailViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: ContentDetailViewHolder, position: Int) {
        holder.bind(position)
    }

    fun getItemAt(position: Int): PostContentEntity? {
        if (listContent?.size ?: 0 < position) return null
        return listContent!![position]
    }

    override fun getItemCount(): Int {
        return listContent?.size ?: 0
    }

    inner class ContentDetailViewHolder(private val binding: ItemContentDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeTextContent)
            }
        }

        private fun getLastPosition() : Int {
            return listContent!!.size - 1
        }


        fun bind(position: Int) {
            binding.apply {
                getItemAt(position)?.apply {
                tvContent.text = content

                if (listImg.value.isNullOrEmpty()) {
                    vImagePreview.visibility = View.GONE
                } else {
                    vImagePreview.visibility = View.VISIBLE
                    vImagePreview.initData(
                        mContent,
                        listImg.value
                    )
                }

                vImagePreview.imgMain.setOnClickListener {
                    if (position < listContent!!.size){
                        onClickImageListener.apply {
                            onClick(listContent?.get(position),0)
                        }
                    }
                }
                vImagePreview.imgChild1.setOnClickListener {
                        if (position < listContent!!.size){
                            onClickImageListener.apply {
                                onClick(listContent?.get(position),1)
                            }
                        }
                    }
                vImagePreview.imgChild2.setOnClickListener {
                        if (position < listContent!!.size){
                            onClickImageListener.apply {
                                onClick(listContent?.get(position),2)
                            }
                        }
                    }
                vImagePreview.imgChild3.setOnClickListener {
                        if (position < listContent!!.size){
                            onClickImageListener.apply {
                                onClick(listContent?.get(position),3)
                            }
                        }
                    }

            }
            }

        }
    }
}