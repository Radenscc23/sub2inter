package com.dicoding.storyapp.ui.home
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityHomeBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.appAdapters.SectionsPagerAdapter
import com.dicoding.storyapp.ui.insertStory.InsertActivity
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.ui.maps.MapsActivity
import com.dicoding.storyapp.ui.setting.SettingActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast


@Suppress("PrivatePropertyName")
class HomeActivity : AppCompatActivity() {

    private lateinit var appBinding: ActivityHomeBinding
    private lateinit var appViewModel: HomeViewModel
    private val PRESSED_INTERVAL = 2000
    private var pressedTime: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        supportActionBar?.elevation = 0f

        sectionsPagerAdapter()
        viewModel()
        setupAction()
    }

    private fun setupAction() {
        this.onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pressedTime + PRESSED_INTERVAL > System.currentTimeMillis()) {
                    this@HomeActivity.finish()
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        R.string.press_back_string,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                pressedTime = System.currentTimeMillis()
            }
        })
    }

    private fun viewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[HomeViewModel::class.java]
    }

    private fun sectionsPagerAdapter() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        appBinding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(appBinding.tabs, appBinding.viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.text = resources.getString(TITLES[position])
        }.attach()

        appBinding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        val homeFragment =
                            supportFragmentManager.findFragmentByTag("f0") as? HomeFragment
                        homeFragment?.scrollToTop()
                    }
                    1 -> {
                        val bookmarkFragment =
                            supportFragmentManager.findFragmentByTag("f1") as? BookmarkFragment
                        bookmarkFragment?.toTop()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_insert -> {
                startActivity(Intent(this, InsertActivity::class.java))
                true
            }
            R.id.menu_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.menu_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            R.id.menu_sign_out -> {
                showLogoutDialog()
                true
            }
            else -> true
        }
    }

    private fun showLogoutDialog() {
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
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        @StringRes
        private val TITLES = intArrayOf(R.string.home, R.string.bookmark)
    }
}