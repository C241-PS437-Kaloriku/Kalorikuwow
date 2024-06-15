package com.dicoding.kaloriku.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.kaloriku.R
import com.dicoding.kaloriku.databinding.ActivityMainBinding
import com.dicoding.kaloriku.ui.helper.ImageClassifierHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FoodRecogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var previewImageView: ImageView
    private lateinit var analyzeButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var resultImageView: ImageView

    companion object {
        private const val PICK_IMAGE = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodrecog)

        resultTextView = findViewById(R.id.result_text)
        previewImageView = findViewById(R.id.previewImageView)
//        resultImageView = findViewById(R.id.result_image)
        val galleryButton = findViewById<ImageView>(R.id.galleryButton)
        val cameraButton = findViewById<ImageView>(R.id.cameraButton)

        galleryButton.setOnClickListener { startGallery() }
        cameraButton.setOnClickListener { startCamera() }
        imageClassifierHelper = ImageClassifierHelper(this)
        imageClassifierHelper.setupImageClassifier()
    }

    private fun startGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            var image: Bitmap? = null
            try {
                imageUri?.let { uri ->
                    image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    previewImageView.setImageBitmap(image)
                    showImage()
                    image?.let { analyzeImage(it) }
                } ?: showToast("Error: URI is null")
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Error: Failed to load image")
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(EXTRA_CAMERAX_IMAGE)?.let { uriString ->
                val uri = Uri.parse(uriString)
                var image: Bitmap? = null
                try {
                    image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    previewImageView.setImageBitmap(image)
                    showImage()
                    image?.let { analyzeImage(it) }
                } catch (e: IOException) {
                    e.printStackTrace()
                    showToast("Error: Failed to load image")
                }
            }
        }
    }

    private fun createTempImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }

    private fun classifyImage(image: Bitmap) {
        val result = imageClassifierHelper.classifyStaticImage(image)
        displayResult(result, image)
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            previewImageView.setImageURI(uri)
        }
    }

    private fun analyzeImage(image: Bitmap) {
        classifyImage(image)
    }

    private fun displayResult(result: String, image: Bitmap) {
        resultTextView.text = result
        val tempFile = createTempImageFile()
        saveBitmapToFile(image, tempFile)
        previewImageView.setImageURI(Uri.fromFile(tempFile))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}