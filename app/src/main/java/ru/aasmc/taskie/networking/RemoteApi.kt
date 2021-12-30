package ru.aasmc.taskie.networking

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

    fun getTasks(onTasksReceived: (Result<List<Task>>) -> Unit) {
        apiService.getNotes().enqueue(object : Callback<GetTasksResponse> {
            override fun onResponse(
                call: Call<GetTasksResponse>,
                response: Response<GetTasksResponse>
            ) {

                val data = response.body()
                if (data != null && data.notes.isNotEmpty()) {
                    onTasksReceived(Success(data.notes.filter { !it.isCompleted }))
                } else {
                    onTasksReceived(Failure (NullPointerException("No data available!")))
                }
            }

            override fun onFailure(call: Call<GetTasksResponse>, t: Throwable) {
                onTasksReceived(Failure(t))
            }

        })
    }

    fun deleteTask(onTaskDeleted: (Throwable?) -> Unit) {
        onTaskDeleted(null)
    }

    fun completeTask(taskId: String, onTaskCompleted: (Throwable?) -> Unit) {
        apiService.completeTask(taskId)
            .enqueue(object : Callback<CompleteNoteResponse> {
                override fun onResponse(
                    call: Call<CompleteNoteResponse>,
                    response: Response<CompleteNoteResponse>
                ) {
                    val completeNoteResponse = response.body()
                    if (completeNoteResponse?.message == null) {
                        onTaskCompleted(NullPointerException("No response!"))
                    } else {
                        onTaskCompleted(null)
                    }
                }

                override fun onFailure(call: Call<CompleteNoteResponse>, t: Throwable) {
                    onTaskCompleted(t)
                }
            })
    }

    fun addTask(addTaskRequest: AddTaskRequest, onTaskCreated: (Result<Task>) -> Unit) {

        apiService.addTask(addTaskRequest).enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                val data = response.body()
                if (data == null) {
                    onTaskCreated(Failure (NullPointerException("No response!")))
                } else {
                    onTaskCreated(Success(data))
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                onTaskCreated(Failure(t))
            }
        })
    }

    fun getUserProfile(onUserProfileReceived: (Result<UserProfile>) -> Unit) {
        getTasks { result ->
            if (result is Failure && result.error !is NullPointerException) {
                onUserProfileReceived(Failure(result.error))
                return@getTasks
            }
            val tasks = (result as Success).data
            apiService.getMyProfile().enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    val userProfileResponse =
                        response.body()
                    if (userProfileResponse?.email == null || userProfileResponse.name == null) {
                        onUserProfileReceived(Failure(NullPointerException("No data!")))
                    } else {

                        onUserProfileReceived(
                            Success(
                                UserProfile(
                                    userProfileResponse.email,
                                    userProfileResponse.name,
                                    tasks.size
                                )
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    onUserProfileReceived(Failure(t))
                }
            })
        }
    }
}