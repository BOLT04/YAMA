package isel.pt.yama.dataAccess.firebase

import com.android.volley.VolleyError
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserMD

class FirebaseDatabase(private val chatBoard : ChatBoard){
    fun sendMessage(message: MessageDto, team: TeamMD) {
        chatBoard.post(message, team.id)
    }

    fun getSubscribedTeams(user: UserMD, success: (List<TeamMD>) -> Unit, fail: (Exception) -> Unit) {
        chatBoard.getSubscribedTeams(user, success, fail)
    }


}