package com.dicoding.storyapp.ui.main
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.auth.LoginFragment
import com.dicoding.storyapp.ui.home.HomeActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider


class MainActivity : AppCompatActivity() {

    private lateinit var appBinding: ActivityMainBinding
    private lateinit var appViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        setupViewModel()
        initTheme()
        setupFragment()
    }

    private fun setupFragment() {
        appViewModel.getLogin().observe(this) { user ->
            if (user.token.isNotBlank()) {
                directToHomeActivity()
            } else {
                addLoginFragment()
            }
        }
    }

    private fun addLoginFragment() {
        val fragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val fragment = fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

        if (fragment !is LoginFragment) {
            Log.d("StoryApp", "Fragment Name: ${LoginFragment::class.java.simpleName}")
            fragmentManager
                .beginTransaction()
                .replace(R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun directToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun initTheme() {
        appViewModel.getThemeSetting()
    }


    private fun setupViewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[MainViewModel::class.java]
    }
}