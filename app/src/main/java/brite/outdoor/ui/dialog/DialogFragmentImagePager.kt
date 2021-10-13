package brite.outdoor.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import brite.outdoor.adapter.ImagePagerAdapter
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_IMAGE_PICK
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.databinding.FrmImagePagerBinding
import brite.outdoor.ui.fragments.FrmImage
import kotlinx.android.synthetic.main.popop_un_save.*
import kotlin.math.abs


class DialogFragmentImagePager : DialogFragment() {

    private var binding:FrmImagePagerBinding?=null
    private lateinit var adapter:ImagePagerAdapter
    private var listImage:ArrayList<ImagePicker>?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= FrmImagePagerBinding.inflate(inflater,container,false)
        initView()
        return binding!!.root
    }

    fun initView(){

        val listFragment= arrayListOf<Fragment>()

        listImage?.let {
           it.forEach {
               listFragment.add(FrmImage.newInstance(it.uri.toString()))
           }
       }
        adapter= ImagePagerAdapter(requireContext(),listImage,itemClickListener ={imagePicker: ImagePicker -> onItemClickListener(imagePicker) } )
        binding!!.viewPager.adapter=adapter

        binding?.apply {
            viewPager.adapter = adapter
            viewPager.clipToPadding = false
            viewPager.clipChildren = false
            viewPager.offscreenPageLimit = 3
            viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(50))
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            viewPager.setPageTransformer(compositePageTransformer)
        }
    }
    fun setData(list: ArrayList<ImagePicker>){
        this.listImage=list
    }
    private fun onItemClickListener(item:ImagePicker){}

}