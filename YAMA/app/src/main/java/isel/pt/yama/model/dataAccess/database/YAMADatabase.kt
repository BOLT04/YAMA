package isel.pt.yama.model.dataAccess.database

import androidx.room.*

@Dao
interface TeamDAO {
    /*
    @Query("SELECT * FROM teams WHERE date LIKE :date")
    fun getAllByDate(date: Calendar): List<Team>
    */

    @Query("SELECT * FROM teams WHERE orgId = :orgId")
    fun getOrganizationTeams(orgId: String): List<Team>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg quotes: Team)

    @Delete
    fun delete(quote: Team)
}

@Dao
interface MessageDAO {
    @Insert
    fun insert(message: Message)

    @Query("SELECT * from messages ORDER BY date ASC")
    fun getAllMessages(): List<Message>
}

@Database(entities = [Team::class, Member::class, Message::class], version = 1)
abstract class YAMADatabase : RoomDatabase() {
    abstract fun teamDAO(): TeamDAO
    //abstract fun teamMembersDAO(): ?DAO
    abstract fun messagesDAO(): MessageDAO
}