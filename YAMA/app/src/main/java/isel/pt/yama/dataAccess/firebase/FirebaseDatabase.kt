package isel.pt.yama.dataAccess.firebase

import isel.pt.yama.Chat
import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dto.SentMessage

class FirebaseDatabase(private val chatBoard : Chat){

    fun sendMessage(message: SentMessage, team: Team) {
        chatBoard.post(message.toMap(), team.name)
    }
}