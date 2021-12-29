package ru.aasmc.taskie.networking

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.aasmc.taskie.App
import ru.aasmc.taskie.model.Task
import ru.aasmc.taskie.model.UserProfile
import ru.aasmc.taskie.model.request.AddTaskRequest
import ru.aasmc.taskie.model.request.UserDataRequest
import ru.aasmc.taskie.model.response.CompleteNoteResponse
import ru.aasmc.taskie.model.response.GetTasksResponse
import ru.aasmc.taskie.model.response.LoginResponse
import ru.aasmc.taskie.model.response.UserProfileResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

/**
 * Holds decoupled logic for all the API calls.
 */

const val BASE_URL = "https://taskie-rw.herokuapp.com"

class RemoteApi(
    private val apiService: RemoteApiService
) {
    private val gson = Gson()

    fun loginUser(userDataRequest: UserDataRequest, onUserLoggedIn: (String?, Throwable?) -> Unit) {
        val body = RequestBody.create(
            MediaType.parse("application/json"), gson.toJson(userDataRequest)
        )
        apiService.loginUser(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val message = response.body()?.string()
                if (message == null) {
                    onUserLoggedIn(null, NullPointerException("No response body!"))
                    return
                }
                val loginResponse = gson.fromJson(message, LoginResponse::class.java)
                if (loginResponse == null || loginResponse.token.isNullOrBlank()) {
                    onUserLoggedIn(null, NullPointerException("No response body!"))
                } else {
                    onUserLoggedIn(loginResponse.token, null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onUserLoggedIn(null, t)
            }
        })
    }

    fun registerUser(
        userDataRequest: UserDataRequest,
        onUserCreated: (String?, Throwable?) -> Unit
    ) {
        val body = RequestBody.create(
            MediaType.parse("application/json"), gson.toJson(userDataRequest)
        )
        apiService.register(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val message = response.body()?.string()
                if (message == null) {
                    onUserCreated(null, NullPointerException("No response body!"))
                    return
                }
                onUserCreated(message, null)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onUserCreated(null, t)
            }
        })
    }

    fun getTasks(onTasksReceived: (List<Task>, Throwable?) -> Unit) {
        apiService.getNotes(App.getToken()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val jsonBody = response.body()?.string()
                if (jsonBody == null) {
                    onTasksReceived(emptyList(), NullPointerException("No data available!"))
                    return
                }
                val data = gson.fromJson(jsonBody, GetTasksResponse::class.java)
                if (data != null && data.notes.isNotEmpty()) {
                    onTasksReceived(data.notes.filter { !it.isCompleted }, null)
                } else {
                    onTasksReceived(emptyList(), NullPointerException("No data available!"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onTasksReceived(emptyList(), t)
            }

        })
    }

    fun deleteTask(onTaskDeleted: (Throwable?) -> Unit) {
        onTaskDeleted(null)
    }

    fun completeTask(taskId: String, onTaskCompleted: (Throwable?) -> Unit) {
        apiService.completeTask(App.getToken(), taskId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val jsonBody = response.body()?.string()
                if (jsonBody == null) {
                    onTaskCompleted(NullPointerException("No response!"))
                    return
                }
                val completeNoteResponse = gson.fromJson(jsonBody, CompleteNoteResponse::class.java)
                if (completeNoteResponse?.message == null) {
                    onTaskCompleted(NullPointerException("No response!"))
                } else {
                    onTaskCompleted(null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onTaskCompleted(t)
            }
        })
    }

    fun addTask(addTaskRequest: AddTaskRequest, onTaskCreated: (Task?, Throwable?) -> Unit) {
        val body = RequestBody.create(
            MediaType.parse("application/json"), gson.toJson(addTaskRequest)
        )
        apiService.addTask(App.getToken(), body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val jsonBody = response.body()?.string()
                if (jsonBody == null) {
                    onTaskCreated(null, NullPointerException("No response!"))
                    return
                }
                val data = gson.fromJson(jsonBody, Task::class.java)
                if (data == null) {
                    onTaskCreated(null, NullPointerException("No response!"))
                } else {
                    onTaskCreated(data, null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
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