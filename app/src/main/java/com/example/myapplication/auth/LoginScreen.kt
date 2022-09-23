package com.example.myapplication.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.ActivityLoginScreenBinding
import com.example.myapplication.models.UserRequest
import com.example.myapplication.util.Helper
import com.example.myapplication.util.NetworkResult
import com.example.myapplication.util.TokenManager
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginScreen : AppCompatActivity() {
    private val authViewModel : AuthViewModel by viewModels()

    private lateinit var binding: ActivityLoginScreenBinding

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (tokenManager.getToken() != null) {
            val intent = Intent(Intent(applicationContext, MainActivity::class.java))
            startActivity(intent)
            finish()
        }

        binding.signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpScreen::class.java)
            startActivity(intent)
            finish()
        }

        binding.forgotPasswor.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
            finish()
        }


        binding.signInBtnId.setOnClickListener {

           /* Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                val userRequest = getUserRequest()
                authViewModel.loginUser(userRequest)
            } else {
                showValidationErrors(validationResult.second)
            }*/
        Validatin()

        }
        bindObservers()

    }

    private fun getUserRequest(): UserRequest {
        return binding.run {
            UserRequest(
                loginEmailId.text.toString(),
                "",
                "",
                loginPasswordId.text.toString(),
            )
        }
    }

    private fun validation(){
        val emailAddress = binding.loginEmailId.text.toString()
        val password = binding.loginPasswordId.text.toString()

        if (emailAddress.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
            binding.loginEmailId.setError("Enter your correct email")
        }
        if (tokenManager.getToken() == null){
            binding.loginEmailId.setError("The email address or password invalid")
        }

         if (password.isEmpty() && password.length < 6 ){
            binding.loginPasswordId.setError("Enter your password above 6 digits")
        }else{
             val userRequest = getUserRequest()
             authViewModel.loginUser(userRequest)
         }

    }

    private fun showValidationErrors(error: String) {
       // binding.txtError.text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(applicationContext, "error, $error", Toast.LENGTH_LONG).show()
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val emailAddress = binding.loginEmailId.text.toString()
        val password = binding.loginPasswordId.text.toString()
        return authViewModel.validateCredentials(emailAddress, "" ,"", password, true)
    }

    private fun bindObservers() {
        authViewModel.userResponseLiveData.observe(this, Observer {
           // binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                   // findNavController().navigate(R.id.action_loginFragment_to_loginMain)
                    val intent = Intent(this, MainActivity::class.java)
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
    }

    private fun Validatin(){

        val email = binding.loginEmailId.text.toString()
        val password = binding.loginPasswordId.text.toString()

         if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showError(binding.loginEmailId,"Email is not valid")
        }
        else if (password.isEmpty() || password.length <6){
            showError(binding.loginPasswordId,"Password must be 6 character")
        }
        else{
            val userRequest = getUserRequest()
            authViewModel.loginUser(userRequest)
            Toast.makeText(applicationContext, "Loging successfull", Toast.LENGTH_LONG).show()
        }

    }

    private fun showError(error: TextInputEditText, s: String) {
        error.setError(s)
        error.requestFocus()
    }


}