package com.dicoding.storyapp.ui.detail
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.entity.StoryEntity
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.utils.DateFormatter
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider



class DetailActivity : AppCompatActivity() {

    private lateinit var appBinding: ActivityDetailBinding
    private lateinit var appViewModel: DetailViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = intent.getParcelableExtra(STORY) as StoryEntity?

        if (story != null) {
            viewModel()
            setupData(story)
            setupAction(story)
        }
    }

    private fun setupAction(story: StoryEntity) {
        appBinding.swipeRefreshLayout.setOnRefreshListener {
            setupData(story)
        }
    }

    private fun setupData(story: StoryEntity) {
        bookmarkAction(story)

        appViewModel.getLogin().observe(this) { user ->
            detailStory(user.token, story.id)
        }

        appBinding.swipeRefreshLayout.isRefreshing = false
    }

    private fun detailStory(token: String, id: String) {
        appViewModel.getDetailStory(token, id).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        appBinding.progressBar.visibility = View.VISIBLE
                        appBinding.tvMessage.visibility = View.GONE
                        appBinding.cvDetailStory.visibility = View.GONE
                    }
                    is Result.Success -> {
                        appBinding.progressBar.visibility = View.GONE
                        appBinding.tvMessage.visibility = View.GONE
                        appBinding.cvDetailStory.visibility = View.VISIBLE
                        appBinding.fabDetailSaveBookmark.visibility = View.VISIBLE

                        setData(result.data)
                    }
                    is Result.Error -> {
                        appBinding.progressBar.visibility = View.GONE
                        appBinding.cvDetailStory.visibility = View.GONE
                        appBinding.tvMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun bookmarkAction(story: StoryEntity) {
        appBinding.apply {
            if (story.isBookmarked) {
                fabDetailSaveBookmark.setImageDrawable(ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.baseline_bookmark_48
                ))
            } else {
                fabDetailSaveBookmark.setImageDrawable(ContextCompat.getDrawable(
                    this@DetailActivity,
                    R.drawable.baseline_bookmark_border_48
                ))
            }

            fabDetailSaveBookmark.setOnClickListener {
                if (story.isBookmarked) {
                    appViewModel.deleteStory(story)
                    fabDetailSaveBookmark.setImageDrawable(ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.baseline_bookmark_border_48
                    ))
                } else {
                    appViewModel.saveStory(story)
                    fabDetailSaveBookmark.setImageDrawable(ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.baseline_bookmark_48
                    ))
                }
            }
        }
    }

    private fun setData(story: StoryEntity) {
        appBinding.apply {
            Glide
                .with(this@DetailActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)

            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            tvDetailCreatedAt.text = DateFormatter.formatDate(story.createdAt)
        }
    }

    private fun viewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[DetailViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_3, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_sign_out -> {
                logoutDialog()
                true
            }
            else -> true
        }
    }

    private fun logoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sign_out)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { _, _ ->
                appViewModel.deleteLogin()
                toMainActivity()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun toMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        const val STORY = "extra_story"
    }
}