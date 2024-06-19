import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FoodItemDao {
	@Insert
	suspend fun insert(foodItemEntity: FoodItemEntity)

	@Update
	suspend fun update(foodItemEntity: FoodItemEntity)

	@Delete
	suspend fun delete(foodItemEntity: FoodItemEntity)

	@Query("SELECT * FROM food_items")
	suspend fun getAllFoodItems(): List<FoodItemEntity>
}
