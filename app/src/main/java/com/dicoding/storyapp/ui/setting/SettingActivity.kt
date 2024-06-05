package com.dicoding.storyapp.ui.setting
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivitySettingBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.main.MainActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton

class SettingActivity : AppCompatActivity() {

    private lateinit var appBinding: ActivitySettingBinding
    private lateinit var appViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.app_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        initTheme()
    }

    private fun initTheme() {
        appBinding.apply {
            executeGetThemeSetting()

            switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                appViewModel.saveThemeSetting(isChecked)
            }
        }
    }

    private fun setupViewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[SettingViewModel::class.java]
    }

    private fun executeGetThemeSetting() {
        appBinding.apply {
            appViewModel.getThemeSetting().observe(
                this@SettingActivity
            ) {isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_sign_out -> {
                showLogoutDialog()
                true
            }
            else -> true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        appViewModel.getLogin().observe(this) { user ->
            if (user.token.isNotBlank()) {
                menuInflater.inflate(R.menu.option_menu_3, menu)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun directToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sign_out)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { _, _ ->
                appViewModel.deleteLogin()
                directToMainActivity()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }
}