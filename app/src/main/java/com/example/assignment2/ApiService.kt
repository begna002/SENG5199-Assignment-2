package com.example.assignment2

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

suspend fun getData(startDateString: String, endDateString: String): List<ResponseItem> {
    val url = "https://api.nasa.gov/"
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(ApiService::class.java)
    System.out.println("Start: $startDateString, End: $endDateString")
    return service.getAPOD(startDate = startDateString, endDate = endDateString)
}

interface ApiService {
    @GET("planetary/apod?api_key=wWJsUYWXItBxQZLZr6kMBqeZMSYqBMaim7WtRqC0")
    suspend fun getAPOD(@Query("start_date") startDate: String,
                        @Query("end_date") endDate: String,): List<ResponseItem>
}

data class ResponseItem(
    @SerializedName("url")
    val url: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("date")
    val date: String

)