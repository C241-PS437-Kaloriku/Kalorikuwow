package com.dicoding.kaloriku.ui.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dicoding.kaloriku.databinding.FragmentProgressBinding
import com.dicoding.kaloriku.ui.FoodRecogActivity
import com.dicoding.kaloriku.ui.MainViewModel
import com.dicoding.kaloriku.ui.ViewModelFactory
import com.dicoding.kaloriku.ui.auth.LoginActivity
import com.dicoding.kaloriku.ui.auth.viewmodel.BMIViewModel

class ProgressFragment : Fragment() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val bmiViewModel by viewModels<BMIViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupAction()

        viewModel.getSession().observe(viewLifecycleOwner, Observer { user ->
            if (!user.isLogin) {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }

            bmiViewModel.calculateBMI(user.userId)
        })

        bmiViewModel.bmiResult.observe(viewLifecycleOwner, Observer { bmiResponse ->
            bmiResponse?.let {
                val bmiText = "BMI: ${bmiResponse.bmi}\nCategory: ${bmiResponse.category}"
                binding.bmiTextView.text = bmiResponse.bmi
                binding.categoryTextView.text = bmiResponse.category
            }
        })

        binding.camera.setOnClickListener {
            startActivity(Intent(activity, FoodRecogActivity::class.java))
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.window?.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
