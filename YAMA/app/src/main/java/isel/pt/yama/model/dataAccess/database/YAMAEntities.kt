package isel.pt.yama.model.dataAccess.database

import androidx.room.Entity

@Entity(tableName = "teams", primaryKeys = arrayOf("name", "id"))
data class Team (
        val name : String,
        val id : Int,
        val description : String?)
