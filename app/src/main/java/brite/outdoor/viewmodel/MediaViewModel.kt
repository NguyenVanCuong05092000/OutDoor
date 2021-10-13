package brite.outdoor.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import brite.outdoor.R
import brite.outdoor.data.remote.ApiController
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(private val gson : Gson, apiController: ApiController) : ViewModel(){
    // for hold all players generated
    var playersMap: MutableMap<Int, SimpleExoPlayer>  = mutableMapOf()
    // for hold current player
    var currentPlayingVideo: Pair<Int, SimpleExoPlayer>? = null
    fun releaseAllPlayers(){
        playersMap.map {
            it.value.release()
        }
    }

    // call when item recycled to improve performance
    fun releaseRecycledPlayers(index: Int){
        playersMap[index]?.release()
    }

    // call when scroll to pause any playing player
    fun pauseCurrentPlayingVideo(){
        if (currentPlayingVideo != null){
            currentPlayingVideo?.second?.playWhenReady = false
        }
    }

    fun playIndexThenPausePreviousPlayer(index: Int){
        if (playersMap.get(index)?.playWhenReady == false) {
            pauseCurrentPlayingVideo()
            playersMap.get(index)?.playWhenReady = true
            currentPlayingVideo = Pair(index, playersMap.get(index)!!)
        }

    }

    // pause all video when screen onPause
    fun pauseAllVideo(){
        playersMap.map {
            it.value.playWhenReady=false

        }
    }
    var viewCloseMap: MutableMap<Int, View>  = mutableMapOf()
    var currentView: Pair<Int, View>? = null

    private fun hideIndexViewClose(){
        if (currentView !=null){
            currentView?.second?.visibility=View.GONE
        }
    }
    fun showIndexViewClose(index: Int,context:Context){
        if (viewCloseMap.get(index)?.isVisible == false) {
            hideIndexViewClose()
            viewCloseMap.get(index)?.visibility = View.VISIBLE
            viewCloseMap.get(index)?.animation = AnimationUtils.loadAnimation(context, R.anim.to_top)
            currentView = Pair(index,viewCloseMap.get(index)!!)
            Handler(Looper.getMainLooper()).postDelayed({
                if (viewCloseMap.get(index)?.isVisible==true){
                    viewCloseMap.get(index)?.visibility=View.GONE
                    viewCloseMap.get(index)?.animation = AnimationUtils.loadAnimation(context,R.anim.out_top)
                }
            }, 2000L)

        }

    }

}