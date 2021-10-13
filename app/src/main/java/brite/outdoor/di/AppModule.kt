package brite.outdoor.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import brite.outdoor.constants.ApiConst
import brite.outdoor.data.local.room.AppDatabase
import brite.outdoor.data.remote.ApiController
import brite.outdoor.data.remote.CallApiService
import brite.outdoor.utils.AppUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    const val TIMEOUT = 60

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        val client: OkHttpClient = OkHttpClient.Builder()
//                .addInterceptor(interceptor)
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .build()
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder.header("Content-Type", "application/json")
                requestBuilder.header("Accept", "application/json")
                requestBuilder.header("charset", "UTF-8")

                return chain.proceed(requestBuilder.build())
            }
        })
        return Retrofit.Builder()
                .baseUrl(ApiConst.DOMAIN_NAME)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClientBuilder.build())
                .client(client)
                .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().serializeNulls()
            .setLenient()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(AppUtils.Companion.NullStringToEmptyAdapterFactory<Any?>()).create()

    @Provides
    fun provideCharacterService(retrofit: Retrofit): CallApiService = retrofit.create(CallApiService::class.java)

    @Provides
    fun provideApiController(apiService: CallApiService): ApiController = ApiController(apiService)

    //ROOM
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext : Context) = AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideAppDap(database: AppDatabase) = database.appDao()


}