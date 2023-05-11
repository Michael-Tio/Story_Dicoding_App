package com.michael.mystoryapplication.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.michael.mystoryapplication.api.ApiConfig
import com.michael.mystoryapplication.responses.AllStoryResponse
import com.michael.mystoryapplication.responses.ListStoryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel: ViewModel() {
    private val _infoError = MutableLiveData<Boolean>()
    val infoError: LiveData<Boolean> = _infoError

    private val _storyInfo = MutableLiveData<List<ListStoryItem?>>()
    val storyInfo: LiveData<List<ListStoryItem?>> = _storyInfo

    fun getAllStory(token:String) {
        val client = ApiConfig.getApiService().getAllStoryForMap(token, 1)
        client.enqueue(object : Callback<AllStoryResponse> {
            override fun onResponse(
                call: Call<AllStoryResponse>,
                response: Response<AllStoryResponse>
            ) {
                if (response.isSuccessful) {
                    _infoError.value = false
                    _storyInfo.value = response.body()?.listStory
                } else {
                    _infoError.value = true
                }
            }

            override fun onFailure(call: Call<AllStoryResponse>, t: Throwable) {
                Log.e("MapsActivity", "onFailure: ${t.message}")
            }
        })
    }
}