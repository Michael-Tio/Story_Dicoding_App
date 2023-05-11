package com.michael.mystoryapplication.ui.create

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michael.mystoryapplication.api.ApiConfig
import com.michael.mystoryapplication.responses.BasicResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateViewModel: ViewModel() {
    private val _infoError = MutableLiveData<Boolean>()
    val infoError: LiveData<Boolean> = _infoError

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun addStory(pToken: String, pDesc:RequestBody, pPhoto:MultipartBody.Part){
        //        _isLoadingAdd.value = true
        val client = ApiConfig.getApiService().addStory(pToken, pDesc, pPhoto)
        client.enqueue(object : Callback<BasicResponse> {
            override fun onResponse(
                call: Call<BasicResponse>,
                response: Response<BasicResponse>
            ) {
                if (response.isSuccessful) {
                    _infoError.value = false
//                    _isLoadingLogin.value = false
                } else {
                    _infoError.value = true
                    _errorMessage.value = response.message()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
//                _isLoadingLogin.value = false
                Log.e("RegisterActivity", "onFailure: ${t.message}")
            }
        })
    }
}