package isel.pt.yama.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import isel.pt.yama.YAMAApplication
import isel.pt.yama.common.defaultErrorHandler
import isel.pt.yama.dataAccess.database.Organization
import isel.pt.yama.dataAccess.database.User

class LoginViewModel(val app : YAMAApplication) : AndroidViewModel(app) {

    var textUser: String = ""
    var textOrganization: String = ""
    var textToken: String = ""

    val userInfo: MutableLiveData<User> = MutableLiveData()
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
        // Usage of !! is guaranteed not to be null because its initialized by LoginActivity
        app.repository.getUserDetails(textUser, textToken!!,{
            userInfo.value = it
        }, {
            defaultErrorHandler(app)
        })

        app.repository.getUserOrganizations(textUser, textToken!!, {
            userOrganizations.value = it
        }, {
            defaultErrorHandler(app)
        })
    }

    private fun tryLogin(): LiveData<Boolean> =
        userInfo.combineAndCompute(userOrganizations) { a, b ->
            checkLoginInfo(a, b)
        }

    private fun checkLoginInfo(usr : User, orgs: List<Organization>) =
        if (usr.login == textUser) {
            val organization = orgs.firstOrNull {
                it.login == textOrganization
            }
            organization != null
        } else false
}