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
import com.dicoding.kaloriku.data.response.FoodItem
import com.dicoding.kaloriku.databinding.FragmentProgressBinding
import com.dicoding.kaloriku.ui.viewmodelactivity.MainViewModel
import com.dicoding.kaloriku.ui.viewmodelactivity.ViewModelFactory
import com.dicoding.kaloriku.ui.auth.LoginActivity
import com.dicoding.kaloriku.ui.fragment.viewmodelfrag.BMIViewModel
import com.dicoding.kaloriku.ui.fragment.viewmodelfrag.ProgressViewModel
import com.dicoding.kaloriku.ui.fragment.viewmodelfrag.ProfileViewModel
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
    private lateinit var foodselectiondialogfragment: FoodSelectionDialogFragment

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
        observeProgressData()

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
            progressViewModel.setDate(date)
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

        progressViewModel.remainingCalories.observe(viewLifecycleOwner) { remaining ->
            binding.tvSubtitle.text = remaining.toInt().toString()
        }
    }

    private fun observeProgressData() {
        progressViewModel.eatenCalories.observe(viewLifecycleOwner) { calories ->
            binding.Kcalss.text = calories.toInt().toString()
        }

        progressViewModel.eatenCarbs.observe(viewLifecycleOwner) { carbs ->
            binding.carbsText.text = "Carbs ${carbs.toInt()}g"
        }

        progressViewModel.eatenProteins.observe(viewLifecycleOwner) { proteins ->
            binding.proteinsText.text = "Protein ${proteins.toInt()}g"
        }

        progressViewModel.eatenFats.observe(viewLifecycleOwner) { fats ->
            binding.fatsText.text = "Fats ${fats.toInt()}g"
        }

        progressViewModel.dailyCaloriesNeeded.observe(viewLifecycleOwner) { calories ->
            Log.d("ProgressFragment", "Daily calories needed: $calories")
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
        foodselectiondialogfragment = FoodSelectionDialogFragment.newInstance(mealType)
        foodselectiondialogfragment.setTargetFragment(this, 0)
        foodselectiondialogfragment.show(parentFragmentManager, "FoodSelectionDialogFragment_$mealType")

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
            binding.dateTextView.text = formattedDate
            progressViewModel.setDate(date)
            foodselectiondialogfragment.setDate(date)
        }
    }

    override fun onFoodSelected(food: FoodItem, mealType: String) {
        progressViewModel.addEatenFood(food.calories, food.carbohydrate, food.proteins, food.fat)

        viewModel.selectedDate.value?.let { date ->
            viewModel.loadFoodItemsForDate(date)
            Log.d("ROFLLLLLLLLLLLLLLLLL", "Initial selected date: $date")
        }
    }

    private fun observeProfileData() {
        profileViewModel.physicalData.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                val weight = userProfile.weight ?: 0
                val height = userProfile.height ?: 0
                val age = calculateAge(userProfile.birthdate)
                val goal = userProfile.goal

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
            Log.d("ProgressFragment", "Daily calories needed: $dailyCaloriesNeeded")
        }
    }

    private fun changeDate(offset: Int) {
        val calendar = Calendar.getInstance()
        calendar.time = viewModel.selectedDate.value ?: Date()
        calendar.add(Calendar.DAY_OF_MONTH, offset)
        val newDate = calendar.time
        viewModel.setDate(newDate)
        viewModel.selectedDate.value?.let { date ->
            viewModel.loadFoodItemsForDate(date)
            }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        Log.d("ProgressFragment", "Selected date changed to: ${dateFormat.format(newDate)}")
    }
}