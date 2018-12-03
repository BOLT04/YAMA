package isel.pt.yama.dataAccess.firebase

import isel.pt.yama.dataAccess.database.Team
import isel.pt.yama.dto.MessageDto

class FirebaseDatabase(private val chatBoard : ChatBoard){
    fun sendMessage(message: MessageDto, team: Team) {
        chatBoard.post(message, team.name)
    }
}