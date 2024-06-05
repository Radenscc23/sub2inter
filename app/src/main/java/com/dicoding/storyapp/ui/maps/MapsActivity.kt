package com.dicoding.storyapp.ui.maps
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.main.MainActivity


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var appMap: GoogleMap
    private lateinit var appBinding: ActivityMapsBinding
    private lateinit var appViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()
    }

    private fun setupAction() {
        appViewModel.getLogin().observe(this) { user ->
            executeGetAllStoriesWithLocation(user.token)
        }
    }

    private fun setupViewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[MapsViewModel::class.java]
    }

    private fun executeGetAllStoriesWithLocation(token: String) {
        appViewModel.getAllStoriesWithLocation(token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {}
                    is Result.Success -> {
                        result.data.forEach { story ->
                            val location = LatLng(story.lat!!, story.lon!!)
                            appMap.addMarker(MarkerOptions().position(location).title(story.name))
                            appMap.addMarker(MarkerOptions().position(location).snippet(story.description))
                            appMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                        }
                    }
                    is Result.Error -> {}
                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_3, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        appMap = googleMap
        setupAction()
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