package brite.outdoor.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.databinding.ItemPickCameraBinding
import brite.outdoor.databinding.ItemPickImageBinding
import brite.outdoor.utils.resizeLayout
import brite.outdoor.utils.setSingleClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ImagePickAdapterAvatar(
        private val mContext: Context,
        private val wightItem: Int,
        private val wightCheckBox: Int,
        private val mgCheckBox: Int,
        val itemClickListener: (ImagePicker) ->Unit={_:ImagePicker->},
        private val textSize: Float
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listImage: List<ImagePicker> = ArrayList()
    private val TYPE_VIEW_CAMERA=1
    private val TYPE_VIEW_MEDIA=2

    fun setListImage(newListImage: ArrayList<ImagePicker>?) {
        newListImage?.let {
            this.listImage = newListImage
            notifyDataSetChanged()
        }
    }
    inner class CameraVewHolder(val binding: ItemPickCameraBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.apply {
                rootView.resizeLayout(wightItem, wightItem)
                ivImage.resizeLayout(
                    MyApplication.getInstance().getSizeWithScale(25.0),
                    MyApplication.getInstance().getSizeWithScale(25.0))
            }
        }
        fun bind(imagePicker: ImagePicker, position: Int){
            binding.apply {
                rootView.setSingleClick {
                    itemClickListener(imagePicker)
                }
            }

        }
    }

    inner class ImageViewHolder(val binding: ItemPickImageBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            binding.rootView.resizeLayout(wightItem, wightItem)
            binding.rlCheckBox.resizeLayout(wightCheckBox, wightCheckBox)
            binding.tvCountChecked.resizeLayout(wightCheckBox, wightCheckBox)
            binding.tvCountChecked.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize)
            (binding.rlCheckBox.layoutParams as ConstraintLayout.LayoutParams).marginEnd =
                    mgCheckBox
            (binding.rlCheckBox.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                    mgCheckBox
        }

        fun bind(imagePicker: ImagePicker, position: Int) {
            imagePicker.position = position
            imagePicker.isChecked = true
            Glide.with(mContext)
                    .load(imagePicker.uri)
                    .apply(RequestOptions().centerCrop())
                    .into(binding.ivImage)

            binding.rootView.setOnClickListener {
                itemClickListener(imagePicker)
            }
            binding.rlCheckBox.visibility=View.GONE
//            binding.rlCheckBox.setBackgroundResource(if (imagePicker.isChecked) R.drawable.bg_cb_selected else R.drawable.bg_cb)
//            binding.tvCountChecked.text =
//                    if (imagePicker.numberSelect != 0) imagePicker.numberSelect.toString() else ""
        }


    }

    private fun from(parent: ViewGroup,viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType==TYPE_VIEW_MEDIA){
            val binding = ItemPickImageBinding.inflate(layoutInflater, parent, false)
            return ImageViewHolder(binding)
        }else{
            val  binding = ItemPickCameraBinding.inflate(layoutInflater,parent,false)
            return CameraVewHolder(binding)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return from(parent,viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImagePickAdapterAvatar.ImageViewHolder){
            holder.apply {
                getItemAt(position)?.let {
                    holder.bind(it, position)
                }
            }

        }
        if (holder is ImagePickAdapterAvatar.CameraVewHolder){
            holder.apply {
                getItemAt(position)?.let {
                    holder.bind(it, position)
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (listImage[position].isCamera){
            return TYPE_VIEW_CAMERA
        }else{
            return  TYPE_VIEW_MEDIA
        }
    }

    private fun getItemAt(position: Int): ImagePicker? {
        listImage.let {
            if (position < it.size) {
                return it[position]
            }
        }
        return null
    }

    override fun getItemCount(): Int {
        return listImage.size
    }
}