package net.mready.postit.data

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface PostItAPI {
    @POST("auth/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("display_name") displayName: String
    ): AuthResult

    @POST("auth/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): AuthResult

    @POST("posts")
    @FormUrlEncoded
    suspend fun postMessage(@Field("message") message: String): Response<PostResponse>

    @GET("posts")
    suspend fun getPosts(): PostList

    companion object {
        const val BASE_URL = "https://intern-hackathon.mready.net/api/"
    }
}