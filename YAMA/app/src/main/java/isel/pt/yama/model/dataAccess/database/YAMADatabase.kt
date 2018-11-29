package isel.pt.yama.model.dataAccess.database

import androidx.room.*

@Dao
interface TeamDAO {
    /*
    @Query("SELECT * FROM teams WHERE date LIKE :date")
    fun getAllByDate(date: Calendar): List<TeamDto>
    */

    @Query("SELECT * FROM teams WHERE organization = :organization")
    fun getOrganizationTeams(organization: String): List<Team>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg teams: Team)

    @Delete
    fun delete(quote: Team)
}

@Dao
interface MessageDAO {
    @Insert
    fun insert(message: Message)

    @Query("SELECT * from messages ORDER BY createdAt ASC")
    fun getAllMessages(): List<Message>
}

@Dao
interface UserDAO {
    @Insert
    fun insertUsers(vararg users: User)

    @Query("SELECT * FROM users WHERE login = :user")
    fun getUser(user: String): User?
}

@Dao
interface OrganizationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg organizations: Organization)
}

@Dao
interface OrganizationMembersDAO {
    @Query("SELECT * FROM organizations " +
            "INNER JOIN organization_members ON organizations.login=organization_members.organization " +
            "WHERE user = :user")
    fun getUserOrganizations(user: String): List<Organization>
}

@Dao
interface TeamMembersDAO {
    @Query("SELECT * FROM users " +
            "INNER JOIN team_members ON users.login=team_members.user " +
            "WHERE team = :team AND organization = :organization")
    fun getTeamMembers(team: Int, organization: String): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg members: TeamMember)
}


@Database(entities = [OrganizationMember::class, Organization::class, User::class,
                        Team::class, TeamMember::class, Message::class], version = 1)
@TypeConverters(Converters::class)
abstract class YAMADatabase : RoomDatabase() {
    abstract fun teamDAO(): TeamDAO
    abstract fun teamMembersDAO(): TeamMembersDAO
    abstract fun messagesDAO(): MessageDAO
    abstract fun userDAO(): UserDAO
    abstract fun organizationDAO(): OrganizationDAO
    abstract fun organizationMembersDAO(): OrganizationMembersDAO
}