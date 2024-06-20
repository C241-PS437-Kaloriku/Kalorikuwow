package com.dicoding.kaloriku.ui.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.kaloriku.data.response.FoodItem
import com.dicoding.kaloriku.data.response.FoodRecommendationRequest
import com.dicoding.kaloriku.data.response.FoodRecommendationResponse
import com.dicoding.kaloriku.data.retrofit.ApiConfig
import com.dicoding.kaloriku.databinding.FragmentProgressBinding
import com.dicoding.kaloriku.ui.MainViewModel
import com.dicoding.kaloriku.ui.ViewModelFactory
import com.dicoding.kaloriku.ui.auth.LoginActivity
import com.dicoding.kaloriku.ui.auth.viewmodel.BMIViewModel
import com.dicoding.kaloriku.ui.auth.viewmodel.ProgressViewModel
import com.dicoding.kaloriku.ui.helper.FoodRecommendationHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProgressFragment : Fragment(), FoodSelectionDialogFragment.FoodSelectionListener {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val bmiViewModel by viewModels<BMIViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private val progressViewModel by viewModels<ProgressViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }


    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

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


        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLogin) {
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
                return@observe
            }
            bmiViewModel.calculateBMI(user.userId)
        }

        bmiViewModel.bmiResult.observe(viewLifecycleOwner) { bmiResponse ->
            bmiResponse?.let {
                binding.bmiTextView.text = bmiResponse.bmi
                binding.categoryTextView.text = bmiResponse.category
            }
        }

        observeProfileData()
        binding.previousDayButton.setOnClickListener {
            changeDate(-1)
        }

        binding.nextDayButton.setOnClickListener {
            changeDate(1)
        }

        binding.addBreakfastButton.setOnClickListener {
            showFoodSelectionDialog("Breakfast")
        }

        binding.addLunchButton.setOnClickListener {
            showFoodSelectionDialog("Lunch")
        }

        binding.addDinnerButton.setOnClickListener {
            showFoodSelectionDialog("Dinner")
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
            binding.dateTextView.text = formattedDate
        }

        viewModel.breakfastItems.observe(viewLifecycleOwner) { items ->
            binding.breakfastDescription.text = items.joinToString(", ") { it.name }
        }

        viewModel.lunchItems.observe(viewLifecycleOwner) { items ->
            binding.lunchDescription.text = items.joinToString(", ") { it.name }
        }

        viewModel.dinnerItems.observe(viewLifecycleOwner) { items ->
            binding.dinnerDescription.text = items.joinToString(", ") { it.name }
        }

        // Observe eaten calories and update UI
        progressViewModel.eatenCalories.observe(viewLifecycleOwner) { calories ->
            binding.Kcalss.text = calories.toString()
        }

        // Observe eaten carbs and update UI
        progressViewModel.eatenCarbs.observe(viewLifecycleOwner) { carbs ->
            binding.carbsText.text = "Carbs Eaten: $carbs"
        }

        // Observe eaten proteins and update UI
        progressViewModel.eatenProteins.observe(viewLifecycleOwner) { proteins ->
            binding.proteinsText.text = "Proteins Eaten: $proteins"
        }

        // Observe eaten fats and update UI
        progressViewModel.eatenFats.observe(viewLifecycleOwner) { fats ->
            binding.fatsText.text = "Fats Eaten: $fats"
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
        val dialogFragment = FoodSelectionDialogFragment.newInstance(mealType)
        dialogFragment.setTargetFragment(this, 0)
        dialogFragment.show(parentFragmentManager, "FoodSelectionDialogFragment_$mealType")
    }

    override fun onFoodSelected(food: FoodItem, mealType: String) {
        viewModel.addFoodItemForDate(food, mealType)

        progressViewModel.addEatenFood(food.calories, food.carbohydrate, food.proteins, food.fat)

        updateUIWithEatenValues()
    }

    private fun observeProfileData() {
        profileViewModel.physicalData.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                val weight = userProfile.weight ?: 0
                val height = userProfile.height ?: 0
                val age = calculateAge(userProfile.birthdate)
                val goal = userProfile.goal ?: ""

                fetchDailyCaloriesNeeded(weight, height, age, goal)

            }
        }
        profileViewModel.loadPhysicalData()
    }

    private fun calculateAge(birthdate: String?): Int {
        birthdate?.let {
            try {
                val dateFormat = SimpleDateFormat(Settings.System.DATE_FORMAT, Locale.getDefault())
                val dateOfBirth = dateFormat.parse(birthdate)
                val today = Calendar.getInstance()
                val dob = Calendar.getInstance()
                dob.time = dateOfBirth!!

                var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }
                return age
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return 0
    }

    private fun fetchDailyCaloriesNeeded(weight: Int, height: Int, age: Int, goal: String) {
        progressViewModel.getDailyCalories(weight, height, age, goal) { dailyCaloriesNeeded ->
            Log.d("ProfileFragment", "Daily calories needed: $dailyCaloriesNeeded")
            // Update UI or perform actions with dailyCaloriesNeeded
        }
    }

    private fun changeDate(offset: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = viewModel.selectedDate.value ?: Date()
        calendar.add(Calendar.DAY_OF_MONTH, offset)
        viewModel.setDate(calendar.time)
    }

    private fun updateUIWithEatenValues() {
        progressViewModel.eatenCalories.value?.let { calories ->
            binding.Kcalss.text = calories.toString()
        }
        progressViewModel.eatenCarbs.value?.let { carbs ->
            binding.carbsText.text = "Carbs Eaten: $carbs"
        }
        progressViewModel.eatenProteins.value?.let { proteins ->
            binding.proteinsText.text = "Proteins Eaten: $proteins"
        }
        progressViewModel.eatenFats.value?.let { fats ->
            binding.fatsText.text = "Fats Eaten: $fats"
        }
    }
}