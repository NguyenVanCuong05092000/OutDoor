package brite.outdoor.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import brite.outdoor.data.api_entities.response.ResponseLogin
import brite.outdoor.data.api_entities.response.ResponseRegisterToken
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.ApiResult
import brite.outdoor.utils.performGetOperation
import com.google.gson.Gson
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val gson : Gson, private val apiController: ApiController) : ViewModel() {



}