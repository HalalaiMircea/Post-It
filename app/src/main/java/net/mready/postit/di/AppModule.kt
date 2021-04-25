package net.mready.postit.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.mready.postit.data.JWT_TOKEN
import net.mready.postit.data.PostItAPI
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val TAG = "AppModule"

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("net.mready.postit_preferences", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideRetrofit(sharedPrefs: SharedPreferences, gson: Gson): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                // Executes the block only if the receiver of ?.let is nonNull
                sharedPrefs.getString(JWT_TOKEN, null)?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }

                return@addInterceptor chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(PostItAPI.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAPI(retrofit: Retrofit): PostItAPI =
        retrofit.create(PostItAPI::class.java)
}