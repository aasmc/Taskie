package ru.aasmc.taskie.networking

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.aasmc.taskie.App
import ru.aasmc.taskie.model.Task
import ru.aasmc.taskie.model.UserProfile
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
    private val gson = Gson()

    fun loginUser(userDataRequest: UserDataRequest, onUserLoggedIn: (String?, Throwable?) -> Unit) {
        apiService.loginUser(userDataRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {

                val loginResponse = response.body()
                if (loginResponse == null || loginResponse.token.isNullOrBlank()) {
                    onUserLoggedIn(null, NullPointerException("No response body!"))
                } else {
                    onUserLoggedIn(loginResponse.token, null)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onUserLoggedIn(null, t)
            }
        })
    }

    fun registerUser(
        userDataRequest: UserDataRequest,
        onUserCreated: (String?, Throwable?) -> Unit
    ) {

        apiService.register(userDataRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                val message = response.body()?.message
                if (message == null) {
                    onUserCreated(null, NullPointerException("No response body!"))
                    return
                }
                onUserCreated(message, null)
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                onUserCreated(null, t)
            }
        })
    }

    fun getTasks(onTasksReceived: (List<Task>, Throwable?) -> Unit) {
        apiService.getNotes(App.getToken()).enqueue(object : Callback<GetTasksResponse> {
            override fun onResponse(call: Call<GetTasksResponse>, response: Response<GetTasksResponse>) {

                val data = response.body()
                if (data != null && data.notes.isNotEmpty()) {
                    onTasksReceived(data.notes.filter { !it.isCompleted }, null)
                } else {
                    onTasksReceived(emptyList(), NullPointerException("No data available!"))
                }
            }

            override fun onFailure(call: Call<GetTasksResponse>, t: Throwable) {
                onTasksReceived(emptyList(), t)
            }

        })
    }

    fun deleteTask(onTaskDeleted: (Throwable?) -> Unit) {
        onTaskDeleted(null)
    }

    fun completeTask(taskId: String, onTaskCompleted: (Throwable?) -> Unit) {
        apiService.completeTask(App.getToken(), taskId).enqueue(object : Callback<CompleteNoteResponse> {
            override fun onResponse(call: Call<CompleteNoteResponse>, response: Response<CompleteNoteResponse>) {
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

    fun addTask(addTaskRequest: AddTaskRequest, onTaskCreated: (Task?, Throwable?) -> Unit) {

        apiService.addTask(App.getToken(), addTaskRequest).enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                val data = response.body()
                if (data == null) {
                    onTaskCreated(null, NullPointerException("No response!"))
                } else {
                    onTaskCreated(data, null)
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                onTaskCreated(null, t)
            }
        })
    }

    fun getUserProfile(onUserProfileReceived: (UserProfile?, Throwable?) -> Unit) {
        getTasks { list, error ->
            if (error != null && error !is NullPointerException) {
                onUserProfileReceived(null, error)
                return@getTasks
            }
            apiService.getMyProfile(App.getToken()).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val jsonBody = response.body()?.string()
                    if (jsonBody == null) {
                        onUserProfileReceived(null, error)
                        return
                    }
                    val userProfileResponse =
                        gson.fromJson(jsonBody, UserProfileResponse::class.java)
                    if (userProfileResponse.email == null || userProfileResponse.name == null) {
                        onUserProfileReceived(null, error)
                    } else {
                        onUserProfileReceived(
                            UserProfile(
                                userProfileResponse.email,
                                userProfileResponse.name,
                                list.size
                            ),
                            null
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    onUserProfileReceived(null, t)
                }

            })
        }
    }
}