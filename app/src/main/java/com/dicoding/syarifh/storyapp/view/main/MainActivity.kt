package com.dicoding.syarifh.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.syarifh.storyapp.R
import com.dicoding.syarifh.storyapp.data.LoadingStateAdapter
import com.dicoding.syarifh.storyapp.data.StoryAdapter
import com.dicoding.syarifh.storyapp.databinding.ActivityMainBinding
import com.dicoding.syarifh.storyapp.view.ViewModelFactory
import com.dicoding.syarifh.storyapp.view.addstory.AddStoryActivity
import com.dicoding.syarifh.storyapp.view.detailstory.DetailStoryActivity
import com.dicoding.syarifh.storyapp.view.maps.MapsActivity
import com.dicoding.syarifh.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        setupView()
        setupAction()

        showLoading(true)

        viewModel.stories.observe(this, Observer { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData)
            showLoading(false)

        })
    }

    override fun onResume() {
        super.onResume()
        showLoading(true)
        viewModel.getSession().observe(this, Observer { user ->
            val token = user.token
            viewModel.fetchStories(token, this)
        })
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, DetailStoryActivity::class.java).apply {
                putExtra(DetailStoryActivity.EXTRA_NAME, story.name)
                putExtra(DetailStoryActivity.EXTRA_PHOTO_URL, story.photoUrl)
                putExtra(DetailStoryActivity.EXTRA_DESCRIPTION, story.description)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )
    }

    private fun setupAction() {

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }
                R.id.menu2 -> {
                    viewModel.logout()
                    true
                }
                else -> false
            }
        }
        binding.addStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}