package isel.pt.yama.dataAccess.firebase

import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dto.SentMessage

class FirebaseDatabase(private val chatBoard : ChatBoard){

    fun sendMessage(message: SentMessage, team: Team) {
        chatBoard.post(message.toMap(), team.name)
    }

    fun getMessages(team: Team) {
        chatBoard.get(team.name)
    }
}