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
    var globalTeamChats : MutableMap<Int, Chat> = mutableMapOf()
    var globalUserChats : MutableMap<String, UserChat> = mutableMapOf()


    fun start() {
        val user = app.repository.currentUser!!

        usersRef
                .document(user.login)
                .collection("userChats")
                .add("abcd" to "id")

        usersRef
                .document(user.login)
                .collection("userChats")
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w(app.TAG, "Listen failed.", e)
                        return@EventListener
                    }
                    runAsync {
                        for (dc in snapshots!!.documentChanges)
                            if (dc.type == DocumentChange.Type.ADDED) {

                                val chatId = dc.document["chatId"] as String

                                Log.v("DM DEBUG", "listeing to $chatId")

                                val userChat = UserChat(chatId)

                                globalUserChats[dc.document.id] = userChat
                                userChatsRef
                                        .document(chatId.toString())
                                        .collection("messages")
                                        .addSnapshotListener(getListener(userChat))
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
        var tc = globalTeamChats[id]
        if(tc==null){
            tc= Chat()
            globalTeamChats[id] = tc
        }

        return tc
    }





    fun getListener(chat : Chat): EventListener<QuerySnapshot> {
        return EventListener<QuerySnapshot> { snapshots, e ->
            if (e != null) {
                Log.w(app.TAG, "Listen failed.", e)
                return@EventListener
            }
            runAsync {
                for (dc in snapshots!!.documentChanges)
                    if (dc.type == DocumentChange.Type.ADDED)
                        app.repository.mappers.messageMapper.dtoToMD(dc.document.toObject(MessageDto::class.java))
                        {
                            Log.v("DM DEBUG", "message received")
                            val messageLD = chat.add(it)
                            if (it is ReceivedMessageMD) {
                                app.repository.getAvatarImageFromUrl(it.user.avatar_url){
                                    _ -> messageLD.value = messageLD.value
                                }
                            }
                        }

            }
        }
    }



    fun associateTeam(team: TeamMD) {
        val teamId = team.id
        if (observedTeams.containsKey(teamId))
            return

        val teamChat = Chat()

        globalTeamChats[teamId] = teamChat
        Log.d(app.TAG, "associateTeam: teamId = $teamId")
        val registration = teamsRef.document(teamId.toString())
                .collection("messages")
                .orderBy("createdAt")
                .addSnapshotListener (getListener(teamChat))
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

    fun postUserMessage(newMessage: MessageDto, user: String) {
        userChatsRef
                .document(globalUserChats[user]!!.fileID)
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
                    val map = result.toObjects(TeamDto::class.java)
                            .map(app.repository.mappers.teamMapper::dtoToMD)

                   // map.forEach{teamMD -> associateTeam(teamMD)}
                    success(map)
                }
                .addOnFailureListener { exception ->
                    Log.d(app.TAG, "Error getting documents: ", exception)
                    fail(exception)
                }
    }


    fun associateUser(user: UserMD) {
        val otherLogin = user.login
        val currentLogin = app.repository.currentUser!!.login

        if(globalUserChats[otherLogin]!=null) return

       // usersRef.document(currentLogin).



        val id = Random(Date().time).nextInt().toString()

        associateUserAux(currentLogin, otherLogin, id)
        associateUserAux(otherLogin, currentLogin, id)

        globalUserChats[otherLogin]= UserChat(id)
    }



    fun associateUserAux(firstUser: String, secondUser: String, id: String){


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

    fun getUserChat(userLogin: String): UserChat? =
            globalUserChats[userLogin]



}



open class Chat{
    val chatLog: MutableList<MutableLiveData<MessageMD>> = mutableListOf()
    val liveData =  MutableLiveData<List<MutableLiveData<MessageMD>>>()

    init {
        liveData.postValue(chatLog)
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



class UserChat(val fileID : String) : Chat()


