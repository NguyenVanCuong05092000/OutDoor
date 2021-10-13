package brite.outdoor.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import brite.outdoor.databinding.FrmImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class FrmImage : BaseFragment<FrmImageBinding>(){

    private lateinit var image:String

    companion object {
        const val KEY_IMAGE = "KEY_IMAGE"
        fun newInstance(imageUrl: String):FrmImage{
            val fragment = FrmImage()
            val argument = Bundle()
            argument.putString(KEY_IMAGE,imageUrl)
            fragment.arguments = argument
            return fragment
        }
    }
    override fun loadControlsAndResize(binding: FrmImageBinding?) {

    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmImageBinding {
        return FrmImageBinding.inflate(inflater,container,false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        image=arguments?.get(KEY_IMAGE) as String
        Glide.with(this@FrmImage).load(image).apply(RequestOptions().centerCrop()).into(getBinding()!!.imageView)

    }

    override fun getCurrentFragment(): Int {
        return 0
    }

    override fun finish() {

    }
}