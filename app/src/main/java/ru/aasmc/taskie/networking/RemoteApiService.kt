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
    suspend fun getNotes(): GetTasksResponse

    @POST("/api/login")
    fun loginUser(@Body request: UserDataRequest): Call<LoginResponse>

    @GET("/api/user/profile")
    suspend fun getMyProfile(): UserProfileResponse

    @POST("/api/note/complete")
    suspend fun completeTask(
        @Query("id") noteId: String
    ): CompleteNoteResponse

    @POST("/api/note")
    suspend fun addTask(
        @Body request: AddTaskRequest
    ): Task

    @DELETE("/api/note")
    suspend fun deleteNote(@Query("id") noteId: String): DeleteNoteResponse
}