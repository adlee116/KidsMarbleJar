package com.example.kidsmarblejar.presentation.addUser

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.kidsmarblejar.databinding.ActivityNewUserBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddUserActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewUserBinding
    private val viewModel: AddUserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setClickListeners()
        observeViewModel()
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // Might not want to double bang this
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.saveButton.setOnClickListener {
            viewModel.process(
                AddUserEvent.SaveClicked(
                    binding.firstNameTextFieldEditText.text.toString().trim(),
                    conversionType = viewModel.rewardType ?: 1,
                    conversionValue = binding.marbleValueEditText.text.toString().toInt(),
                    requiresPassword = binding.requiresPassword.isEnabled,
                    goal = binding.marblesRequiredEditText.text.toString().trim().toInt(),
                    goalName = binding.rewardNameEditText.text.toString().trim(),
                    isAdult = binding.adultToggleButton.isEnabled
                )
            )
        }
        binding.moneyToggleButton.setOnClickListener {
            viewModel.process(AddUserEvent.MoneyToggled)
        }
        binding.rewardToggleButton.setOnClickListener {
            viewModel.process(AddUserEvent.RewardToggled)
        }
        binding.addImageButton.setOnClickListener {
            addImage()
        }
    }

    private fun addImage() {
        startActivity(Intent(this, AddCameraActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.addImageButton.setImageBitmap(imageBitmap)
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = binding.addImageButton.width
        val targetH: Int = binding.addImageButton.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = 1.coerceAtLeast((photoW / targetW).coerceAtMost(photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            binding.addImageButton.setImageBitmap(bitmap)
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.saveFailed.collectLatest {
                Toast.makeText(
                    this@AddUserActivity,
                    "Unexpected error, please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        lifecycleScope.launch {
            viewModel.saved.collectLatest {
                finish()
            }
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 100
    }

}