package onyok.tope.devexam

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/submit")
    fun submitUserData(@Body userData: UserData): Call<Void>
}