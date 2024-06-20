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
import com.dicoding.kaloriku.data.response.FoodItem
import com.dicoding.kaloriku.databinding.FragmentProgressBinding
import com.dicoding.kaloriku.ui.MainViewModel
import com.dicoding.kaloriku.ui.ViewModelFactory
import com.dicoding.kaloriku.ui.auth.LoginActivity
import com.dicoding.kaloriku.ui.auth.viewmodel.BMIViewModel
import com.dicoding.kaloriku.ui.helper.FoodRecommendationHelper

class ProgressFragment : Fragment(), FoodSelectionDialogFragment.FoodSelectionListener {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val bmiViewModel by viewModels<BMIViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodRecommendationHelper: FoodRecommendationHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

        foodRecommendationHelper = FoodRecommendationHelper(requireContext())

        // Observe user login status
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }

            bmiViewModel.calculateBMI(user.userId)
        }

        // Observe BMI result
        bmiViewModel.bmiResult.observe(viewLifecycleOwner) { bmiResponse ->
            bmiResponse?.let {
                binding.bmiTextView.text = bmiResponse.bmi
                binding.categoryTextView.text = bmiResponse.category
            }
        }


        // Set click listeners for add buttons
        binding.addBreakfastButton.setOnClickListener {
            showFoodSelectionDialog("Breakfast")
        }

        binding.addLunchButton.setOnClickListener {
            showFoodSelectionDialog("Lunch")
        }

        binding.addDinnerButton.setOnClickListener {
            showFoodSelectionDialog("Dinner")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFoodSelectionDialog(mealType: String) {
        // Show food selection dialog fragment
        val dialogFragment = FoodSelectionDialogFragment.newInstance()
        dialogFragment.setTargetFragment(this, 0)
        dialogFragment.show(parentFragmentManager, "FoodSelectionDialogFragment_$mealType")
    }

    // Handle food selection from dialog
    override fun onFoodSelected(food: FoodItem) {
        // Handle food selection based on meal type
        when (food.name) {
            "Breakfast" -> binding.breakfastDescription.text = food.name
            "Lunch" -> binding.lunchDescription.text = food.name
            "Dinner" -> binding.dinnerDescription.text = food.name
        }
    }
}