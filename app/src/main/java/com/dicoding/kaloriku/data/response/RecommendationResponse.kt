import androidx.room.Entity
import androidx.room.PrimaryKey

data class FoodRecommendationRequest(
	val weight: Float,
	val height: Float,
	val age: Int,
	val goal: String
)

@Entity(tableName = "food_items")
data class FoodItemEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val name: String,
	val calories: Double,
	val carbohydrate: Double,
	val fat: Double,
	val image: String,
	val proteins: Double
)

data class FoodItem(
	val id: Int,
	val name: String,
	val calories: Double,
	val carbohydrate: Double,
	val fat: Double,
	val image: String,
	val proteins: Double
)

data class FoodRecommendationResponse(
	val daily_calories_needed: Float,
	val recommended_meals: List<FoodItem>
)
