package isel.pt.yama.repository.dataAccess.firebase

import isel.pt.yama.repository.dataAccess.dto.MessageDto
import isel.pt.yama.repository.model.Team
import isel.pt.yama.repository.model.UserAssociation
import isel.pt.yama.repository.model.User

class FirebaseDatabase(private val chatBoard : ChatBoard){
    fun sendMessage(message: MessageDto, team: Team) {
        chatBoard.postTeamMessage(message, team.id)
    }

    fun getSubscribedTeams(user: User, success: (List<Team>) -> Unit, fail: (Exception) -> Unit) {
        chatBoard.getSubscribedTeams(user, success, fail)
    }

    fun getSubscribedUsers(user: User, success: (List<UserAssociation>) -> Unit, fail: (Exception) -> Unit) {
        chatBoard.getSubscribedUsers(user, success, fail)
    }

    fun sendUserMessage(message: MessageDto, user: String) {
        chatBoard.postUserMessage(message, user)
    }


}