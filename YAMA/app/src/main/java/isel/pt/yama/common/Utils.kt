package isel.pt.yama.common

import android.widget.Toast
import com.android.volley.VolleyError
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication

fun defaultErrorHandler(app: YAMAApplication, err: VolleyError? = null) {
    Toast.makeText(app, R.string.error_network, Toast.LENGTH_LONG).show()
}