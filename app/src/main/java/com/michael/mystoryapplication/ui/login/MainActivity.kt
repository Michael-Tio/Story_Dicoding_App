package com.michael.mystoryapplication.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.michael.mystoryapplication.R
import com.michael.mystoryapplication.databinding.ActivityMainBinding
import com.michael.mystoryapplication.ui.home.ListStoryActivity
import com.michael.mystoryapplication.ui.home.ListStoryActivity.Companion.USER_TOKEN
import com.michael.mystoryapplication.ui.register.RegisterActivity

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val sharedPref = this.getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE)
        var usertoken = sharedPref.getString(getString(R.string.user_token), "")
        if (usertoken != ""){
            USER_TOKEN = usertoken
            moveToHome()
            finish()
        }

        val loginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginViewModel::class.java]
        loginViewModel.infoError.observe(this){ error ->
            if (error){
                Toast.makeText(this, getString(R.string.error_incorrect_email_password), Toast.LENGTH_SHORT).show()
                showLoading(false)
                buttonOn(true)
            }
            else{
                getUserInfo(sharedPref)
                showLoading(false)
                buttonOn(true)
                moveToHome()
                finish()
            }
        }

        binding.txtSignUp.setOnClickListener{
            val signUpIntent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(signUpIntent)
        }

        binding.btnLogin.setOnClickListener{
            buttonOn(false)
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, getString(R.string.error_field_empty), Toast.LENGTH_SHORT).show()
            }
            else{
                if (password.length < 8){
                    Toast.makeText(this, getString(R.string.error_minimum_password), Toast.LENGTH_SHORT).show()
                }
                else{
                    checkLogin(email, password)
                    showLoading(true)


                }
            }
        }

        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val anEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val anPassword = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val anLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val anSignUp1 = ObjectAnimator.ofFloat(binding.textView2, View.ALPHA, 1f).setDuration(500)
        val anSignUp2 = ObjectAnimator.ofFloat(binding.txtSignUp, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(anSignUp1, anSignUp2)
        }
        AnimatorSet().apply {
            playSequentially(anEmail, anPassword, anLogin, together)
            start()
        }
    }

    private fun checkLogin(pEmail: String, pPassword:String){
        val loginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginViewModel::class.java]
        loginViewModel.checkLogin(pEmail, pPassword)
    }

    private fun getUserInfo(sharedPref: SharedPreferences){
        val loginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[LoginViewModel::class.java]
        loginViewModel.userInfo.observe(this){
            val fulltoken = "Bearer ${it.token}"
            USER_TOKEN = fulltoken

            val editor = sharedPref.edit()
            editor.putString(getString(R.string.user_token), fulltoken)
            editor.apply()
        }
    }

    private fun moveToHome(){
        val homeIntent = Intent(this@MainActivity, ListStoryActivity::class.java)
        startActivity(homeIntent)
    }
    private fun buttonOn(state: Boolean) {binding.btnLogin.isEnabled = state}
    private fun showLoading(state: Boolean) { binding.progressBarLogin.visibility = if (state) View.VISIBLE else View.GONE }
}