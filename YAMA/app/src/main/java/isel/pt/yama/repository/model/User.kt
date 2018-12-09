package isel.pt.yama.repository.model

class User (
    val login : String,
    val id : Int,
    val avatar_url : String,
    val name : String?,
    val email : String?,
    val followers : Int?,
    val following : Int
    //val organizations : List<Organization>?
    ){

    override fun equals(other: Any?): Boolean =
            other is User && this.login==other.login



}