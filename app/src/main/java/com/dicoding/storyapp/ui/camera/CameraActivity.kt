package com.dicoding.storyapp.ui.camera
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityCameraBinding
import com.dicoding.storyapp.ui.insertStory.InsertActivity
import com.dicoding.storyapp.utils.createFile
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast


class CameraActivity : AppCompatActivity() {

    private lateinit var appBinding: ActivityCameraBinding
    private var cameraCategory: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var capture: ImageCapture? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(appBinding.root)

        cameraAction()
    }

    private fun cameraOn() {
        val cameraProviderFuture = ProcessCameraProvider
            .getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(appBinding.viewFinder.surfaceProvider)
                }

            capture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraCategory,
                    preview,
                    capture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraActivity,
                    R.string.camera_open_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun cameraAction() {
        appBinding.captureImage.setOnClickListener { capturePhoto() }
        appBinding.switchCamera.setOnClickListener {
            cameraCategory =
                if (cameraCategory == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA

            cameraOn()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        cameraOn()
    }


    private fun capturePhoto() {
        appBinding.progressBar.visibility = View.VISIBLE
        appBinding.captureImage.isEnabled = false
        appBinding.switchCamera.isEnabled = false

        val imageCapture = capture ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        R.string.image_capture_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("picture", photoFile)
                    intent.putExtra(
                        "isBackCamera",
                        cameraCategory == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(InsertActivity.X_RESULT, intent)
                    finish()
                }
            }
        )
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        supportActionBar?.hide()
    }

}