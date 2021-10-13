package brite.outdoor.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import brite.outdoor.data.entities.ImagePicker
import brite.outdoor.data.entities.ListImagePicked
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.repository.AppRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReadExternalStorageViewModel @Inject constructor(private val appRepository : AppRepository, apiController: ApiController, private val gson: Gson): ViewModel() {
    val readMediaExternalStorage = MutableLiveData<ArrayList<ImagePicker>>()
}