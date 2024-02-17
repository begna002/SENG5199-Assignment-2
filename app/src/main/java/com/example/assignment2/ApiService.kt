package com.example.assignment2

import android.util.Log
import androidx.compose.ui.res.stringResource
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

suspend fun getData(apiKey: String, startDateString: String, endDateString: String): List<ResponseItem> {
    try {
        val url = "https://api.nasa.gov/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        System.out.println("Start: $startDateString, End: $endDateString")
        // Add Try Catch
        return service.getAPOD(
            apiKey = apiKey,
            startDate = startDateString,
            endDate = endDateString
        )
    } catch (ex: Exception) {
        Log.e("Exception", "Error calling Nasa API: $ex")
    }
    return listOf(ResponseItem("", "", "", true))
}

interface ApiService {
    @GET("planetary/apod")
    suspend fun getAPOD(@Query("api_key") apiKey: String,
                        @Query("start_date") startDate: String,
                        @Query("end_date") endDate: String,): List<ResponseItem>
}

data class ResponseItem(
    @SerializedName("url")
    val url: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("date")
    val date: String,

    val error: Boolean
)