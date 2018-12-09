package isel.pt.yama.repository.dataAccess.database

import androidx.room.*

@Dao
interface TeamDAO {
    /*
    @Query("SELECT * FROM teams WHERE date LIKE :date")
    fun getAllByDate(date: Calendar): List<TeamDto>
    */

    @Query("SELECT * FROM teams WHERE organizationID = :organizationID")
    fun getOrganizationTeams(organizationID: String): List<TeamDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg teamsDB: TeamDB)

    @Delete
    fun delete(quote: TeamDB)
}

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg usersDB: UserDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userDB: UserDB)

    @Query("SELECT * FROM users WHERE login = :user")
    fun getUser(user: String): UserDB?
}

@Dao
interface OrganizationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg organizationsDB: OrganizationDB)
}

@Dao
interface OrganizationMembersDAO {
    @Query("""SELECT * FROM organizations
            INNER JOIN organization_members ON organizations.login=organization_members.organizationID
            WHERE user = :user""")
    fun getUserOrganizations(user: String): List<OrganizationDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg organizationMembersDB: OrganizationMemberDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(organizationMemberDB: OrganizationMemberDB)
}

@Dao
interface TeamMembersDAO {
    @Query("""SELECT * FROM users
            INNER JOIN team_members ON users.login=team_members.user
            WHERE team_members.team = :team AND team_members.organizationID = :organizationID""")
    fun getTeamMembers(team: Int, organizationID: String): List<UserDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg membersDB: TeamMemberDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(memberDB: TeamMemberDB)
}


@Database(entities = [OrganizationMemberDB::class, OrganizationDB::class, UserDB::class,
                        TeamDB::class, TeamMemberDB::class], version = 3)

@TypeConverters(Converters::class)
abstract class YAMADatabase : RoomDatabase() {
    abstract fun teamDAO(): TeamDAO
    abstract fun teamMembersDAO(): TeamMembersDAO
    abstract fun userDAO(): UserDAO
    abstract fun organizationDAO(): OrganizationDAO
    abstract fun organizationMembersDAO(): OrganizationMembersDAO
}