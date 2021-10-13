package brite.outdoor.ui.activities

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import brite.outdoor.R
import brite.outdoor.adapter.ImagePagerAdapterMedia
import brite.outdoor.app.MyApplication
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.databinding.ActMediaBinding
import brite.outdoor.ui.fragments.FrmImage
import brite.outdoor.viewmodel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer
import kotlin.math.abs

@AndroidEntryPoint
class MediaActivity : AppCompatActivity() {

    private lateinit var binding:ActMediaBinding
    private var listImage:ArrayList<ImagePicker>?=null
    private lateinit var adapter:ImagePagerAdapterMedia
    private val viewModel by viewModels<MediaViewModel>()
    private var position : Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.act_media)
        val bundle = intent.extras
        listImage = bundle?.getParcelableArrayList("data")
        position = bundle?.getInt("indexImagePicker")
        initView()
        loadLocale()

    }

    fun initView(){
        initViewPager()

    }
    private fun onItemClickListener(item:ImagePicker){
    }
    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.releaseAllPlayers()
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseAllPlayers()
        val language = PrefManager.getInstance(this).getString(PrefConst.PREF_MULTI_LANGUAGE)
        language?.let { setLocale(it) }

    }
    @SuppressLint("ClickableViewAccessibility")
    private fun initViewPager(){
        adapter= ImagePagerAdapterMedia(this,listImage,itemClickListener ={imagePicker: ImagePicker -> onItemClickListener(imagePicker) },viewModel.playersMap,viewModel.viewCloseMap,binding.viewPager,viewModel,onShowViewCloseIfNeed = {position -> showViewClose(position)  }){ id, needPlay ->
            if (needPlay) {
                viewModel.playIndexThenPausePreviousPlayer(id)

            } else {
                viewModel.pauseCurrentPlayingVideo()
            }

        }
        binding.viewPager.adapter=adapter
        binding.viewPager.apply {
            offscreenPageLimit=3
            this@MediaActivity.position?.let {
                this.setCurrentItem(it,false)
            }
            clipChildren = false
            clipToPadding = false
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(MyApplication.getInstance().getSizeWithScale(40.0)))
            setPageTransformer(compositePageTransformer)

        }
    }
    private fun showViewClose(position:Int){
        viewModel.showIndexViewClose(position,this)
    }

    override fun onPause() {
        viewModel.pauseAllVideo()
        super.onPause()
    }

    private fun loadLocale(){
        val language = PrefManager.getInstance(this).getString(PrefConst.PREF_MULTI_LANGUAGE)
        language?.let {
            if (it=="") PrefManager.getInstance(this).writeString(PrefConst.PREF_MULTI_LANGUAGE,"vi")
            setLocale(language)
        }
    }
    private fun setLocale(lang:String){
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale=locale

        this.resources.updateConfiguration(config,this.resources.displayMetrics)

    }

}