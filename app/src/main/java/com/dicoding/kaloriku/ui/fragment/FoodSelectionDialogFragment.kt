package com.dicoding.kaloriku.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import androidx.fragment.app.DialogFragment
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
import com.dicoding.kaloriku.ui.ViewModelFactory
import com.dicoding.kaloriku.ui.auth.viewmodel.FoodSelectionViewModel
import com.dicoding.kaloriku.ui.helper.FoodRecommendationHelper
import kotlinx.coroutines.launch

class FoodSelectionDialogFragment : DialogFragment() {

    private var _binding: DialogFoodSelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodItemDao: FoodItemDao
    private lateinit var adapter: FoodRecommendationAdapter
    private lateinit var foodRecommendationHelper: FoodRecommendationHelper
    private lateinit var viewModel: FoodSelectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFoodSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        foodItemDao = AppDatabase.getDatabase(context).foodItemDao()
        foodRecommendationHelper = FoodRecommendationHelper(context)

        val factory = ViewModelFactory.getInstance(context)
        viewModel = ViewModelProvider(this, factory).get(FoodSelectionViewModel::class.java)

        setupRecyclerView()
        fetchFoodRecommendations()
        observeDatabaseChanges()

        setStyle(STYLE_NO_FRAME, R.style.DialogAnimationStyle)
    }

    private fun setupRecyclerView() {
        adapter = FoodRecommendationAdapter(emptyList()) { foodItem ->
            saveFoodItemToDatabase(foodItem)
            dismiss()
            (targetFragment as? FoodSelectionListener)?.onFoodSelected(foodItem)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FoodSelectionDialogFragment.adapter
        }
    }

    private fun saveFoodItemToDatabase(foodItem: FoodItem) {
        val foodItemEntity = FoodItemEntity(
            name = foodItem.name,
            calories = foodItem.calories,
            carbohydrate = foodItem.carbohydrate,
            fat = foodItem.fat,
            image = foodItem.image,
            proteins = foodItem.proteins
        )

        lifecycleScope.launch {
            viewModel.insertFoodItem(foodItemEntity)
            Log.d("FoodSelectionDialog", "Food item inserted: ${foodItem.name}")

            val allFoodItems = foodItemDao.getAllFoodItems()
            Log.d("FoodSelectionDialog", "All food items in database: $allFoodItems")
        }
    }

    private fun fetchFoodRecommendations() {
        val weight = 75f
        val height = 175f
        val age = 20
        val goal = "maintain"

        foodRecommendationHelper.getFoodRecommendations(weight, height, age, goal) { recommendations ->
            adapter.updateData(recommendations)
        }
    }

    private fun observeDatabaseChanges() {
        lifecycleScope.launch {
            viewModel.allFoodItems.collect { foodItems ->
                Log.d("FoodSelectionDialog", "Database updated: $foodItems")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): FoodSelectionDialogFragment {
            return FoodSelectionDialogFragment()
        }
    }

    interface FoodSelectionListener {
        fun onFoodSelected(food: FoodItem)
    }
}
