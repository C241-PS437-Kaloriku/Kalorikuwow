package com.dicoding.kaloriku.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.kaloriku.R
import com.dicoding.kaloriku.data.response.FoodItem

class FoodRecommendationAdapter(
    private var foodItems: List<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodRecommendationAdapter.FoodRecommendationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodRecommendationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_recommendation, parent, false)
        return FoodRecommendationViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodRecommendationViewHolder, position: Int) {
        holder.bind(foodItems[position])
    }

    override fun getItemCount(): Int = foodItems.size

    fun updateData(newFoodItems: List<FoodItem>) {
        foodItems = newFoodItems
        notifyDataSetChanged()
    }

    inner class FoodRecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImageView: ImageView = itemView.findViewById(R.id.foodImageView)
        private val foodNameTextView: TextView = itemView.findViewById(R.id.foodNameTextView)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.caloriesTextView)
        private val addButton: ImageButton = itemView.findViewById(R.id.addButton)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(foodItems[position])
                }
            }
        }

        fun bind(foodItem: FoodItem) {
            foodNameTextView.text = foodItem.name
            caloriesTextView.text = "Calories: ${foodItem.calories} kcal"

            addButton.setOnClickListener {
                onItemClick(foodItem)
            }

            itemView.setOnClickListener {
                onItemClick(foodItem)
            }

            Glide.with(itemView)
                .load(foodItem.image)
                .into(foodImageView)
        }
    }
}
