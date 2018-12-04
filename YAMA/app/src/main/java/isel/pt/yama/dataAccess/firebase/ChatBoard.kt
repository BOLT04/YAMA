package isel.pt.yama.dataAccess.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.ReceivedMessage
import isel.pt.yama.kotlinx.runAsync


class ChatBoard(private val app: YAMAApplication) {
    val db = FirebaseFirestore.getInstance()
    private val teamsRef = db.collection("teams")

    // teamName -> registrations (makes it possible to unregister)
    private val observedTeams = HashMap<String, ListenerRegistration>()

    // teamName -> msgId, msgDto
    private var teamsMessages : MutableMap<String, MutableMap<String, MessageDto>> = mutableMapOf()

    // teamName -> msgList(=chatLog)
    var content : MutableMap<String, MutableLiveData<List<MessageDto>>> = mutableMapOf()

    // single team chat
    //private var chat : MutableList<MessageDto> = mutableListOf()

    fun associateTeam(teamName : String) {
        if (observedTeams.containsKey(teamName)) return
        teamsMessages[teamName] = mutableMapOf()
        content[teamName] = MutableLiveData()
        Log.d(app.TAG, "associateTeam: teamName = $teamName")
        val registration = teamsRef.document(teamName)
                .collection("messages")
                .orderBy("createdAt")
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w(app.TAG, "Listen failed.", e)
                        return@EventListener
                    }
                    val msgMap = teamsMessages[teamName]!!
                    runAsync {
                        for (dc in snapshots!!.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                var msg = dc.document.toObject(MessageDto::class.java)
                                if (app.repository.user?.login != msg.user) {
                                    val avatarUrl = app.repository.userAvatarUrlCache[msg.user]
                                    Log.d(app.TAG, "avatarUrl = $avatarUrl")
                                    if (avatarUrl == null) {
                                        msg = ReceivedMessage(msg)
                                        app.repository.getAvatarImageFromLogin(msg.user) {
                                            msg = ReceivedMessage(msg,
                                                    app.repository.avatarCache[
                                                            app.repository.userAvatarUrlCache[msg.user]
                                                    ])
                                            msgMap[dc.document.id] = msg
                                            content[teamName]?.postValue(ArrayList(msgMap.values))
                                            Log.d(app.TAG, "returning from callback. avatarUrl = $msg.")
                                        }
                                    } else {
                                        msg = ReceivedMessage(msg, app.repository.avatarCache[avatarUrl])
                                    }
                                }
                                msgMap[dc.document.id] = msg
                                content[teamName]?.postValue(ArrayList(msgMap.values))
                            }
                        }
                    }//.andThen {
                        //content[teamName]?.postValue(ArrayList(msgMap.values))
                    //}
                })
        observedTeams[teamName] = registration
    }

    fun post(newMessage: MessageDto, teamName: String) {
        db.collection("teams")
                .document(teamName)
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.d("YAMAApp", "post: Message sent with success")
                }
                .addOnFailureListener{
                    Log.d("YAMAApp", "post: Message failed to be sent")
                    Log.d("YAMAApp", it.toString())
                }
    }
}