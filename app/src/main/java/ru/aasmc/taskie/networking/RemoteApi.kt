package ru.aasmc.taskie.networking

import com.google.gson.Gson
import org.json.JSONObject
import ru.aasmc.taskie.App
import ru.aasmc.taskie.model.Task
import ru.aasmc.taskie.model.UserProfile
import ru.aasmc.taskie.model.request.AddTaskRequest
import ru.aasmc.taskie.model.request.UserDataRequest
import ru.aasmc.taskie.model.response.GetTasksResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

/**
 * Holds decoupled logic for all the API calls.
 */

const val BASE_URL = "https://taskie-rw.herokuapp.com"

class RemoteApi {
    private val gson = Gson()

    fun loginUser(userDataRequest: UserDataRequest, onUserLoggedIn: (String?, Throwable?) -> Unit) {
        Thread(Runnable {
            val connection = URL("$BASE_URL/api/login").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.readTimeout = 10000
            connection.connectTimeout = 10000
            connection.doOutput = true
            connection.doInput = true

            val body = gson.toJson(userDataRequest)

            val bytes = body.toByteArray()

            try {
                connection.outputStream.use { out ->
                    out.write(bytes)
                }

                val reader = InputStreamReader(connection.inputStream)
                reader.use { input ->
                    val response = StringBuilder()
                    val bufferedReader = BufferedReader(input)
                    bufferedReader.useLines { lines ->
                        lines.forEach {
                            response.append(it.trim())
                        }
                    }
                    val jsonObject = JSONObject(response.toString())
                    val token = jsonObject.getString("token")
                    onUserLoggedIn(token, null)
                }
            } catch (e: Throwable) {
                onUserLoggedIn(null, e)
            } finally {
                connection.disconnect()
            }
        }).start()
    }

    fun registerUser(
        userDataRequest: UserDataRequest,
        onUserCreated: (String?, Throwable?) -> Unit
    ) {
        Thread(Runnable {
            val connection = URL("$BASE_URL/api/register").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.readTimeout = 10000
            connection.connectTimeout = 10000
            connection.doOutput = true
            connection.doInput = true

            val body = gson.toJson(userDataRequest)

            val bytes = body.toByteArray()
            try {
                connection.outputStream.use { out ->
                    out.write(bytes)
                }

                val reader = InputStreamReader(connection.inputStream)
                reader.use { input ->
                    val response = StringBuilder()
                    val bufferedReader = BufferedReader(input)
                    bufferedReader.useLines { lines ->
                        lines.forEach {
                            response.append(it.trim())
                        }
                    }
                    val jsonObject = JSONObject(response.toString())

                    onUserCreated(jsonObject.getString("message"), null)
                }
            } catch (e: Throwable) {
                onUserCreated(null, e)
            } finally {
                connection.disconnect()
            }
        }).start()
    }

    fun getTasks(onTasksReceived: (List<Task>, Throwable?) -> Unit) {
        Thread(Runnable {
            val connection = URL("$BASE_URL/api/note").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", App.getToken())
            connection.readTimeout = 10000
            connection.connectTimeout = 10000
            connection.doOutput = true
            connection.doInput = true

            try {
                val reader = InputStreamReader(connection.inputStream)
                reader.use { input ->
                    val response = StringBuilder()
                    val bufferedReader = BufferedReader(input)
                    bufferedReader.useLines { lines ->
                        lines.forEach {
                            response.append(it.trim())
                        }
                    }
                    val tasksResponse =
                        gson.fromJson(response.toString(), GetTasksResponse::class.java)
                    onTasksReceived(tasksResponse.notes.filter { !it.isCompleted }, null)
                }
            } catch (e: Throwable) {
                onTasksReceived(emptyList(), e)
            } finally {
                connection.disconnect()
            }
        }).start()
    }

    fun deleteTask(onTaskDeleted: (Throwable?) -> Unit) {
        onTaskDeleted(null)
    }

    fun completeTask(taskId: String, onTaskCompleted: (Throwable?) -> Unit) {
        Thread(Runnable {
            val connection =
                URL("$BASE_URL/api/note/complete?id=$taskId").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", App.getToken())
            connection.readTimeout = 10000
            connection.connectTimeout = 10000
            connection.doOutput = true
            connection.doInput = true

            try {
                val reader = InputStreamReader(connection.inputStream)
                reader.use { input ->
                    val response = StringBuilder()
                    val bufferedReader = BufferedReader(input)
                    bufferedReader.useLines { lines ->
                        lines.forEach {
                            response.append(it.trim())
                        }
                    }
                }
                onTaskCompleted(null)
            } catch (e: Throwable) {
                onTaskCompleted(e)
            } finally {
                connection.disconnect()
            }

        }).start()
    }

    fun addTask(addTaskRequest: AddTaskRequest, onTaskCreated: (Task?, Throwable?) -> Unit) {
        Thread(Runnable {
            val connection = URL("$BASE_URL/api/note").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", App.getToken())
            connection.readTimeout = 10000
            connection.connectTimeout = 10000
            connection.doOutput = true
            connection.doInput = true

            val body = gson.toJson(addTaskRequest)

            val bytes = body.toByteArray()
            try {
                connection.outputStream.use { out ->
                    out.write(bytes)
                }

                val reader = InputStreamReader(connection.inputStream)
                reader.use { input ->
                    val response = StringBuilder()
                    val bufferedReader = BufferedReader(input)
                    bufferedReader.useLines { lines ->
                        lines.forEach {
                            response.append(it.trim())
                        }
                    }
                    val jsonObject = JSONObject(response.toString())
                    val task = Task(
                        jsonObject.getString("id"),
                        jsonObject.getString("title"),
                        jsonObject.getString("content"),
                        jsonObject.getBoolean("isCompleted"),
                        jsonObject.getInt("taskPriority")
                    )
                    onTaskCreated(task, null)
                }
            } catch (e: Throwable) {
                onTaskCreated(null, e)
            } finally {
                connection.disconnect()
            }
        }).start()
    }

    fun getUserProfile(onUserProfileReceived: (UserProfile?, Throwable?) -> Unit) {
        onUserProfileReceived(UserProfile("mail@mail.com", "Filip", 10), null)
    }
}