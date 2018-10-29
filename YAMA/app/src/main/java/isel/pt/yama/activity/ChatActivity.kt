package isel.pt.yama.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import isel.pt.yama.R
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    //TODO: make view model for this activity that holds a list of  an object containing sent message and avatar img of the user who sent it!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sendBtn.setOnClickListener {
            val msg = userMessageTxt.text
            if (msg.isEmpty()) {
                Toast.makeText(this, "Please provide a message", Toast.LENGTH_SHORT)
                        .show()
                return@setOnClickListener
            }

            messagesList.add

            // Reset edit text view
            //userMessageTxt.text = ""
        }
    }
}
