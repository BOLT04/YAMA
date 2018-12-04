package isel.pt.yama.model

class UserMD (
    val login : String,
    val id : Int,
    val avatar_url : String,
    val name : String?,
    val email : String?,
    val followers : Int?,
    val following : Int,
    val organizations : List<OrganizationMD>?){

    override fun equals(other: Any?): Boolean =
            other is UserMD && this.login==other.login



}