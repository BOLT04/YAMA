package isel.pt.yama.model.dataAccess.database

import androidx.room.*
import isel.pt.yama.dto.Team
import java.util.*

@Dao
interface TeamDAO {
    @Query("SELECT * FROM teams")
    fun getAll(): List<Team>

    /*
    @Query("SELECT * FROM teams WHERE date LIKE :date")
    fun getAllByDate(date: Calendar): List<Team>
    */

    @Query("SELECT * FROM teams WHERE name = :name")
    fun findById(name: String): Team

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg quotes: Team)

    @Delete
    fun delete(quote: Team)
}

@Database(entities = arrayOf(Team::class), version = 1)
abstract class YAMADatabase : RoomDatabase() {
    abstract fun teamDAO(): TeamDAO
    //abstract fun teamMembersDAO(): ?DAO
    //abstract fun messagesDAO(): ?DAO
}