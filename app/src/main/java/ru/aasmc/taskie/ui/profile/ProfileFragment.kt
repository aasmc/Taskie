package ru.aasmc.taskie.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.aasmc.taskie.App
import ru.aasmc.taskie.R
import ru.aasmc.taskie.databinding.FragmentProfileBinding
import ru.aasmc.taskie.model.Success
import ru.aasmc.taskie.ui.login.LoginActivity

/**
 * Displays the user profile information.
 */
class ProfileFragment : Fragment() {

    private val remoteApi = App.remoteApi

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()

        remoteApi.getUserProfile { result ->
            if (result is Success) {
                val userProfile = result.data
                binding.userEmail.text = userProfile.email
                binding.userName.text = getString(R.string.user_name_text, userProfile.name)
                binding.numberOfNotes.text =
                    getString(R.string.number_of_notes_text, userProfile.numberOfNotes)
            }
        }
    }

    private fun initUi() {
        binding.logOut.setOnClickListener {
            App.saveToken("")
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}