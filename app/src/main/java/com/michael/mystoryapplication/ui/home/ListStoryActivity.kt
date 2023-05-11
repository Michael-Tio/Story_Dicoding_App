package com.michael.mystoryapplication.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.michael.mystoryapplication.R
import com.michael.mystoryapplication.databinding.ActivityListStoryBinding
import com.michael.mystoryapplication.ui.create.CreateStoryActivity
import com.michael.mystoryapplication.ui.login.MainActivity
import com.michael.mystoryapplication.ui.maps.MapsActivity

class ListStoryActivity : AppCompatActivity() {

    private var _binding: ActivityListStoryBinding? = null
    private val binding get() = _binding!!
    private val listViewModel: ListViewModel by viewModels {
        ViewModelFactory()
    }
    private val adapter = ListStoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.story_list)

        binding.fabAddStory.setOnClickListener{
            val createIntent = Intent(this@ListStoryActivity, CreateStoryActivity::class.java)
            startActivity(createIntent)
        }

    }

    override fun onResume() {
        super.onResume()
        getStoryList()
    }

    private fun showRecycleView(){
        binding.rvStory.apply{
            val layoutManager = LinearLayoutManager(this@ListStoryActivity)
            binding.rvStory.layoutManager = layoutManager

            val itemDecoration = DividerItemDecoration(this@ListStoryActivity, layoutManager.orientation)
            binding.rvStory.addItemDecoration(itemDecoration)

            setHasFixedSize(true)
        }
    }

    private fun getStoryList(){
        showRecycleView()


        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        listViewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_logout -> {
                val sharedPref = this.getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString(getString(R.string.user_token), "")
                editor.commit()

                val logoutIntent = Intent(this@ListStoryActivity, MainActivity::class.java)
                startActivity(logoutIntent)
                finish()
                return true
            }
            R.id.btn_maps ->{
                val mapsIntent = Intent(this@ListStoryActivity, MapsActivity::class.java)
                startActivity(mapsIntent)
                return true
            }
            else -> return true
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        finish()
    }


    companion object{
        var USER_TOKEN: String? = "USER_TOKEN"
    }
}