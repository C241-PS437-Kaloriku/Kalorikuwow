package com.dicoding.kaloriku.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.kaloriku.R
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.databinding.ActivityPhysicalDataBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Calendar

class PhysicalDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhysicalDataBinding
    private val viewModel: PhysicalDataViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhysicalDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        setupTextWatchers()
        setupGenderSpinner()
        setupDatePicker()
        observeViewModel()
    }

    private fun setupTextWatchers() {
        binding.weightEditText.addTextChangedListener(createNumberTextWatcher(binding.weightEditTextLayout))
        binding.heightEditText.addTextChangedListener(createNumberTextWatcher(binding.heightEditTextLayout))
        binding.birthdateEditText.addTextChangedListener(createBirthdateTextWatcher(binding.birthdateEditTextLayout))
    }

    private fun setupGenderSpinner() {
        val genderOptions = resources.getStringArray(R.array.gender_options)
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genderAdapter
    }

    private fun setupDatePicker() {
        binding.birthdateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    binding.birthdateEditText.setText(formattedDate)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        }
    }

    private fun createNumberTextWatcher(inputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.isEmpty() || text.toIntOrNull() == null) {
                    inputLayout.error = "Please enter a valid number"
                } else {
                    inputLayout.isErrorEnabled = false
                }
            }
        }
    }

    private fun createBirthdateTextWatcher(inputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (!isValidDate(text)) {
                    inputLayout.error = "Please enter a valid date (yyyy-mm-dd)"
                } else {
                    inputLayout.isErrorEnabled = false
                }
            }
        }
    }

    private fun isValidDate(date: String): Boolean {
        val regex = Regex("\\d{4}-\\d{2}-\\d{2}")
        return date.matches(regex)
    }

    private fun setupAction() {
        binding.btnSave.setOnClickListener {
            val weight = binding.weightEditText.text.toString().toIntOrNull()
            val height = binding.heightEditText.text.toString().toIntOrNull()
            val gender = binding.genderSpinner.selectedItem.toString().lowercase()
            val birthdate = binding.birthdateEditText.text.toString()

            if (weight == null || height == null) {
                Toast.makeText(this, "Please enter valid weight and height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(birthdate)) {
                Toast.makeText(this, "Please enter valid birthdate", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = UpdatePhysicalRequest(weight, height, gender, birthdate, userId = "")
            viewModel.getTokenAndUpdatePhysicalData(request)
        }
    }

    private fun observeViewModel() {
        viewModel.updatePhysicalResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(
                    this,
                    "Physical Data updated: ${response.message}",
                    Toast.LENGTH_SHORT
                ).show()
                // Pindah ke MainActivity setelah data diperbarui
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Menutup activity saat ini
            }.onFailure { throwable ->
                Toast.makeText(
                    this,
                    "Failed to update Physical Data: ${throwable.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}
