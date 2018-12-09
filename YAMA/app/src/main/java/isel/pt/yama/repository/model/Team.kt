package isel.pt.yama.repository.model

class Team (val name : String,
            val id : Int,
            val description : String?,
            var users : List<User>? = null)