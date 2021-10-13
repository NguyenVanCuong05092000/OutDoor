package brite.outdoor.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import brite.outdoor.data.remote.ApiController
import brite.outdoor.entity.Media
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(private val gson : Gson, apiController: ApiController) : ViewModel() {
    enum class STATE {
        NOTHING,
        CONSUMED
    }
    val actionState : MutableLiveData<CameraViewModel.STATE> = MutableLiveData(CameraViewModel.STATE.NOTHING)

    var  mediaData : MutableLiveData<List<Media>> = MutableLiveData()
}