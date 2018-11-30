package isel.pt.yama.model.dataAccess.firebase

import isel.pt.yama.Chat
import isel.pt.yama.dto.SentMessage
import isel.pt.yama.model.dataAccess.database.Message
import isel.pt.yama.model.dataAccess.database.Team

class FirebaseDatabase(private val chatBoard : Chat){

    fun sendMessage(message: SentMessage, team: Team) {
        chatBoard.post(message.toMap(), team.name)
    }
}