package isel.pt.yama.dataAccess.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.MessageDto
import isel.pt.yama.dto.TeamDto
import isel.pt.yama.kotlinx.runAsync
import isel.pt.yama.model.MessageMD
import isel.pt.yama.model.ReceivedMessageMD
import isel.pt.yama.model.TeamMD
import isel.pt.yama.model.UserMD
import java.util.*


class ChatBoard(private val app: YAMAApplication) {
    val db = FirebaseFirestore.getInstance()
    private val teamsRef = db.collection("teams")
    private val usersRef = db.collection("users")
    private val userChatsRef = db.collection("userChats")

    // teamName -> registrations (makes it possible to unregister)
    private val observedTeams = HashMap<Int, ListenerRegistration>()

    // teamName -> msgId, msgDto
    //TODO: DOCUMENT what is this for
    // Used to observe da observar vários chats de várias teams
    //e saberes em qual é que vais meter as msgs q chegam
    var globalChats : MutableMap<Int, Chat> = mutableMapOf()


    fun start() {
        val user = app.repository.currentUser!!



        usersRef.document(user.login)
                .collection("userChats")
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w(app.TAG, "Listen failed.", e)
                        return@EventListener
                    }
                    runAsync {
                        for (dc in snapshots!!.documentChanges)
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val chatId = dc.document["chatId"]

                                userChatsRef
                                        .document(chatId.toString())
                                        .collection("messages")
                                        .addSnapshotListener(this::onDocumentChange)
                            }

                    }
                })
    }


    fun onDocumentChange(snapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            Log.w(app.TAG, "Listen failed.", e)
            return
        }

        val chat = Chat()

        runAsync {
            for (dc in snapshots!!.documentChanges)
                if (dc.type == DocumentChange.Type.ADDED) {
                    val dto = dc.document.toObject(MessageDto::class.java)
                    app.repository.mappers.messageMapper.dtoToMD(dto) {
                        val messageLD = chat.add(it)
                        if (it is ReceivedMessageMD) {
                            app.repository.getAvatarImageFromUrl(it.user.avatar_url) { _ ->
                                messageLD.value = messageLD.value
                            }
                        }
                    }
                }
        }
    }


    fun getTeamChat(id: Int): Chat{
        var tc = globalChats[id]
        if(tc==null){
            tc= Chat()
            globalChats[id] = tc
        }

        return tc
    }

    private data class TeamId(val teamId: Int)


    class Chat{
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

    fun associateTeam(team: TeamMD) {
        val teamId = team.id
        if (observedTeams.containsKey(teamId))
            return

        val teamChat = Chat()

        globalChats[teamId] = teamChat
        Log.d(app.TAG, "associateTeam: teamId = $teamId")
        val registration = teamsRef.document(teamId.toString())
                .collection("messages")
                .orderBy("createdAt")
                .addSnapshotListener { snapshots, e ->
                    onDocumentChange(snapshots, e)
                }
        addToSubscribedTeams(team)
        observedTeams[teamId] = registration
    }

    fun post(newMessage: MessageDto, teamId: Int) {
        teamsRef.document(teamId.toString())
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.d(app.TAG, "post: MessageMD sent with success")
                }
                .addOnFailureListener{
                    Log.d(app.TAG, "post: MessageMD failed to be sent")
                    Log.d(app.TAG, it.toString())
                }
    }

    fun addToSubscribedTeams(team: TeamMD) {
        usersRef.document(app.repository.currentUser?.login!!)
                .collection("chats")
                .add(team) // TODO: needs to be a POJO!!!
                .addOnSuccessListener{
                    Log.d(app.TAG, "addToSubscribedTeams: success")
                }
                .addOnFailureListener{
                    Log.d(app.TAG, "addToSubscribedTeams: error")
                    Log.d(app.TAG, it.toString())
                }
    }

    fun getSubscribedTeams(user: UserMD, success: (List<TeamMD>) -> Unit, fail: (Exception) -> Unit) {
        usersRef.document(user.login)
                .collection("chats")
                .get()
                .addOnSuccessListener { result ->
                    success(result.toObjects(TeamDto::class.java).map(app.repository.mappers.teamMapper::dtoToMD))
                   /* for (document in result) {
                        associateTeam(document.toObject(TeamId::class.java).teamId)
                        Log.d(app.TAG, document.id + " => " + document.data)
                    }*/
                }
                .addOnFailureListener { exception ->
                    Log.d(app.TAG, "Error getting documents: ", exception)
                    fail(exception)
                }
    }


    fun associateUser(user: UserMD) {
        val currentLogin = app.repository.currentUser!!.login
        val otherLogin = user.login
        val id = Random(Date().time).nextLong()

        associateUserAux(currentLogin, otherLogin, id)
        associateUserAux(otherLogin, currentLogin, id)
    }



    fun associateUserAux(firstUser: String, secondUser: String, id: Long){


        val map = mutableMapOf("chatId" to id) as Map<String, Any>

        usersRef.document(firstUser)
                .collection("userChats")
                .document(secondUser)
                .set(map)
                .addOnSuccessListener {
                    Log.d(app.TAG, "addToSubscribedTeams: success")
                }
                .addOnFailureListener {
                    Log.d(app.TAG, "addToSubscribedTeams: error")
                    Log.d(app.TAG, it.toString())
                }
    }


}