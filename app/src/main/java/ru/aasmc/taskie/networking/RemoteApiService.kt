package ru.aasmc.taskie.networking

import retrofit2.Call
import retrofit2.http.*
import ru.aasmc.taskie.model.Task
import ru.aasmc.taskie.model.request.AddTaskRequest
import ru.aasmc.taskie.model.request.UserDataRequest
import ru.aasmc.taskie.model.response.*

interface RemoteApiService {

    @POST("/api/register")
    fun register(@Body request: UserDataRequest): Call<RegisterResponse>

    @GET("/api/note")
    fun getNotes(): Call<GetTasksResponse>

    @POST("/api/login")
    fun loginUser(@Body request: UserDataRequest): Call<LoginResponse>

    @GET("/api/user/profile")
    fun getMyProfile(): Call<UserProfileResponse>

    @POST("/api/note/complete")
    fun completeTask(
        @Query("id") noteId: String
    ): Call<CompleteNoteResponse>

    @POST("/api/note")
    fun addTask(
        @Body request: AddTaskRequest
    ): Call<Task>

    @DELETE("/api/note")
    fun deleteNote(@Query("id") noteId: String): Call<DeleteNoteResponse>
}