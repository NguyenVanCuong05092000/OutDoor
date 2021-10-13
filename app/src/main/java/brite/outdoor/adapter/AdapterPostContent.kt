package brite.outdoor.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.data.api_entities.response.ResponsePushPosts
import brite.outdoor.data.entities.PostContentEntity
import brite.outdoor.databinding.ItemPushPostContentBinding
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.PushPostViewModel
import kotlin.math.log

class AdapterPostContent(
    val mContent: Context,
    val heightButton: Int,
    val sizeIconAddImages: Int,
    val sizeTextContent: Float,
    val minHeightContent: Int,
    val scrollToNewContent: (lastPosition: Int) -> Unit,
    val transToSelectImages: (position: Int) -> Unit
) :
    RecyclerView.Adapter<AdapterPostContent.PostContentViewHolder>() {

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

    fun from(parent: ViewGroup): PostContentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPushPostContentBinding.inflate(layoutInflater, parent, false)
        return PostContentViewHolder(binding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostContentViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: PostContentViewHolder, position: Int) {
        holder.bind(position)
    }

    fun getItemAt(position: Int): PostContentEntity? {
        if (listContent?.size ?: 0 < position) return null
        return listContent!![position]
    }

    override fun getItemCount(): Int {
        return listContent?.size ?: 0
    }

    inner class PostContentViewHolder(private val binding: ItemPushPostContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                clAddImages.resizeHeight(heightButton)
                imgIconAddImages.resizeLayout(sizeIconAddImages, sizeIconAddImages)
                edtContent.minHeight = minHeightContent
                edtContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeTextContent)
                //
                    edtContent.addTextChangedListener(object :TextWatcher{
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            getItemAt(position)?.content=s.toString()
                        }

                        override fun afterTextChanged(s: Editable?) {

                        }

                    })
            }
        }

        private fun getLastPosition() : Int {
            return listContent!!.size - 1
        }


        fun bind(position: Int) {
            binding.apply {
                getItemAt(position)?.apply {
                edtContent.setText(content)

                if (listImg.value.isNullOrEmpty()) {
                    clAddImages.visibility = View.VISIBLE
                    vImagePreview.visibility = View.GONE
                } else {
                    clAddImages.visibility = View.GONE
                    vImagePreview.visibility = View.VISIBLE
                    vImagePreview.initData(
                        mContent,
                        listImg.value
                    )
                }

                clAddImages.setSingleClickSwitchScreen {
                    content = edtContent.text.toString()
                    if (position < listContent!!.size)
                        transToSelectImages(position)
                }
                vImagePreview.setSingleClick {
                    content = edtContent.text.toString()
                    if (position < listContent!!.size)
                        transToSelectImages(position)
                }
            }
            }
        }
    }
}