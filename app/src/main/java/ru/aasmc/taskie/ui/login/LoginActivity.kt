package ru.aasmc.taskie.ui.login

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.aasmc.taskie.App
import ru.aasmc.taskie.databinding.ActivityLoginBinding
import ru.aasmc.taskie.model.request.UserDataRequest
import ru.aasmc.taskie.networking.NetworkStatusChecker
import ru.aasmc.taskie.networking.RemoteApi
import ru.aasmc.taskie.ui.main.MainActivity
import ru.aasmc.taskie.ui.register.RegisterActivity
import ru.aasmc.taskie.utils.gone
import ru.aasmc.taskie.utils.visible

/**
 * Displays the Login screen, with the options to head over to the Register screen.
 */
class LoginActivity : AppCompatActivity() {

    private val networkStatusChecker by lazy {
        NetworkStatusChecker(getSystemService(ConnectivityManager::class.java))
    }

    private val remoteApi = RemoteApi()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initUi()
    }

    private fun initUi() {
        binding.login.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                logUserIn(UserDataRequest(email, password))
            } else {
                showLoginError()
            }
        }
        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun logUserIn(userDataRequest: UserDataRequest) {
        networkStatusChecker.performIfConnectedToInternet {
            remoteApi.loginUser(userDataRequest) { token: String?, throwable: Throwable? ->
                runOnUiThread {
                    if (token != null && token.isNotBlank()) {
                        onLoginSuccess(token)
                    } else if (throwable != null) {
                        showLoginError()
                    }
                }
            }
        }
    }

    private fun onLoginSuccess(token: String) {
        binding.errorText.gone()
        App.saveToken(token)
        startActivity(MainActivity.getIntent(this))
    }

    private fun showLoginError() {
        binding.errorText.visible()
    }
}