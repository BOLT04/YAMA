package isel.pt.yama.dataAccess.database

import androidx.room.*

@Dao
interface TeamDAO {
    /*
    @Query("SELECT * FROM teams WHERE date LIKE :date")
    fun getAllByDate(date: Calendar): List<TeamDto>
    */

    @Query("SELECT * FROM teams WHERE organizationID = :organizationID")
    fun getOrganizationTeams(organizationID: String): List<Team>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg teams: Team)

    @Delete
    fun delete(quote: Team)
}

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser( user: User)

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
    @Query("""SELECT * FROM organizations
            INNER JOIN organization_members ON organizations.login=organization_members.organizationID
            WHERE user = :user""")
    fun getUserOrganizations(user: String): List<Organization>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg organizationMembers: OrganizationMember)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(organizationMember: OrganizationMember)
}

@Dao
interface TeamMembersDAO {
    @Query("""SELECT * FROM users
            INNER JOIN team_members ON users.login=team_members.user
            WHERE team_members.team = :team AND team_members.organizationID = :organizationID""")
    fun getTeamMembers(team: Int, organizationID: String): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg members: TeamMember)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(member: TeamMember)
}


@Database(entities = [OrganizationMember::class, Organization::class, User::class,
                        Team::class, TeamMember::class], version = 3)
@TypeConverters(Converters::class)
abstract class YAMADatabase : RoomDatabase() {
    abstract fun teamDAO(): TeamDAO
    abstract fun teamMembersDAO(): TeamMembersDAO
    abstract fun userDAO(): UserDAO
    abstract fun organizationDAO(): OrganizationDAO
    abstract fun organizationMembersDAO(): OrganizationMembersDAO
}