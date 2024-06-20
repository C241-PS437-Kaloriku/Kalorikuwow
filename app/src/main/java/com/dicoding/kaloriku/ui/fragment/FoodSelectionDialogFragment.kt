package com.dicoding.kaloriku.ui.fragment

import android.os.Bundle
import android.provider.Settings.System.DATE_FORMAT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.kaloriku.R
import com.dicoding.kaloriku.data.dao.AppDatabase
import com.dicoding.kaloriku.data.adapter.FoodRecommendationAdapter
import com.dicoding.kaloriku.data.dao.FoodItemDao
import com.dicoding.kaloriku.data.response.FoodItem
import com.dicoding.kaloriku.data.response.FoodItemEntity
import com.dicoding.kaloriku.databinding.DialogFoodSelectionBinding
import com.dicoding.kaloriku.ui.MainViewModel
import com.dicoding.kaloriku.ui.ViewModelFactory
import com.dicoding.kaloriku.ui.auth.viewmodel.FoodSelectionViewModel
import com.dicoding.kaloriku.ui.helper.FoodRecommendationHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FoodSelectionDialogFragment : DialogFragment() {

    private var _binding: DialogFoodSelectionBinding? = null
    private val binding get() = _binding!!
    private val sviewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var foodItemDao: FoodItemDao
    private lateinit var adapter: FoodRecommendationAdapter
    private lateinit var foodRecommendationHelper: FoodRecommendationHelper
    private lateinit var viewModel: FoodSelectionViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var mealType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mealType = it.getString(ARG_MEAL_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFoodSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        foodItemDao = AppDatabase.getDatabase(context).foodItemDao()
        foodRecommendationHelper = FoodRecommendationHelper(context)

        // Initialize ViewModel using ViewModelFactory
        val factory = ViewModelFactory.getInstance(context)
        viewModel = ViewModelProvider(this, factory).get(FoodSelectionViewModel::class.java)

        // Initialize ProfileViewModel to fetch user's profile data
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        observeProfileData()

        setupRecyclerView()
    }

    private fun observeProfileData() {
        profileViewModel.physicalData.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                val weight = userProfile.weight ?: 0
                val height = userProfile.height ?: 0
                val age = calculateAge(userProfile.birthdate)
                val goal = userProfile.goal ?: ""

                fetchFoodRecommendations(weight, height, age, goal)
            }
        }

        profileViewModel.loadPhysicalData()
    }

    private fun calculateAge(birthdate: String?): Int {
        birthdate?.let {
            try {
                val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
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
        return 0 // Return 0 if birthdate is null or if there's an exception

        setStyle(STYLE_NO_FRAME, R.style.DialogAnimationStyle)
    }

    private fun setupRecyclerView() {
        adapter = FoodRecommendationAdapter(emptyList()) { foodItem ->

                sviewModel.addFoodItemForDate(foodItem, mealType ?: "")
                dismiss()
                (targetFragment as? FoodSelectionListener)?.onFoodSelected(foodItem, mealType ?: "")
            }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FoodSelectionDialogFragment.adapter
        }
    }

    private fun fetchFoodRecommendations(weight: Int, height: Int, age: Int, goal: String) {
        foodRecommendationHelper.getFoodRecommendations(weight, height, age, goal) { recommendations ->
            adapter.updateData(recommendations)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MEAL_TYPE = "meal_type"

        fun newInstance(mealType: String): FoodSelectionDialogFragment {
            val fragment = FoodSelectionDialogFragment()
            val args = Bundle()
            args.putString(ARG_MEAL_TYPE, mealType)
            fragment.arguments = args
            return fragment
        }
    }

    interface FoodSelectionListener {
        fun onFoodSelected(food: FoodItem, mealType: String)
    }

}