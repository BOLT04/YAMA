package isel.pt.yama.model

class TeamMD (val name : String,
              val id : Int,
              val description : String?,
              var users : List<UserMD>? = null)