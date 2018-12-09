package isel.pt.yama.common

import android.util.Log
import android.widget.Toast
import com.android.volley.VolleyError
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication

fun defaultErrorHandler(app: YAMAApplication, err: VolleyError? = null) {
    Toast.makeText(app, R.string.error_network, Toast.LENGTH_LONG).show()
}

const val PREFIX_TAG = "YAMAApplication"
fun log(tag: String, msg: String) {
    Log.v("$PREFIX_TAG/$tag", msg)
}