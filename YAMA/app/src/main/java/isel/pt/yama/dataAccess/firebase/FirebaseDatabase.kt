package isel.pt.yama.dataAccess.firebase

import com.android.volley.VolleyError
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.dto.MessageDto

class FirebaseDatabase(private val chatBoard : ChatBoard){
    fun sendMessage(message: MessageDto, team: Team) {
        chatBoard.post(message, team.id)
    }

    fun getSubscribedTeams(user: User, success: (List<Team>) -> Unit, fail: (VolleyError) -> Unit) {
        chatBoard.getSubscribedTeams(user)
    }


}