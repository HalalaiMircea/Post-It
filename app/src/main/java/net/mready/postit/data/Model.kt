package net.mready.postit.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class AuthResult(val data: Data?, val error: Error?) {
    data class Data(val user: LoggedInUser, val token: String)

    data class Error(val code: Int, val message: String)
}

data class Post(
    val id: Int,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("user_id") val userId: Int?,
    val message: String,
    @SerializedName("created_at") val createdAt: Date,
)

data class PostResponse(@SerializedName("data") val post: Post)

data class PostList(@SerializedName("data") val posts: List<Post>)

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val id: Int,
    val username: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("created_by") val createdBy: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val displayName: String
)