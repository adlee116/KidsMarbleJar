package com.example.kidsmarblejar.presentation.addUser

import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.kidsmarblejar.R
import com.example.kidsmarblejar.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (!hasPermissions()) {
            getPermissions()
        } else {
            startCamera()
        }
        binding.takePhotoButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if(mediaDir != null && mediaDir.exists()) mediaDir else filesDir

    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile  = File(
            outputDirectory,
            SimpleDateFormat(FILE_NAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg")
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo saved"
                    showImage(photoFile)
                    Toast.makeText(this@AddCameraActivity, "$msg $savedUri", Toast.LENGTH_SHORT).show()
                }
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@AddCameraActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showImage(photoFile: File) {
        Glide.with(binding.root.context)
            .load(photoFile)
            .placeholder(R.drawable.add_user_image)
            .centerCrop()
            .fitCenter()
            .into(binding.image)
        binding.image.visibility = View.VISIBLE
        binding.cameraPreview.visibility = View.INVISIBLE
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider
            .getInstance(this)
        cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also { mPreview ->
                        mPreview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                    }
                imageCapture = ImageCapture.Builder()
                    .build()
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture
                    )
                } catch (ex: Exception) {
                    Toast.makeText(this, "Issue with Camera", Toast.LENGTH_SHORT).show()
                }
            }, ContextCompat.getMainExecutor(this)
        )
    }

    private fun hasPermissions(): Boolean {
        PERMISSIONS.forEach {
            val granted =
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }
        return true
    }

    private fun getPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (hasPermissions()) {
                startCamera()
            } else {
                Toast.makeText(this, "Unable to get permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        const val TAG = "cameraX"
        const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 123
        val PERMISSIONS = arrayOf(CAMERA)
    }
}