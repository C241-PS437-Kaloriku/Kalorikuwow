package com.dicoding.kaloriku.ui.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.dicoding.kaloriku.R
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.databinding.FragmentProfileBinding
import com.dicoding.kaloriku.ui.ViewModelFactory
import java.io.IOException
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var isEditMode = false
    private var profileImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenderSpinner()
        setupBirthdatePicker()
        observeViewModel()

        binding.editUploadPhoto.setOnClickListener {
            openImagePicker()
        }

        binding.btnSave.setOnClickListener {
            if (isEditMode) {
                saveData()
            } else {
                enableEditMode(true)
            }
        }

        viewModel.loadPhysicalData()
    }

    private fun setupGenderSpinner() {
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genderAdapter
    }

    private fun setupBirthdatePicker() {
        binding.birthdateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.birthdateEditText.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }
    }

    private fun isValidDate(date: String): Boolean {
        val regex = Regex("\\d{4}-\\d{2}-\\d{2}")
        return date.matches(regex)
    }

    private fun observeViewModel() {
        viewModel.physicalData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.usernameEditText.setText(data.username)
                binding.weightEditText.setText(data.weight.toString())
                binding.heightEditText.setText(data.height.toString())
                binding.birthdateEditText.setText(data.birthdate)
                binding.genderSpinner.setSelection(getGenderPosition(data.gender))
                binding.profileImageView.setImageResource(R.drawable.ic_profile_placeholder)

                if (!data.profilePictureUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(data.profilePictureUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(binding.profileImageView)
                }
            }
            enableEditMode(false)
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                response.message?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }.onFailure { throwable ->
                throwable.message?.let { errorMessage ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to update profile: $errorMessage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.photoUploadResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Toast.makeText(
                    requireContext(),
                    response.message ?: "Photo uploaded successfully",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.loadPhysicalData()
            }.onFailure { throwable ->
                Log.e("ProfileFragment", "Error uploading photo", throwable)
                Toast.makeText(
                    requireContext(),
                    "Failed to upload photo: ${throwable.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getGenderPosition(gender: String): Int {
        val genderOptions = resources.getStringArray(R.array.gender_options)
        return genderOptions.indexOfFirst { it.equals(gender, true) }
    }

    private fun enableEditMode(enable: Boolean) {
        isEditMode = enable
        binding.weightEditText.isEnabled = enable
        binding.heightEditText.isEnabled = enable
        binding.birthdateEditText.isEnabled = enable
        binding.usernameEditText.isEnabled = enable
        binding.genderSpinner.isEnabled = enable

        binding.btnSave.text = if (enable) getString(R.string.save) else getString(R.string.edit)
    }

    private fun saveData() {
        val username = binding.usernameEditText.text.toString()
        val weight = binding.weightEditText.text.toString().toIntOrNull()
        val height = binding.heightEditText.text.toString().toIntOrNull()
        val gender = binding.genderSpinner.selectedItem.toString().lowercase()
        val birthdate = binding.birthdateEditText.text.toString()
        val profilePictureUrl = profileImageUri?.toString() ?: viewModel.physicalData.value?.profilePictureUrl

        if (weight == null || height == null) {
            Toast.makeText(requireContext(), "Please enter valid weight and height", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidDate(birthdate)) {
            Toast.makeText(requireContext(), "Please enter valid birthdate", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdatePhysicalRequest(weight, height, gender, birthdate, userId = "", username = username, profilePictureUrl = profilePictureUrl)
        viewModel.updatePhysicalData(request)
        enableEditMode(false)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val cursor = requireContext().contentResolver.query(contentUri, null, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            it.getString(idx)
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                profileImageUri = data.data
                try {
                    val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, profileImageUri)
                    } else {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, profileImageUri!!)
                        ImageDecoder.decodeBitmap(source)
                    }
                    binding.profileImageView.setImageBitmap(bitmap)

                    profileImageUri?.let {
                        val realPath = getRealPathFromURI(it)
                        if (realPath != null) {
                            viewModel.uploadPhotoProfile(realPath)
                        } else {
                            Toast.makeText(requireContext(), "Failed to get image path", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
