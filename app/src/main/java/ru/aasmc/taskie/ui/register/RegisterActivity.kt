package ru.aasmc.taskie.ui.register

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.aasmc.taskie.App
import ru.aasmc.taskie.databinding.ActivityRegisterBinding
import ru.aasmc.taskie.model.Success
import ru.aasmc.taskie.model.request.UserDataRequest
import ru.aasmc.taskie.networking.NetworkStatusChecker
import ru.aasmc.taskie.utils.gone
import ru.aasmc.taskie.utils.toast
import ru.aasmc.taskie.utils.visible

/**
 * Displays the Register screen, with the options to register, or head over to Login!
 */
class RegisterActivity : AppCompatActivity() {

    private val networkStatusChecker by lazy {
        NetworkStatusChecker(getSystemService(ConnectivityManager::class.java))
    }
    private val remoteApi = App.remoteApi

    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi() {
        binding.register.setOnClickListener {
            processData(
                binding.nameInput.text.toString(), binding.emailInput.text.toString(),
                binding.passwordInput.text.toString()
            )
        }
    }

    private fun processData(username: String, email: String, password: String) {
        if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            networkStatusChecker.performIfConnectedToInternet {
                remoteApi.registerUser(
                    UserDataRequest(
                        email,
                        password,
                        username
                    )
                ) { result ->
                    if (result is Success) {
                        toast(result.data)
                        onRegisterSuccess()
                    } else  {
                        onRegisterError()
                    }
                    
                }
            }
        } else {
            onRegisterError()
        }
    }

    private fun onRegisterSuccess() {
        binding.errorText.gone()
        finish()
    }

    private fun onRegisterError() {
        binding.errorText.visible()
    }
}