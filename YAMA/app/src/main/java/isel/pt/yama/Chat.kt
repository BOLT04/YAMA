package isel.pt.yama

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentReference




class Chat(private val app: YAMAApplication) {

    val content = MutableLiveData<Map<String, String>>()
    val db = FirebaseFirestore.getInstance()
    //val teamsRef = db.collection("teams")

    init {

       /* teamsRef.document("teamA").addSnapshotListener(EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.w(app.TAG, "Listen failed.", e)
                return@EventListener
            }
            if (snapshot != null && snapshot.exists()) {
                content.value = snapshot.data.orEmpty().mapValues { it.value.toString() }
                Log.d(app.TAG, "data: ${snapshot.data}")
            }
        })*/
    }

    fun post(newMessage: Map<String, String>, teamName: String) {
        db.collection("teams")
                .document(teamName)
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.v("SendMessage", "Message sent with success")
                }
                .addOnFailureListener{
                    Log.v("SendMessage", "Message failed to be sent")
                    Log.v("SendMessage", it.toString())
                }
        /*db.collection("teams")
                .document(teamName)
                .collection("messages")
                .add(newMessage)
                .addOnSuccessListener{
                    Log.v("SendMessage", "Message sent with success")
                }
                .addOnFailureListener{
                    Log.v("SendMessage", "Message failed to be sent")
                    Log.v("SendMessage", it.toString())
                }*/

    }
}