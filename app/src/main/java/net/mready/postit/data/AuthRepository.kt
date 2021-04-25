package net.mready.postit.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

const val LOGGED_USER = "LOGGED_USER"
const val JWT_TOKEN = "JWT_TOKEN"

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val api: PostItAPI,
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? =
        gson.fromJson(sharedPrefs.getString(LOGGED_USER, null), LoggedInUser::class.java)
        private set

    val isLoggedIn: Boolean
        get() = user != null

    suspend fun register(request: RegisterRequest): Result<LoggedInUser> {
        val authResult: AuthResult = api.register(
            username = request.username,
            password = request.password,
            displayName = request.displayName
        )
        return loginOrRegister(authResult)
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        val authResult: AuthResult = api.login(username, password)
        return loginOrRegister(authResult)
    }

    fun logout() {
        user = null
        sharedPrefs.edit {
            remove(JWT_TOKEN)
            remove(LOGGED_USER)
        }
    }

    private fun loginOrRegister(authResult: AuthResult): Result<LoggedInUser> {
        // If the AuthResult is not null, login the user
        authResult.data?.let {
            val loggedInUser = it.user
            user = loggedInUser
            // Save token and user data to shared prefs
            sharedPrefs.edit {
                putString(JWT_TOKEN, it.token)
                putString(LOGGED_USER, gson.toJson(loggedInUser))
            }
            return Result.Success(loggedInUser)
        }
        // Result wrapped in sealed class type Error if null
        return Result.Error(Exception(authResult.error!!.message))
    }
}
