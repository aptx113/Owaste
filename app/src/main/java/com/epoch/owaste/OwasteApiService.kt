package com.epoch.owaste

import com.epoch.owaste.data.PlaceDetailsResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val HOST_NAME = "maps.googleapis.com/maps"
private const val BASE_URL = "https://$HOST_NAME/api/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 *
 * implementation "com.squareup.moshi:moshi:1.8.0"
 * implementation "com.squareup.moshi:moshi-kotlin:1.8.0"
 * implementation "com.squareup.retrofit2:converter-moshi:2.5.0"
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Build the OkHttpClient object that Retrofit will be using, making sure to add the logging interceptor for
 * check response. Setup level to Level.BODY that we will know all information about http connect.
 *
 * implementation("com.squareup.okhttp3:logging-interceptor:4.0.1")
 */
private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 *
 * And using an OkHttpClient with our OkHttpClient object.
 *
 * implementation "com.squareup.retrofit2:retrofit:2.5.0"
 * implementation "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .client(client)
    .build()

/**
 * A public interface that exposes methods
 */
interface OwasteApiService {

    @GET("place/details/json")
    fun getPlaceDetailsAsync(@Query("place_id") placeId: String, @Query("fields") fields: String, @Query("key") key: String):
            Deferred<PlaceDetailsResult>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object OwasteApi {
    val retrofitService : OwasteApiService by lazy { retrofit.create(OwasteApiService::class.java) }
}