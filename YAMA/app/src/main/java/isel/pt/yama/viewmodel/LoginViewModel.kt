package isel.pt.yama.viewmodel

import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import isel.pt.yama.R
import isel.pt.yama.YAMAApplication
import isel.pt.yama.dto.Organization
import isel.pt.yama.dto.UserDto
import isel.pt.yama.network.GetRequestOrganizations
import isel.pt.yama.network.GetRequestUser

class LoginViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    var textUser: String? = null
    var textOrganization: String? = null
    var textToken: String? = null

    val userInfo: MutableLiveData<UserDto> = MutableLiveData()
    private val userOrganizations: MutableLiveData<List<Organization>> = MutableLiveData()

    val loginIsOk: LiveData<Boolean> = tryLogin()

    private fun <T, A, B> LiveData<A>.combineAndCompute(other: LiveData<B>, onChange: (A, B) -> T): MediatorLiveData<T> {

        var source1emitted = false
        var source2emitted = false

        val result = MediatorLiveData<T>()

        val mergeF = {
            val source1Value = this.value
            val source2Value = other.value

            if (source1emitted && source2emitted) {
                result.value = onChange.invoke(source1Value!!, source2Value!!)
            }
        }

        result.addSource(this) { source1emitted = true; mergeF.invoke() }
        result.addSource(other) { source2emitted = true; mergeF.invoke() }

        return result
    }

    fun submitLogin() {
        getUserDetails()
        getUserOrganizations()
    }

    private fun tryLogin(): LiveData<Boolean> {

        return userInfo.combineAndCompute(userOrganizations) { a, b ->
            checkLoginInfo(a, b)
        }
    }

    private fun checkLoginInfo(usr : UserDto, orgs: List<Organization>) : Boolean {
        if (usr.login == textUser) {
            val organization = orgs.firstOrNull {
                it.login == textOrganization
            }
            if (organization != null)
                return true
        }
        return false
    }

    private fun getUserDetails() {
        //val queue = getApplication<YAMAApplication>().queue
        val request = GetRequestUser(
                "https://api.github.com/user",
                Response.Listener { userInfo.value = it },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                },
                mutableMapOf(Pair("Authorization", "token $textToken"))
        )

        app.queue.add(request)
    }

    private fun getUserOrganizations() {
        val queue = getApplication<YAMAApplication>().queue
        val url = "https://api.github.com/user/orgs"

        val request = GetRequestOrganizations(
                url,
                Response.Listener { userOrganizations.value = it },
                Response.ErrorListener {
                    Toast.makeText(getApplication(), R.string.error_network, Toast.LENGTH_LONG).show()
                },
                mutableMapOf(Pair("Authorization", "token $textToken"))
        )
        queue.add(request)
    }
}