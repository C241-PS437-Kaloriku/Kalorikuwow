package com.dicoding.kaloriku.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.kaloriku.R
import com.dicoding.kaloriku.data.response.RegisterResponse
import com.dicoding.kaloriku.databinding.ActivityRegisterBinding
import com.dicoding.kaloriku.ui.auth.viewmodelauth.RegisterViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: ActivityRegisterBinding
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.RegisterActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
        observeViewModel()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (!isValidEmail(email)) {
                    binding.emailEditTextLayout.error = "Format email tidak valid"
                } else {
                    binding.emailEditTextLayout.isErrorEnabled = false
                }
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                if (!isValidPassword(password)) {
                    binding.passwordEditTextLayout.error = "Password minimal 8 karakter"
                } else {
                    binding.passwordEditTextLayout.isErrorEnabled = false
                }
            }
        })

        binding.birthdateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.birthdateEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val birthdate = s.toString()
                if (!isValidBirthdate(birthdate)) {
                    binding.birthdateEditTextLayout.error = "Format tanggal tidak valid, contoh: 2023-12-21"
                } else {
                    binding.birthdateEditTextLayout.isErrorEnabled = false
                }
            }
        })
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.birthdateEditText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(Regex(emailPattern))
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    private fun isValidBirthdate(birthdate: String): Boolean {
        val birthdatePattern = "\\d{4}-\\d{2}-\\d{2}"
        return birthdate.matches(Regex(birthdatePattern))
    }

    private fun setupAction() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val birthdateString = binding.birthdateEditText.text.toString()
            if (!isValidBirthdate(birthdateString)) {
                binding.birthdateEditTextLayout.error = "Format tanggal tidak valid, contoh: 2023-12-21"
                return@setOnClickListener
            }
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val birthdate: Date = dateFormatter.parse(birthdateString)

            binding.loadingProgressBar.visibility = View.VISIBLE

            viewModel.register(email, password, dateFormatter.format(birthdate))
        }
        binding.tvLoginHere.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


    private fun observeViewModel() {
        viewModel.registerResult.observe(this) { response ->
            binding.loadingProgressBar.visibility = View.INVISIBLE
            if (response != null) {
                showRegisterSuccessDialog(response)
            } else {
                showErrorDialog("Gagal melakukan registrasi")
            }
        }
    }


    private fun showRegisterSuccessDialog(response: RegisterResponse) {
        AlertDialog.Builder(this).apply {
            setTitle("Berhasil")
            setMessage("Sukses melakukan registrasi. Silahkan login !")
            setPositiveButton("Lanjut") { _, _ ->
                navigateToLogin()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog(erormessage: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Gagal Melakukan Registrasi")
            setMessage(erormessage)
            setPositiveButton("Kembali") { _, _ -> }
            create()
            show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

