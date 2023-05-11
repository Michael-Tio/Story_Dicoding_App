package com.michael.mystoryapplication.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.michael.mystoryapplication.R
import com.michael.mystoryapplication.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private var _binding: ActivityDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.story_details)

        val name = intent.getStringExtra(NAME)
        val desc = intent.getStringExtra(DESC)
        val photo = intent.getStringExtra(PHOTO)

        Glide.with(this)
            .load(photo)
            .into(binding.ivDetailPhoto)

        binding.tvDetailName.text = name
        binding.tvDetailDescription.text = desc
    }



    companion object {
        const val NAME = "NAME"
        const val DESC = "DESC"
        const val PHOTO = "PHOTO"
    }
}