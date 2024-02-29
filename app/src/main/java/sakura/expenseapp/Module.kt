package sakura.expenseapp

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val date: Long,
    val amount: Float,
    val category: Int
)


@Dao
interface RecordDao {

    @Query("SELECT * FROM record")
    fun getAll(): List<Record>

    @Query("SELECT * FROM record WHERE category = (:category)")
    fun filterByCategory(category: Int): List<Record>

    @Query("SELECT * FROM record WHERE date BETWEEN (:start) AND (:end)")
    fun filterByDate(start: Long, end: Long): List<Record>

    @Query("SELECT * FROM record WHERE category = (:category) AND date BETWEEN (:start) AND (:end)")
    fun filterByCategoryAndDate(category: Int, start: Long, end: Long): List<Record>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: Record): Long

    @Update
    fun updateRecord(record: Record): Int

}


@Database(entities = [Record::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}
