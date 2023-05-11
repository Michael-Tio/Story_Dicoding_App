package com.michael.mystoryapplication.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.michael.mystoryapplication.R
import com.michael.mystoryapplication.databinding.ActivityRegisterBinding
import com.michael.mystoryapplication.ui.login.MainActivity

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val registerViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[RegisterViewModel::class.java]

        registerViewModel.infoError.observe(this){error ->
            if (error == true){
                showLoading(false)
                buttonOn(true)
                val errorMessage = registerViewModel.errorMessage.value
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else{
                showLoading(false)
                buttonOn(true)
                val loginIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(loginIntent)
                finish()
            }
        }

        binding.btnRegister.setOnClickListener{
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, R.string.error_field_empty, Toast.LENGTH_SHORT).show()
            }
            else{
                if (password.length < 8){
                    Toast.makeText(this, R.string.error_minimum_password, Toast.LENGTH_SHORT).show()
                }
                else{
                    registerViewModel.createUser(name, email, password)
                    showLoading(true)
                    buttonOn(false)
                }
            }
        }
    }
    private fun buttonOn(state: Boolean) {binding.btnRegister.isEnabled = state}
    private fun showLoading(state: Boolean) { binding.progressBarRegister.visibility = if (state) View.VISIBLE else View.GONE }
}