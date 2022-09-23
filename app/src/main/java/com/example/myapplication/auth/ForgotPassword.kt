package com.example.myapplication.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.myapplication.databinding.ActivityForgotPasswordBinding
import com.example.myapplication.models.ForgotPass
import com.example.myapplication.util.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class ForgotPassword : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val authViewModel : AuthViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.forgotPassEmailBtnId.setOnClickListener {

            /*Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                val userRequest = getUserRequest()
                authViewModel.forgotPassword(userRequest)
            } else {
                showValidationErrors(validationResult.second)
            }*/

            val userRequest = getUserRequest()
            authViewModel.forgotPassword(userRequest)
        }
       // bindObservers()
    }


    private fun getUserRequest(): ForgotPass {
        return binding.run {
            ForgotPass(
                forgotPasswordEmailId.text.toString()
            )
        }
    }

    private fun showValidationErrors(error: String) {
          binding.errroIds.text = error.toString()
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val emailAddress = binding.forgotPasswordEmailId.text.toString()
        return authViewModel.validateCredentials(emailAddress, "" ,"", "", true)
    }

 /*   private fun bindObservers() {
        authViewModel.userResponseLiveData.observe(this, Observer {
            // binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    // findNavController().navigate(R.id.action_loginFragment_to_loginMain)
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    finish()
                }
                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }
                is NetworkResult.Loading ->{
                    // binding.progressBar.isVisible = true
                }
            }
        })
    }*/



}