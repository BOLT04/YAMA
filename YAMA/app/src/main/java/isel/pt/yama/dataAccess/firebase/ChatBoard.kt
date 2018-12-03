package isel.pt.yama.dataAccess.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.ReceivedMessage
import isel.pt.yama.kotlinx.runAsync
import kotlinx.coroutines.runBlocking


class ChatBoard(private val app: YAMAApplication) {

    val content = MutableLiveData<List<MessageDto>>()
    val db = FirebaseFirestore.getInstance()
    private val teamsRef = db.collection("teams")
    private val teamsObserved = HashMap<String, ListenerRegistration>()
    private var chat : MutableList<MessageDto> = mutableListOf()
    var aa : MutableMap<String, MessageDto> = mutableMapOf()

    fun associateTeam(teamName : String) {
        if (teamsObserved.containsKey(teamName)) return
        Log.d("YAMAApp", "teamName = $teamName")
        val registration = teamsRef.document(teamName)
                .collection("messages")
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w("YAMAApp", "Listen failed.", e)
                        return@EventListener
                    }
                    runAsync {
                        for (dc in snapshots!!.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                var msg = dc.document.toObject(MessageDto::class.java)
                                if (app.repository.user?.login != msg.user) {
                                    app.repository.getAvatarImage(msg.user) { _ ->
                                        aa[dc.document.id] = ReceivedMessage(msg, app.repository.avatarCache[msg.user]!!)
                                    }
                                    msg = ReceivedMessage(msg, )//TODO: Imagem por omissão)//app.repository.avatarCache[msg.user]!!)
                                }
                                //chat.add(msg)
                                aa[dc.document.id] = msg
                                chat = aa.values as MutableList<MessageDto>
                            }
                        }
                    }.andThen {
                        content.value = chat
                    }
                })
        teamsObserved[teamName] = registration
    }

    fun updateImage(msgId : String, msg : MessageDto) {
        aa[msgId] = ReceivedMessage(msg, app.repository.avatarCache[msg.user]!!)
    }

    fun post(newMessage: MessageDto, teamName: String) {
        Log.d("YAMAApp", "inside post")
        db.collection("teams")
                .document(teamName)
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.d("YAMAApp", "Message sent with success")
                }
                .addOnFailureListener{
                    Log.d("YAMAApp", "Message failed to be sent")
                    Log.d("YAMAApp", it.toString())
                }
    }
}