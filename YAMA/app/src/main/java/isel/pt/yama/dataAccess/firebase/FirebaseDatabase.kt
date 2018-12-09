package isel.pt.yama.dataAccess.firebase

import com.android.volley.VolleyError
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dataAccess.database.User
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserAssociation
import isel.pt.yama.model.UserMD

class FirebaseDatabase(private val chatBoard : ChatBoard){
    fun sendMessage(message: MessageDto, team: TeamMD) {
        chatBoard.post(message, team.id)
    }

    fun getSubscribedTeams(user: UserMD, success: (List<TeamMD>) -> Unit, fail: (Exception) -> Unit) {
        chatBoard.getSubscribedTeams(user, success, fail)
    }

    fun getSubscribedUsers(user: UserMD, success: (List<UserAssociation>) -> Unit, fail: (Exception) -> Unit) {
        chatBoard.getSubscribedUsers(user, success, fail)
    }

    fun sendUserMessage(message: MessageDto, user: String) {
        chatBoard.postUserMessage(message, user)
    }


}