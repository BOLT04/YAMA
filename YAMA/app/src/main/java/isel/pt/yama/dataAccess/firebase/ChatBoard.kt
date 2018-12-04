package isel.pt.yama.dataAccess.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.kotlinx.runAsync
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.ReceivedMessageMD


class ChatBoard(private val app: YAMAApplication) {
    val db = FirebaseFirestore.getInstance()
    private val teamsRef = db.collection("teams")

    // teamName -> registrations (makes it possible to unregister)
    private val observedTeams = HashMap<Int, ListenerRegistration>()

    // teamName -> msgId, msgDto
    public var globalTeamChats : MutableMap<Int, TeamChat> = mutableMapOf()

    public fun getTeamChat(id: Int): TeamChat{
        var tc = globalTeamChats[id]
        if(tc==null){
            tc= TeamChat()
            globalTeamChats[id] = tc
        }

        return tc


    }


    public class TeamChat{
        val chatLog: MutableList<MutableLiveData<MessageMD>> = mutableListOf()
        val liveData =  MutableLiveData<List<MutableLiveData<MessageMD>>>()

        init {
            liveData.value=chatLog
        }

        fun add(message : MessageMD): MutableLiveData<MessageMD> {

            val mld = MutableLiveData<MessageMD>()
            mld.value = message
            chatLog.add(mld)

            updateList()

            return mld
        }

        fun updateList(){
            //liveData.value=liveData.value
            liveData.value = liveData.value
        }

        fun toList(): List<MutableLiveData<MessageMD>> =
                chatLog.toList().sortedBy { it.value?.createdAt }

    }

/*
    private var teamChats : MutableMap<Int, MutableMap<String,MessageMD>> = mutableMapOf()
    // teamId -> msgList(=chatLog)
    var content : MutableMap<Int, MutableLiveData<List<MutableLiveData<MessageMD>>>> = mutableMapOf()

    */


    // single team chat
    //private var chat : MutableList<MessageDto> = mutableListOf()

    fun associateTeam(teamId : Int) {
        if (observedTeams.containsKey(teamId))
            return

        val teamChat = TeamChat()

        globalTeamChats[teamId] = teamChat
        Log.d(app.TAG, "associateTeam: teamId = $teamId")
        val registration = teamsRef.document(teamId.toString())
                .collection("messages")
                .orderBy("createdAt")
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w(app.TAG, "Listen failed.", e)
                        return@EventListener
                    }
                    runAsync {
                        for (dc in snapshots!!.documentChanges)
                            if (dc.type == DocumentChange.Type.ADDED)
                                app.repository.dtoToMessage(dc.document.toObject(MessageDto::class.java))
                                    {
                                        val messageLD = teamChat.add(it)
                                        if (it is ReceivedMessageMD) {
                                            app.repository.getAvatarImageFromUrl(it.user.avatarUrl){
                                                _ -> messageLD.value=messageLD.value
                                            }
                                        }
                                    }

                    }
                })
        observedTeams[teamId] = registration
    }

    fun post(newMessage: MessageDto, teamId: Int) {
        db.collection("teams")
                .document(teamId.toString())
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.d("YAMAApp", "post: MessageMD sent with success")
                }
                .addOnFailureListener{
                    Log.d("YAMAApp", "post: MessageMD failed to be sent")
                    Log.d("YAMAApp", it.toString())
                }
    }
}