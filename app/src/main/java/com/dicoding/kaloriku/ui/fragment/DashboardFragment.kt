package com.dicoding.kaloriku.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.kaloriku.databinding.FragmentDashboardBinding
import com.dicoding.kaloriku.ui.CameraActivity
import com.dicoding.kaloriku.ui.helper.ImageClassifierHelper
import java.io.IOException

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var previewImageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    companion object {
        private const val PICK_IMAGE = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageClassifierHelper = ImageClassifierHelper(requireContext())
        imageClassifierHelper.setupImageClassifier()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val view = binding.root

        resultTextView = binding.resultText
        previewImageView = binding.previewImageView
        val galleryButton = binding.galleryButton
        val cameraButton = binding.cameraButton

        galleryButton.setOnClickListener { startGallery() }
        cameraButton.setOnClickListener { startCamera() }

        return view
    }

    private fun startGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    private fun startCamera() {
        val intent = Intent(context, CameraActivity::class.java)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        try {
                            val image = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                            previewImageView.setImageBitmap(image)
                            analyzeImage(image)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            showToast("Error: Failed to load image")
                        }
                    }
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.getStringExtra(EXTRA_CAMERAX_IMAGE)?.let { uriString ->
                        val uri = Uri.parse(uriString)
                        try {
                            val image = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                            previewImageView.setImageBitmap(image)
                            analyzeImage(image)
                        } catch (e: IOException) {
                            e.printStackTrace()
                            showToast("Error: Failed to load image")
                        }
                    }
                }
            }
        }
    }

    private fun analyzeImage(image: Bitmap) {
        imageClassifierHelper.setupImageClassifier()
        val result = imageClassifierHelper.classifyStaticImage(image)
        displayResult(result)
    }

    private fun displayResult(result: String) {
        resultTextView.text = result
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        imageClassifierHelper.close()
    }
}
