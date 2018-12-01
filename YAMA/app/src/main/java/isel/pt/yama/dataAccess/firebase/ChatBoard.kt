package isel.pt.yama.dataAccess.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.MessageDto


class ChatBoard(private val app: YAMAApplication) {

    val content = MutableLiveData<List<MessageDto>>()
    val db = FirebaseFirestore.getInstance()
    val teamsRef = db.collection("teams")
    val teamsObserved = HashMap<String, ListenerRegistration>()

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
                    for (dc in snapshots!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            MessageDto
                                for (msg in dc.document.data)
                                    Log.d("YAMAApp", "${msg.key} : ${msg.value}")
                        }
                    }
/*                    for (doc in value!!) {
                        for (msg in doc.data)
                            Log.d("YAMAApp", "${msg.key} : ${msg.value}")
                    }*/
                })
        teamsObserved[teamName] = registration
    }


    fun post(newMessage: Map<String, String>, teamName: String) {
        Log.d("YAMAApp", "inside post")
        db.collection("teams")
                .document(teamName)
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.v("YAMAApp", "Message sent with success")
                }
                .addOnFailureListener{
                    Log.v("YAMAApp", "Message failed to be sent")
                    Log.v("YAMAApp", it.toString())
                }
    }

    fun get(teamName: String) {
        db.collection("teams")
                .document(teamName)
                .collection("messages")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("FS MSG", document.id + " => " + document.data)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("FS MSG", "Error getting documents.", exception)
                }
    }
}