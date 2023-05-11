package com.michael.mystoryapplication.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michael.mystoryapplication.api.ApiConfig
import com.michael.mystoryapplication.responses.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {

    private val _infoError = MutableLiveData<Boolean>()
    val infoError: LiveData<Boolean> = _infoError

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun createUser(pName: String, pEmail:String, pPassword:String){
        val client = ApiConfig.getApiService().registerUser(pName, pEmail, pPassword)
        client.enqueue(object : Callback<BasicResponse> {
            override fun onResponse(
                call: Call<BasicResponse>,
                response: Response<BasicResponse>
            ) {
                if (response.isSuccessful) {
                    _infoError.value = false
                } else {
                    _errorMessage.value = response.message()
                    _infoError.value = true
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Log.e("RegisterActivity", "onFailure: ${t.message}")
            }
        })
    }
}