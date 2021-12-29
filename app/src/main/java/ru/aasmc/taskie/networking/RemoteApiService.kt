package ru.aasmc.taskie.networking

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.aasmc.taskie.model.Task
import ru.aasmc.taskie.model.request.AddTaskRequest
import ru.aasmc.taskie.model.request.UserDataRequest
import ru.aasmc.taskie.model.response.CompleteNoteResponse
import ru.aasmc.taskie.model.response.GetTasksResponse
import ru.aasmc.taskie.model.response.LoginResponse
import ru.aasmc.taskie.model.response.RegisterResponse

interface RemoteApiService {

    @POST("/api/register")
    fun register(@Body request: UserDataRequest): Call<RegisterResponse>

    @GET("/api/note")
    fun getNotes(@Header("Authorization") token: String): Call<GetTasksResponse>

    @POST("/api/login")
    fun loginUser(@Body request: UserDataRequest): Call<LoginResponse>

    @GET("/api/user/profile")
    fun getMyProfile(@Header("Authorization") token: String): Call<ResponseBody>

    @POST("/api/note/complete")
    fun completeTask(
        @Header("Authorization") token: String,
        @Query("id") noteId: String
    ): Call<CompleteNoteResponse>

    @POST("/api/note")
    fun addTask(
        @Header("Authorization") token: String,
        @Body request: AddTaskRequest
    ): Call<Task>

}