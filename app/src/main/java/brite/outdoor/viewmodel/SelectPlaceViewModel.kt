package brite.outdoor.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import brite.outdoor.data.api_entities.response.ResponseDetail
import brite.outdoor.data.entities.LocationEntity
import brite.outdoor.data.entities.ObjectSearch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectPlaceViewModel @Inject constructor(): ViewModel() {
    var locationSelected = MutableLiveData<LocationEntity>(null)
    var listLocation: MutableLiveData<ArrayList<ObjectSearch.ResultSearchEntity>> = MutableLiveData(arrayListOf())


}