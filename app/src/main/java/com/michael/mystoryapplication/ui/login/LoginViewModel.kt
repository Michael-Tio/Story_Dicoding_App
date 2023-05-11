package com.michael.mystoryapplication.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michael.mystoryapplication.api.ApiConfig
import com.michael.mystoryapplication.responses.LoginResponse
import com.michael.mystoryapplication.responses.LoginResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {

    private val _infoError = MutableLiveData<Boolean>()
    val infoError: LiveData<Boolean> = _infoError

    private val _userInfo = MutableLiveData<LoginResult>()
    val userInfo: LiveData<LoginResult> = _userInfo

    fun checkLogin(pEmail:String, pPassword:String) {
        val client = ApiConfig.getApiService().loginUser(pEmail, pPassword)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    _infoError.value = false
                    _userInfo.value = response.body()?.loginResult
                } else {
                    _infoError.value = true
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("MainActivity", "onFailure: ${t.message}")
            }
        })
    }
}