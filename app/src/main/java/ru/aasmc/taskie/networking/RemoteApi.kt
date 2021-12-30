package ru.aasmc.taskie.networking

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.aasmc.taskie.model.*
import ru.aasmc.taskie.model.request.AddTaskRequest
import ru.aasmc.taskie.model.request.UserDataRequest
import ru.aasmc.taskie.model.response.*

/**
 * Holds decoupled logic for all the API calls.
 */

const val BASE_URL = "https://taskie-rw.herokuapp.com"

class RemoteApi(
    private val apiService: RemoteApiService
) {

    fun loginUser(userDataRequest: UserDataRequest, onUserLoggedIn: (Result<String>) -> Unit) {
        apiService.loginUser(userDataRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                val loginResponse = response.body()
                if (loginResponse == null || loginResponse.token.isNullOrBlank()) {
                    onUserLoggedIn(Failure(NullPointerException("No response body!")))
                } else {
                    onUserLoggedIn(Success(loginResponse.token))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onUserLoggedIn(Failure(t))
            }
        })
    }

    fun registerUser(
        userDataRequest: UserDataRequest,
        onUserCreated: (Result<String>) -> Unit
    ) {

        apiService.register(userDataRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                val message = response.body()?.message
                if (message == null) {
                    onUserCreated(Failure(NullPointerException("No response body!")))
                    return
                }
                onUserCreated(Success(message))
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                onUserCreated(Failure(t))
            }
        })
    }

    suspend fun getTasks(): Result<List<Task>> = try {
        val data = apiService.getNotes()
        Success(data.notes.filter { !it.isCompleted })
    } catch (e: Throwable) {
        Failure(e)
    }

    suspend fun deleteTask(taskId: String): Result<String> =
        try {
            val data = apiService.deleteNote(taskId)
            Success(data.message)
        } catch (e: Throwable) {
            Failure(e)
        }


    suspend fun completeTask(taskId: String): Result<String> = try {
        val data = apiService.completeTask(taskId)
        Success(data.message!!)
    } catch (e: Throwable) {
        Failure(e)
    }

    suspend fun addTask(addTaskRequest: AddTaskRequest): Result<Task> = try {
        val data = apiService.addTask(addTaskRequest)
        Success(data)
    } catch (e: Throwable) {
        Failure(e)
    }

    suspend fun getUserProfile(): Result<UserProfile> = try {
        val notesResult = getTasks()
        if (notesResult is Failure) {
            Failure(notesResult.error)
        } else {
            val notes = notesResult as Success
            val data = apiService.getMyProfile()
            if (data.email == null || data.name == null) {
                Failure(NullPointerException("No data available!"))
            } else {
                Success(
                    UserProfile(
                        data.email,
                        data.name,
                        notes.data.size
                    )
                )
            }
        }
    } catch (e: Throwable) {
        Failure(e)
    }
}