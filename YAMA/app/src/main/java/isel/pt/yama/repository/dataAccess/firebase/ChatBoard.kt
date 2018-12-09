package isel.pt.yama.repository.dataAccess.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.kotlinx.runAsync
import isel.pt.yama.repository.dataAccess.dto.MessageDto
import isel.pt.yama.repository.dataAccess.dto.TeamDto
import isel.pt.yama.repository.dataAccess.dto.UserAssociationDto
import isel.pt.yama.repository.model.*
import java.util.*


class ChatBoard(private val app: YAMAApplication) {
    val db = FirebaseFirestore.getInstance()
    private val teamsRef = db.collection("teams")
    private val usersRef = db.collection("users")
    private val userChatsRef = db.collection("userChats")

    // teamID -> registrations (makes it possible to unregister)
    private val observedTeams = HashMap<Int, ListenerRegistration>()
    private val observedDM = HashMap<String, ListenerRegistration>()


    // Used to observe da observar vários chats de várias teams
    //e saberes em qual é que vais meter as msgs q chegam
    var globalTeamChats : MutableMap<Int, Chat> = mutableMapOf()
    var globalUserChats : MutableMap<String, UserChat> = mutableMapOf()


    fun start() {
        val user = app.repository.currentUser!!

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
                            if (dc.type == DocumentChange.Type.ADDED /*&& dc.document.id!= dummy*/) {

                                val userAssociation = dc.document.toObject(UserAssociationDto::class.java)
                                val chatId = userAssociation.chatId
                                val otherLogin = dc.document.id

                                Log.v("DM DEBUG", "listeing to $chatId")

                                var userChat = globalUserChats[otherLogin]

                                if(userChat==null){
                                    userChat = UserChat(chatId)
                                    globalUserChats[dc.document.id]=userChat
                                }


                                if(observedDM[otherLogin] == null) {

                                    val registration = userChatsRef
                                            .document(chatId)
                                            .collection("messages")
                                            .addSnapshotListener(getListener(userChat))

                                    observedDM[otherLogin] = registration
                                }
                            }
                    }
                })
    }

    fun getTeamChat(teamId: Int): Chat{
        var tc = globalTeamChats[teamId]
        if (tc == null){
            tc = Chat()
            globalTeamChats[teamId] = tc
        }

        return tc
    }


    private fun getListener(chat : Chat): EventListener<QuerySnapshot> {
        return EventListener { snapshots, e ->
            if (e != null) {
                Log.w(app.TAG, "Listen failed.", e)
                return@EventListener
            }
            runAsync {
                var d : DocumentChange?  =null
                for (dc in snapshots!!.documentChanges)
                    if (dc.type == DocumentChange.Type.ADDED) {

                        if (dc != null && dc.document.metadata.hasPendingWrites()) {
                            app.repository.msgIconResource = R.mipmap.ic_msg_not_sent
                            Log.v("ICONS", "Local")
                        } else {
                            app.repository.msgIconResource = R.mipmap.ic_msg_sent
                            Log.v("ICONS", "Server")
                        }

                        if (d == null)
                            d = dc
                        else {
                            Log.v("MYTAG_INNER", (dc == d).toString())
                            Log.v("MYTAG_INNER", dc.toString())
                            Log.v("MYTAG_INNER", d.toString())
                        }

                        app.repository.mappers.messageMapper.dtoToModel(dc.document.toObject(MessageDto::class.java))
                        {
                            Log.v("DM DEBUG", "message received")
                            val messageLD = chat.add(it)
                            if (it is SentMessage) {

                                if (dc != null && dc.document.metadata.hasPendingWrites()) {
                                    //Log.v("ICONS", "Local")
                                } else {
                                    it.sent=true
                                    messageLD.value = messageLD.value
                                   // Log.v("ICONS", "Server")
                                }

                            }
                        }
                    }
            }
        }
    }



    fun associateTeam(team: Team) {
        val teamId = team.id
        if (observedTeams.containsKey(teamId))
            return

        val teamChat = Chat()

        globalTeamChats[teamId] = teamChat
        Log.d(app.TAG, "associateTeam: teamId = $teamId")
        val registration = teamsRef.document(teamId.toString())
                .collection("messages")
                //.orderBy("createdAt")
                .addSnapshotListener (getListener(teamChat))
        addToSubscribedTeams(team)
        observedTeams[teamId] = registration
    }


    fun associateUser(otherLogin: String) {

        val currentLogin = app.repository.currentUser!!.login
        val id = Random(Date().time).nextInt().toString()

        if(globalUserChats.containsKey(otherLogin)) return

        val userChat = UserChat(id)

        associateUserAux(currentLogin, otherLogin, id)
        associateUserAux(otherLogin, currentLogin, id)

        globalUserChats[otherLogin]= userChat
    }



    fun postTeamMessage(newMessage: MessageDto, teamId: Int) {
        teamsRef.document(teamId.toString())
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.d(app.TAG, "postTeamMessage: Message sent with success")
                }
                .addOnFailureListener{
                    Log.d(app.TAG, "postTeamMessage: Message failed to be sent")
                    Log.d(app.TAG, it.toString())
                }
    }

    fun postUserMessage(newMessage: MessageDto, user: String) {
        userChatsRef
                .document(globalUserChats[user]!!.fileID)
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.d(app.TAG, "postUserMessage: Message sent with success")
                }
                .addOnFailureListener{
                    Log.d(app.TAG, "postUserMessage: Message failed to be sent")
                    Log.d(app.TAG, it.toString())
                }
    }



    private fun addToSubscribedTeams(team: Team) {
        usersRef.document(app.repository.currentUser?.login!!)
                .collection("chats")
                .document("${team.id}")
                .set(team)
                .addOnSuccessListener{
                    Log.d(app.TAG, "addToSubscribedTeams: success")
                }
                .addOnFailureListener{
                    Log.d(app.TAG, "addToSubscribedTeams: error")
                    Log.d(app.TAG, it.toString())
                }
    }

    fun getSubscribedTeams(user: User, success: (List<Team>) -> Unit, fail: (Exception) -> Unit) {
        usersRef.document(user.login)
                .collection("chats")
                .get()
                .addOnSuccessListener { result ->
                    val map = result.toObjects(TeamDto::class.java)
                            .map(app.repository.mappers.teamMapper::dtoToModel)

                    success(map)
                }
                .addOnFailureListener { exception ->
                    Log.d(app.TAG, "Error getting documents: ", exception)
                    fail(exception)
                }
    }

    fun getSubscribedUsers(user: User, success: (List<UserAssociation>) -> Unit, fail: (Exception) -> Unit) {
        usersRef.document(user.login)
                .collection("userChats")
                .get()
                .addOnSuccessListener { result ->

                    val list = mutableListOf<UserAssociation>()
                    result.toObjects(UserAssociationDto::class.java)
                            .forEach{
                                dto->
                                app.repository.mappers.userAssociationMapper.dtoToModel(dto){
                                        list.add(it)
                                        if(result.size()==list.size)
                                            success(list)
                                }
                            }
                }
                .addOnFailureListener { exception ->
                    Log.d(app.TAG, "Error getting documents: ", exception)
                    fail(exception)
                }
    }





    private fun associateUserAux(firstUser: String, secondUser: String, id: String){

        val association = UserAssociationDto(id, secondUser)

        usersRef.document(firstUser)
                .collection("userChats")
                .document(secondUser)
                .set(association)
                .addOnSuccessListener {
                    Log.d(app.TAG, "addToSubscribedTeams: success")
                }
                .addOnFailureListener {
                    Log.d(app.TAG, "addToSubscribedTeams: error")
                    Log.d(app.TAG, it.toString())
                }
    }

    fun getUserChat(userLogin: String): UserChat {
        if(globalUserChats[userLogin]==null)
            associateUser(userLogin)
        return globalUserChats[userLogin]!!
    }
}



open class Chat{
    val chatLog: MutableList<MutableLiveData<Message>> = mutableListOf()
    val liveData =  MutableLiveData<List<MutableLiveData<Message>>>()

    init {
        liveData.postValue(chatLog)
    }

    fun add(message : Message): MutableLiveData<Message> {
        val mld = MutableLiveData<Message>()
        mld.value = message
        chatLog.add(mld)
        updateList()

        return mld
    }

    fun updateList(){
        liveData.value = liveData.value
    }

    fun toList(): List<MutableLiveData<Message>> =
            chatLog.toList().sortedBy { it.value?.createdAt }

}


class UserChat(val fileID : String) : Chat()


