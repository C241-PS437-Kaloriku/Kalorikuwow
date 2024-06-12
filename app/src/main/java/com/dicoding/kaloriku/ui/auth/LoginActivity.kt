package com.dicoding.kaloriku.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.kaloriku.data.response.LoginResponse
import com.dicoding.kaloriku.data.pref.UserModel
import com.dicoding.kaloriku.databinding.ActivityLoginBinding
import com.dicoding.kaloriku.ui.MainActivity
import com.dicoding.kaloriku.ui.PhysicalDataActivity
import com.dicoding.kaloriku.ui.ViewModelFactory
import com.dicoding.kaloriku.ui.auth.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Validasi panjang password minimal 8 karakter
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
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(Regex(emailPattern))
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    private fun setupAction() {
        binding.btnSignUpWithEmail.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            binding.loadingProgressBar.visibility = View.VISIBLE

            viewModel.login(email, password)
        }
        binding.tvRegisterHere.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(this) { response ->
            binding.loadingProgressBar.visibility = View.INVISIBLE
            if (response != null) {
                showLoginSuccessDialog(response)
            } else {
                showErrorDialog()
            }
        }
    }

    private fun showLoginSuccessDialog(response: LoginResponse) {
        AlertDialog.Builder(this).apply {
            setTitle("Berhasil")
            setMessage("Anda berhasil masuk ke aplikasi !")
            setPositiveButton("Masuk") { _, _ ->
                val token = response.token ?: ""
                val email = response.user?.email ?: ""
                val userId = response.user?.userId ?: ""
                Log.d("LoginActivity", "Token received from response: $token")
                Log.d("LoginActivity", "UserId received from response: $userId")
                val userModel = UserModel(email, token, isLogin = true, userId = userId)

                viewModel.saveSession(userModel)

                viewModel.hasPhysicalData().observe(this@LoginActivity) { hasData ->
                    val intent = if (hasData) {
                        Intent(this@LoginActivity, MainActivity::class.java)
                    } else {
                        Intent(this@LoginActivity, PhysicalDataActivity::class.java)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
            create()
            show()
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Gagal Melakukan Login")
            setMessage("Tolong masukkan email dan password dengan benar !")
            setPositiveButton("Kembali") { _, _ -> }
            create()
            show()
        }
    }
}
