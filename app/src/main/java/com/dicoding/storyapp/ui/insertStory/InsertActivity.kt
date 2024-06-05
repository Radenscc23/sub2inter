package com.dicoding.storyapp.ui.insertStory
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.data.source.remote.request.NewStoryRequest
import com.dicoding.storyapp.databinding.ActivityInsertBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.camera.CameraActivity
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.utils.reduceFileImage
import com.dicoding.storyapp.utils.rotateFile
import com.dicoding.storyapp.utils.uriToFile
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

class InsertActivity : AppCompatActivity() {

    private var uploadFile: File? = null
    private lateinit var appBinding: ActivityInsertBinding
    private lateinit var appViewModel: InsertViewModel

    private val intentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra(
                "isBackCamera",
                true
            ) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                uploadFile = file
                appBinding.ivItemImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val intentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@InsertActivity)
                uploadFile = myFile
                appBinding.ivItemImage.setImageURI(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    R.string.don_t_have_permission_to_access_camera,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        const val X_RESULT = 200
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                CODE_PERMISSIONS
            )
        }

        setupViewModel()
        setupSupportActionBarTitle()
        setupAction()
    }

    private fun setupAction() {
        appBinding.btnCameraX.setOnClickListener { startCameraX() }
        appBinding.btnGallery.setOnClickListener { startGallery() }
        appBinding.btnAdd.setOnClickListener { addNewStory() }
    }

    private fun setupSupportActionBarTitle() {
        appViewModel.getLogin().observe(this) { user ->
            if (user.token.isNotBlank()) {
                supportActionBar?.setTitle(R.string.add_new_story)
            } else {
                supportActionBar?.setTitle(R.string.add_new_story_guest)
            }
        }
    }

    private fun setupViewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[InsertViewModel::class.java]
    }

    private fun startCameraX() {
        intentCamera.launch(Intent(this, CameraActivity::class.java))
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, R.string.choose_image.toString())
        intentGallery.launch(chooser)
    }

    private fun addNewStory() {
        val edAddDescription = appBinding.edAddDescription.text.toString()

        when {
            uploadFile == null -> {
                Toast.makeText(
                    this@InsertActivity,
                    R.string.insert_image_first,
                    Toast.LENGTH_SHORT
                ).show()
            }
            edAddDescription.isEmpty() -> {
                Toast.makeText(
                    this@InsertActivity,
                    R.string.description_mandatory,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val file = reduceFileImage(uploadFile as File)
                val description = edAddDescription.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                showAddNewStoryDialog(description, imageMultipart)
            }
        }
    }

    private fun showAddNewStoryDialog(desc: RequestBody, photo: MultipartBody.Part) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.add_new_story)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { _, _ ->
                appViewModel.getLogin().observe(this) { user ->
                    executeAddNewStory(user.token, desc, photo)
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun executeAddNewStory(token: String, desc: RequestBody, photo: MultipartBody.Part) {
        appViewModel.addNewStory(NewStoryRequest(token, desc, photo)).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        appBinding.progressBar.visibility = View.VISIBLE
                        appBinding.btnCameraX.isEnabled = false
                        appBinding.btnGallery.isEnabled = false
                        appBinding.btnAdd.isEnabled = false
                    }
                    is Result.Success -> {
                        appBinding.progressBar.visibility = View.GONE
                        appBinding.btnCameraX.isEnabled = true
                        appBinding.btnGallery.isEnabled = true
                        appBinding.btnAdd.isEnabled = true

                        Toast.makeText(
                            this,
                            R.string.post_story_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    is Result.Error -> {
                        appBinding.progressBar.visibility = View.GONE
                        appBinding.btnCameraX.isEnabled = true
                        appBinding.btnGallery.isEnabled = true
                        appBinding.btnAdd.isEnabled = true

                        Toast.makeText(
                            this,
                            R.string.post_story_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun directToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    @Suppress("BooleanMethodIsAlwaysInverted")
    private fun allPermissionsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        appViewModel.getLogin().observe(this) { user ->
            if (user.token.isNotBlank()) menuInflater.inflate(R.menu.option_menu_3, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
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
                directToMainActivity()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }


}